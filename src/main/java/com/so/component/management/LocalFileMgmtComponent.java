package com.so.component.management;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import com.so.component.CommonComponent;
import com.so.component.util.ColorEnum;
import com.so.component.util.ConfirmationDialogPopupWindow;
import com.so.component.util.FileUploader;
import com.so.entity.RemoteFileInfo;
import com.so.ui.ComponentFactory;
import com.so.ui.LoginView;
import com.so.util.Constants;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.*;
import net.schmizz.sshj.xfer.FilePermission;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 本地文件管理
 */
@Service
@Scope("prototype")
public class LocalFileMgmtComponent extends CommonComponent {

    private static final Logger log = LoggerFactory.getLogger(LocalFileMgmtComponent.class);

    private Panel mainPanel;
    private VerticalLayout contentLayout;
    private String hostName;
    private Grid<RemoteFileInfo> grid;
    private Button backBtn;
    private Label pathLb;
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
                FileUploader loader = new FileUploader();
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
                        if (file.getIsFile()){
                            Files.delete(Paths.get(file.getCurrentPath()));
                        }else{
                            win.close();
                            Notification.show("提示：", "为安全起见，暂不支持删除目录", Notification.Type.WARNING_MESSAGE);
                            return;
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
        String username = System.getProperty("user.name");
        try {
            List<RemoteFileInfo> remoteResourceInfos = null;
            if (null == path) {
                if (SystemUtil.getOsInfo().isLinux()){
                    pathList.add("/");
                }else{
                    pathList.add(SystemUtil.getUserInfo().getHomeDir());
                }
                if ("root".equals(username)) {
                    File file = new File("/root");
                    remoteResourceInfos = convertRemoteFileInfo(Arrays.asList(file.listFiles()), username);
                    pathList.add("/root");
                    pathLb.setValue("/root");
                } else {
                    String currentDir = SystemUtil.getUserInfo().getHomeDir();
                    File file = new File(currentDir);
                    remoteResourceInfos = convertRemoteFileInfo(Arrays.asList(file.listFiles()), username);
                    pathList.add(currentDir);
                    pathLb.setValue(currentDir);
                }
            } else {//多次点击目录
                File file = new File(path);
                if (file.listFiles() != null && file.listFiles().length != 0){
                    remoteResourceInfos = convertRemoteFileInfo(Arrays.asList(file.listFiles()), username);
                }
            }
            if (CollectionUtil.isEmpty(remoteResourceInfos)){
                grid.setItems(new ArrayList<RemoteFileInfo>());
            }else{
                grid.setItems(remoteResourceInfos);
            }
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            Notification.show("获取目录数据错误，请及时查看日志！：", Notification.Type.ERROR_MESSAGE);
        }
    }

    private List<RemoteFileInfo> convertRemoteFileInfo(List<File> files,String userName) {
        List<RemoteFileInfo> fileInfos = new ArrayList<>();
        for (File in : files) {
            if (!in.getName().startsWith(".")) {
                RemoteFileInfo fileInfo = new RemoteFileInfo();
                Path path = Paths.get(in.getAbsolutePath());
                try {
                    BasicFileAttributes fileAttributes = Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
                    fileInfo.setFileName(in.getName());
                    fileInfo.setUserName(userName);
                    if (SystemUtil.getOsInfo().isLinux()){
                        Set<PosixFilePermission> posixFilePermissions = Files.getPosixFilePermissions(path);
                        String perm = getPermissionString(posixFilePermissions);
                        fileInfo.setPermission(perm);
                    }
                    fileInfo.setLastModify(DateUtil.format(new Date(fileAttributes.lastModifiedTime().toMillis()),"yyyy-MM-dd HH:mm:ss"));
                    if (fileAttributes.size() <= 1024) {
                        fileInfo.setSize(fileAttributes.size() + "b");
                    } else if (fileAttributes.size() < 1024 * 1024) {
                        fileInfo.setSize(fileAttributes.size() / 1024 + "kb");
                    } else if (fileAttributes.size() < 1024 * 1024 * 1024) {
                        fileInfo.setSize(fileAttributes.size() / 1024 / 1024 + "mb");
                    } else {
                        fileInfo.setSize(fileAttributes.size() / 1024 / 1024 / 1024 + "Gb");
                    }
                    fileInfo.setParentPath(in.getParent());
                    fileInfo.setCurrentDir(in.getPath());
                    fileInfo.setIsFile(fileAttributes.isRegularFile());
                    fileInfos.add(fileInfo);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return fileInfos;
    }

    // [OWNER_WRITE, GROUP_WRITE, OTHERS_EXECUTE, OTHERS_READ, OWNER_READ, GROUP_READ, GROUP_EXECUTE, OWNER_EXECUTE]
    private String getPermissionString(Set<PosixFilePermission> permissions) {
        List<String> usr = permissions.stream().filter(f -> f.name().startsWith("OWNER")).map(m -> StrUtil.subPre(m.name().split("_")[1],1)).collect(Collectors.toList());
        List<String> usrGroup = permissions.stream().filter(f -> f.name().startsWith("GROUP")).map(m -> StrUtil.subPre(m.name().split("_")[1],1)).collect(Collectors.toList());
        List<String> other = permissions.stream().filter(f -> f.name().startsWith("OTHERS")).map(m -> StrUtil.subPre(m.name().split("_")[1],1)).collect(Collectors.toList());
        String result = usr.toString().replace(",", "") + usrGroup.toString().replace(",", "") + other.toString().replace(",", "");
        return result.replace("E","X");
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

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    class FileStreamResource implements StreamResource.StreamSource {

        private static final long serialVersionUID = 6327185867459484865L;
        private RemoteFileInfo filePathInfo;
        public FileStreamResource(RemoteFileInfo filePathInfo) {
            this.filePathInfo = filePathInfo;
        }

        @Override
        public InputStream getStream() {
            InputStream inputStream;
            try {
                inputStream = new FileInputStream(filePathInfo.getCurrentPath());
                return inputStream;
            } catch (FileNotFoundException e) {
                log.error("下载文件出现错误");
                log.error(ExceptionUtils.getStackTrace(e));
            }
            return null;
        }

    }

}
