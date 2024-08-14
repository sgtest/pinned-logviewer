package com.so.component.remote;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.ssh.JschUtil;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.so.component.CommonComponent;
import com.so.component.util.ColorEnum;
import com.so.component.util.ConfirmationDialogPopupWindow;
import com.so.component.util.RemoteFileUploaderForSshj;
import com.so.entity.ConnectionInfo;
import com.so.entity.RemoteFileInfo;
import com.so.ui.ComponentFactory;
import com.so.ui.LoginView;
import com.so.util.Constants;
import com.so.util.JSchUtil;
import com.so.util.SSHClientUtil;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.*;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.xfer.FilePermission;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 远程文件管理
 */
@Service
@Scope("prototype")
public class RemoteFileMgmtComponent extends CommonComponent {

    private static final Logger log = LoggerFactory.getLogger(RemoteFileMgmtComponent.class);

    private Panel mainPanel;
    private VerticalLayout contentLayout;
    private ConnectionInfo addr;
    private String hostName;
    private Grid<RemoteFileInfo> grid;
    private Button backBtn;
    private Label pathLb;
    private SSHClientUtil clientUtil;
    private Session jschSession;
    private LinkedList<String> pathList = new LinkedList();

    @Override
    public void initLayout() {
        mainPanel = new Panel();
        contentLayout = new VerticalLayout();
        setCompositionRoot(mainPanel);
        mainPanel.setContent(contentLayout);
        contentLayout.setWidth("100%");
        contentLayout.setHeight("700px");
        initMainLayout();
//        加载目录
        initGridContent(null);
        readyToConnect();
    }

    private void initMainLayout() {
        HorizontalLayout horizontalLayout = ComponentFactory.getHorizontalLayout();
        horizontalLayout.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        backBtn = ComponentFactory.getImageButton();
        backBtn.setIcon(VaadinIcons.BACKSPACE);
        horizontalLayout.addComponent(backBtn);
        pathLb = ComponentFactory.getStandardLabel("");
        horizontalLayout.addComponent(pathLb);
        horizontalLayout.setExpandRatio(pathLb, 1);
        contentLayout.addComponent(horizontalLayout);

        grid = new Grid<RemoteFileInfo>();
        contentLayout.addComponent(grid);
        contentLayout.setExpandRatio(grid, 1);
        grid.setWidthFull();
        grid.setHeightFull();
        grid.addColumn(RemoteFileInfo::getFileName).setCaption("名称");
        grid.addColumn(RemoteFileInfo::getPermission).setCaption("权限");
        grid.addColumn(RemoteFileInfo::getUserName).setCaption("用户");
        grid.addColumn(RemoteFileInfo::getSize).setCaption("大小");
        grid.addColumn(RemoteFileInfo::getLastModify).setCaption("修改时间");
        grid.addComponentColumn(file -> {
            if (!file.getIsFile()) {
                Button b = ComponentFactory.getLinkButton("进入");
                b.addClickListener(e -> {
                    initGridContent(file.getCurrentPath());
                    pathList.add(file.getCurrentPath());
                    pathLb.setValue(file.getCurrentPath());
                });
                return b;
            }
            return null;
        }).setCaption("打开");
        grid.addComponentColumn(file -> {
            if (!file.getIsFile()) {
                RemoteFileUploaderForSshj loader = new RemoteFileUploaderForSshj();
                loader.setSession(clientUtil);
                loader.setParentPath(file.getCurrentPath());
                Upload upload = new Upload("上传", loader);
                upload.setImmediateMode(true);
                upload.addStartedListener(event -> {
                    if (!LoginView.checkPermission(Constants.UPLOAD)) {
                        Notification.show("权限不足，请联系管理员", Notification.Type.WARNING_MESSAGE);
                        throw new RuntimeException("权限不足，终止上传");
                    }
                });
                upload.setButtonCaption("上传");
                upload.setHeight("30px");
                upload.addSucceededListener(loader);
                return upload;
            }
            return null;
        }).setCaption("上传");
        grid.addComponentColumn(file -> {
            if (file.getIsFile()) {
                Button b = ComponentFactory.getLinkButton("下载");
                FileDownloader fileDownloader = new FileDownloader(new StreamResource(new FileStreamResource(file), file.getFileName()));
                fileDownloader.extend(b);
                return b;
            }
            return null;
        }).setCaption("下载");
        grid.addComponentColumn(file -> {
            Button deleteBtn = ComponentFactory.getButtonWithColor("删除", ColorEnum.RED);
            deleteBtn.addClickListener(e -> {
                ConfirmationDialogPopupWindow win = new ConfirmationDialogPopupWindow("确认", "确认是否删除！", "确认", "取消", true);
                win.getYesButton().addClickListener(c -> {
                    try {
                        if (!LoginView.checkPermission(Constants.DELETE)){
                            Notification.show("权限不足，请联系管理员", Notification.Type.WARNING_MESSAGE);
                            return;
                        }
                        if (file.getIsFile()) {
                            clientUtil.getSftpClient().rm(file.getCurrentPath());
                        } else {
                            clientUtil.getSftpClient().rmdir(file.getCurrentPath());
                        }
                        win.close();
                        Notification.show("提示：", "删除成功", Notification.Type.WARNING_MESSAGE);
                        initGridContent(file.getCurrentPath());
                    } catch (IOException ex) {
                        log.error("删除文件错误" + ExceptionUtils.getStackTrace(ex));
                    }
                });
                win.showConfirmation();
            });
            return deleteBtn;
        }).setCaption("删除");
    }

