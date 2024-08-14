package com.so.component.util;

import com.so.entity.ConnectionInfo;
import com.so.util.SSHClientUtil;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.TimeUnit;

// Implement both receiver that saves upload in a file and
// listener for successful upload
public class RemoteFileUploaderForSshj implements Receiver, SucceededListener, FailedListener {

    public File file;
    private String localPath;
    private String parentPath;
    private static volatile boolean uploadLocalEnd = false;
    private SSHClientUtil session;

    private static final Logger log = LoggerFactory.getLogger(RemoteFileUploaderForSshj.class);


    public RemoteFileUploaderForSshj() {
        super();
    }

    @Override
    public OutputStream receiveUpload(String filename, String mimeType) {
        String property = System.getProperty("user.dir");
        localPath = property + File.separator + filename;
        log.info("上传临时文件路径为：" + localPath);
        file = new File(localPath);
        FileOutputStream fileStream = null;
        try {
            fileStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return fileStream;
    }

    @Override
    public void uploadFailed(FailedEvent event) {
        Notification.show("提示：", "上传文件失败，请重新上传", Notification.Type.WARNING_MESSAGE);
    }

    @Override
    public void uploadSucceeded(SucceededEvent event) {
        //判断：如果是远程上传则现将文件上传到本地机器当前工作目录下，然后再启动一个线程等待文件上传完成后，将文件发送到远程机器，成功后将本地文件删除。
        uploadLocalEnd = true;
        try {
            TimeUnit.SECONDS.sleep(1);
            if (uploadLocalEnd) {
                FileInputStream in = null;
                try {
                    in = new FileInputStream(file);
//                        JSchUtil.uploadFile(session, in, parentPath, file.getName());
                    File localFile = new File(localPath);
                    session.uploadFile(localPath, parentPath, localFile.hashCode()+"");
                    log.info("远程文件上传成功=====");
                    Notification.show("提示：", "上传文件成功", Notification.Type.WARNING_MESSAGE);
                } catch (Exception e) {
                    log.info("远程文件上传失败=====");
                    e.printStackTrace();
                    log.error(ExceptionUtils.getStackTrace(e));
                } finally {
                    try {
                        file.delete();
                        if (null != in) {
                            in.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                log.info("等待1秒后检查是否本地上传成功。。。");
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public void setSession(SSHClientUtil session) {
        this.session = session;
    }

}