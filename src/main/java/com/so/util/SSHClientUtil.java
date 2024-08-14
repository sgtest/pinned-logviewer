package com.so.util;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.sftp.FileAttributes;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.xfer.FileSystemFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class SSHClientUtil {

    private static final Logger log = LoggerFactory.getLogger(SSHClientUtil.class);

    private String ip, username, password;
    private Integer port;
    private SSHClient sshClient;
    private SFTPClient sftpClient;

    // 构造函数初始化连接参数
    public SSHClientUtil(String ip, Integer port, String username, String password) {
        this.ip = ip;
        this.username = username;
        this.password = password;
        this.port = port;
    }

    /**
     * 使用秘钥链接
     *
     * @param host
     * @param port
     * @param privateKeyPath
     * @throws IOException
     */
    public SSHClientUtil(String host, int port, String privateKeyPath) throws IOException {
        sshClient = new SSHClient();
        // 仅用于测试，生产环境中请使用更安全的HostKeyVerifier实现
        sshClient.addHostKeyVerifier(new PromiscuousVerifier());
        sshClient.connect(host, port);
        sshClient.authPublickey(username, privateKeyPath);
        sshClient.setConnectTimeout(30000);
        // 设置读取超时时间
        sshClient.setTimeout(1200000);
        sshClient.getConnection().getKeepAlive().setKeepAliveInterval(5);
        log.info("Connected to {}", ip);
//        sshClient.authPublickey(System.getProperty("user.name"), new PKCS8KeyFile(new File(privateKeyPath)));
    }

    // 打开SSH连接
    public void openConnection() throws IOException {
        sshClient = new SSHClient();
        sshClient.addHostKeyVerifier(new PromiscuousVerifier());
        sshClient.connect(ip, port);
        sshClient.authPassword(username, password);
        sshClient.setConnectTimeout(30000);
        // 设置读取超时时间
        sshClient.setTimeout(1200000);
        sshClient.getConnection().getKeepAlive().setKeepAliveInterval(5);
        log.info("Connected to {}", ip);
    }

    // 关闭SSH连接
    public void closeConnection() {
        if (sshClient != null) {
            try {
                sshClient.disconnect();
                sshClient.close();
                log.info("Connection closed");
            } catch (IOException e) {
                log.error("Error closing SSH connection: {}", e.getMessage());
            }
        }
        if (sftpClient != null) {
            try {
                sftpClient.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // 执行单条命令
    public String executeCommand(String command) throws IOException {
        try (Session startSession = sshClient.startSession()) {
            try (Session.Command cmd = startSession.exec(command)) {
                String output = IOUtils.readFully(cmd.getInputStream()).toString(); // 读取命令输出
                cmd.join(5, TimeUnit.SECONDS); // 等待命令执行完成
                log.info("Executed command: {}. Output: {}", command, output);
                return output;
            }
        }
    }

    // 执行多条命令
    public void executeCommands(List<String> commands) throws IOException {
        for (String command : commands) {
            executeCommand(command);
        }
    }

    // 上传文件并可选验证哈希值
    public boolean uploadFile(String localFilePath, String remoteFilePath, String localFileHash) throws IOException {
        getSftpClient().put(new FileSystemFile(localFilePath), remoteFilePath); // 上传文件
        log.info("Uploaded file from {} to {}", localFilePath, remoteFilePath);
        if (localFileHash != null && !localFileHash.isEmpty()) {
            return verifyRemoteFileHash(remoteFilePath, localFileHash); // 验证远程文件哈希值
        }
        return true;
    }

    // 下载文件并验证文件大小
    public boolean downloadFile(String remoteFilePath, String localFilePath) throws IOException {
        getSftpClient().get(remoteFilePath, new FileSystemFile(localFilePath)); // 下载文件
        log.info("Downloaded file from {} to {}", remoteFilePath, localFilePath);
        return verifyFileSize(localFilePath, remoteFilePath); // 验证文件大小
    }

    // 验证远程文件哈希值
    public boolean verifyRemoteFileHash(String remoteFilePath, String localFileHash) throws IOException {
        String remoteCommand = String.format("md5sum %s | awk '{ print $1 }'", remoteFilePath);
        String remoteFileHash = executeCommand(remoteCommand).trim(); // 执行远程命令获取哈希值

        boolean result = localFileHash.equals(remoteFileHash); // 比较哈希值
        if (result) {
            log.info("File hash verification successful for file: {}", remoteFilePath);
        } else {
            log.error("File hash mismatch for file: {}. Local hash: {}, Remote hash: {}", remoteFilePath, localFileHash, remoteFileHash);
        }
        return result;
    }

    // 验证文件大小
    private boolean verifyFileSize(String localFilePath, String remoteFilePath) throws IOException {
        File localFile = new File(localFilePath);
        long localFileSize = localFile.length(); // 获取本地文件大小

        FileAttributes remoteFile = getSftpClient().stat(remoteFilePath);
        long remoteFileSize = remoteFile.getSize(); // 获取远程文件大小

        boolean result = localFileSize == remoteFileSize; // 比较文件大小
        if (result) {
            log.info("File size verification successful for file: {}", localFilePath);
        } else {
            log.error("File size mismatch for file: {}. Local size: {}, Remote size: {}", localFilePath, localFileSize, remoteFileSize);
        }
        return result;
    }

    // 列出远程目录文件
    public List<RemoteResourceInfo> listFiles(String remoteDirectory) throws IOException {
        List<RemoteResourceInfo> files = getSftpClient().ls(remoteDirectory); // 列出远程目录文件
        return files;
    }

    public SFTPClient getSftpClient() {
        if (null == sftpClient) {
            try {
                sftpClient = sshClient.newSFTPClient();
                return sftpClient;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return sftpClient;
        }
    }

}