    private void initGridContent(String path) {
        //创建链接获取用户目录列表
        try {
            if (null == clientUtil) {
                clientUtil = new SSHClientUtil(addr.getIdHost(), Integer.parseInt(addr.getCdPort()), addr.getIdUser(), addr.getCdPassword());
                clientUtil.openConnection();
            }
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            Notification.show("链接当前机器失败，请检查该IP：" + addr.getIdHost(), Notification.Type.ERROR_MESSAGE);
            return;
        }
        try {
            List<RemoteResourceInfo> remoteResourceInfos = null;
            if (null == path) {
                pathList.add("/");
                if ("root".equals(addr.getIdUser())) {
                    remoteResourceInfos = clientUtil.listFiles("/root");
                    pathList.add("/root");
                    pathLb.setValue("/root");
                } else {
                    remoteResourceInfos = clientUtil.listFiles("/home/" + addr.getIdUser());
                    pathList.add("/home/" + addr.getIdUser());
                    pathLb.setValue("/home/" + addr.getIdUser());
                }
            } else {//多次点击目录
                remoteResourceInfos = clientUtil.listFiles(path);
            }
            List<RemoteFileInfo> infos = convertRemoteFileInfo(remoteResourceInfos);
            grid.setItems(infos);
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            Notification.show("获取目录数据错误，请及时查看日志！：" + addr.getIdHost(), Notification.Type.ERROR_MESSAGE);
        }
    }

    private List<RemoteFileInfo> convertRemoteFileInfo(List<RemoteResourceInfo> remoteResourceInfos) {
        List<RemoteFileInfo> fileInfos = new ArrayList<>();
        for (RemoteResourceInfo in : remoteResourceInfos) {
            if (!in.getName().startsWith(".")) {
                RemoteFileInfo fileInfo = new RemoteFileInfo();
                fileInfo.setFileName(in.getName());
            Set<FilePermission> permissions = in.getAttributes().getPermissions();
                String perm = getPermissionString(permissions);
                fileInfo.setPermission(perm);
                fileInfo.setLastModify(DateUtil.format(new Date(in.getAttributes().getAtime()*1000),"yyyy-MM-dd HH:mm:ss"));
                if (in.getAttributes().getSize() <= 1024) {
                    fileInfo.setSize(in.getAttributes().getSize() + "b");
                } else if (in.getAttributes().getSize() < 1024 * 1024) {
                    fileInfo.setSize(in.getAttributes().getSize() / 1024 + "kb");
                } else if (in.getAttributes().getSize() < 1024 * 1024 * 1024) {
                    fileInfo.setSize(in.getAttributes().getSize() / 1024 / 1024 + "mb");
                } else {
                    fileInfo.setSize(in.getAttributes().getSize() / 1024 / 1024 / 1024 + "Gb");
                }
                fileInfo.setUserName(addr.getIdUser());
                fileInfo.setParentPath(in.getParent());
                fileInfo.setCurrentDir(in.getPath());
                fileInfo.setIsFile(!in.getAttributes().getMode().getType().name().contains("DIRECTORY"));
                fileInfos.add(fileInfo);
            }
        }
        return fileInfos;
    }

    private String getPermissionString(Set<FilePermission> permissions) {
        List<FilePermission> collect1 = permissions.stream().filter(f -> f.name().length() == 5).collect(Collectors.toList());
        List<String> usr = collect1.stream().filter(f -> f.name().startsWith("USR")).map(m -> m.name().split("_")[1]).collect(Collectors.toList());
        List<String> usrGroup = collect1.stream().filter(f -> f.name().startsWith("GRP")).map(m -> m.name().split("_")[1]).collect(Collectors.toList());
        List<String> other = collect1.stream().filter(f -> f.name().startsWith("OTH")).map(m -> m.name().split("_")[1]).collect(Collectors.toList());
        return usr.toString().replace(",","")+usrGroup.toString().replace(",","")+other.toString().replace(",","");
    }

    @Override
    public void detach() {
        super.detach();
        this.clientUtil.closeConnection();
        if (jschSession != null){
            jschSession.disconnect();
        }
    }

    @Override
    public void initContent() {

    }

    @Override
    public void registerHandler() {
        backBtn.addClickListener(e -> {
            if (pathList.size() > 1) {
                pathList.removeLast();
                String prePath = pathList.peekLast();
                initGridContent(prePath);
                pathLb.setValue(prePath);
            }
            if (pathList.size() == 1) {
                String prePath = pathList.peekLast();
                initGridContent(prePath);
                pathLb.setValue(prePath);
            }
        });
    }

    public ConnectionInfo getAddr() {
        return addr;
    }

    public void setAddr(ConnectionInfo addr) {
        this.addr = addr;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    class FileStreamResource implements StreamResource.StreamSource {

        private static final long serialVersionUID = 6327185867459484865L;
        private RemoteFileInfo filePathInfo;
        private ChannelSftp channel;

        public FileStreamResource(RemoteFileInfo filePathInfo) {
            this.filePathInfo = filePathInfo;
        }

        @Override
        public InputStream getStream() {
            if (null == channel){
                try {
                    channel = JSchUtil.openSftpChannel(jschSession);
                    channel.connect();
                } catch (JSchException e) {
                    log.error(ExceptionUtils.getStackTrace(e));
                    throw new RuntimeException(e);
                }
            }
            InputStream inputStream;
            try {
                inputStream = channel.get(filePathInfo.getCurrentPath());
                return inputStream;
            }catch (SftpException e) {
                log.error("下载文件出现错误");
                log.error(ExceptionUtils.getStackTrace(e));
            }
            return null;
        }

    }

    public Session getJschSession() {
        return jschSession;
    }

    public void setJschSession(Session jschSession) {
        this.jschSession = jschSession;
    }
    private void readyToConnect() {
        try {
            if (addr.getCdKeyPath() == null) {
                //无秘钥连接
                jschSession = JschUtil.createSession(hostName, Integer.parseInt(addr.getCdPort()), addr.getIdUser(), addr.getCdPassword());
            }else if(addr.getCdKeyPath() != null){
                //秘钥连接
                jschSession = JschUtil.createSession(hostName, Integer.parseInt(addr.getCdPort()), addr.getIdUser(),addr.getCdKeyPath(),  addr.getCdPassword() == null ?null :addr.getCdPassword().getBytes());
            }
            jschSession.setTimeout(1800);
        } catch (NumberFormatException e) {
            Notification.show("连接失败请检查配置", Notification.Type.WARNING_MESSAGE);
           log.error(ExceptionUtils.getStackTrace(e));
        } catch (JSchException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        }
    }
}
