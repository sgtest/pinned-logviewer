package com.so.component.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import com.so.component.CommonComponent;
import com.vaadin.ui.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.jcraft.jsch.Session;
import com.so.component.ComponentUtil;
import com.so.entity.ConnectionInfo;
import com.so.entity.ProjectList;
import com.so.mapper.ProjectsMapper;
import com.so.util.JSchUtil;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FailedListener;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

// Implement both receiver that saves upload in a file and
// listener for successful upload
public class RemoteFileUploader implements Receiver, SucceededListener, FailedListener {
	private static final long serialVersionUID = -2426493461264140038L;
	public File file;
	private String keypath;
	private String parentPath;
	private String idProject;
	private boolean remoteFlag;
	private ConnectionInfo addr;
	private CommonComponent component;
	private static volatile boolean uploadLocalEnd = false;
	private Session session;
	
	private static final Logger log = LoggerFactory.getLogger(RemoteFileUploader.class);

	
	public RemoteFileUploader() {
		super();
	}

	public RemoteFileUploader(boolean remoteFlag) {
		super();
		this.remoteFlag = remoteFlag;
	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		String property = System.getProperty("user.dir");
		keypath = property + File.separator + filename;
		log.info("上传临时文件路径为："+keypath);
		file = new File(keypath);
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
		ProjectsMapper projectsMapper = ComponentUtil.applicationContext.getBean(ProjectsMapper.class);
		try {
			QueryWrapper<ProjectList> queryWrapper = new QueryWrapper<ProjectList>();
			queryWrapper.eq("id_host", addr.getIdHost()).eq("id_project",idProject);
			ProjectList selectById = projectsMapper.selectOne(queryWrapper);
			if (file.getName().endsWith("jar")) {
				selectById.setJarName(file.getName());
				UpdateWrapper<ProjectList> up = new UpdateWrapper<ProjectList>();
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("id_host", addr.getIdHost());
				map.put("id_project", idProject);
				up.allEq(map);
				projectsMapper.update(selectById, up);
			}
			if (null != component){
				component.initLayout();
				component.initContent();
				component.registerHandler();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			log.error(ExceptionUtils.getStackTrace(e1));
		}
		Notification.show("提示：", "上传文件成功", Notification.Type.WARNING_MESSAGE);
		uploadLocalEnd = true;
		log.info("上传成功");
		//判断：如果是远程上传则现将文件上传到本地机器当前工作目录下，然后再启动一个线程等待文件上传完成后，将文件发送到远程机器，成功后将本地文件删除。
		if (remoteFlag) {
//			new Thread( new Runnable() {
//				@Override
//				public void run() {
					try {
						TimeUnit.SECONDS.sleep(3);
							if (uploadLocalEnd) {
								FileInputStream in = null;
								try {
									in = new FileInputStream(file);
									JSchUtil.uploadFile(session, in, parentPath, file.getName());
									JSchUtil.uploadFile(session,new FileInputStream(new File(System.getProperty("user.dir")+ File.separator + "bin" +File.separator+"server.sh")),parentPath, "server.sh");
//									JSchUtil.uploadFile(session, file, parentPath);
//									JSchUtil.scpTo2(session, keypath, parentPath);
									log.info("远程文件上传成功=====");
								} catch (Exception e) {
									log.info("远程文件上传失败=====");
									e.printStackTrace();
									log.error(ExceptionUtils.getStackTrace(e));
								}finally {
									try {
										file.delete();
										if (null != in) {
											in.close();
										}
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}else {
								log.info("等待1秒后检查是否本地上传成功。。。");
								TimeUnit.SECONDS.sleep(1);
							}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
//				}
//			}).start();
		}
//	上传server.sh脚本
		if (remoteFlag) {
			try {
				TimeUnit.SECONDS.sleep(1);
					FileInputStream in = null;
					try {
						in = new FileInputStream(new File(System.getProperty("user.dir")+ File.separator + "bin" +File.separator+"server.sh"));
						JSchUtil.uploadFile(session,in,parentPath, "server.sh");
						JSchUtil.remoteExecute(session,"chmod 777 "+parentPath +File.separator+"server.sh");
						log.info("远程脚本上传成功=====");
					} catch (Exception e) {
						log.info("远程文件上传失败=====");
						log.error(ExceptionUtils.getStackTrace(e));
					}finally {
						try {
							if (null != in) {
								in.close();
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getKeypath() {
		return keypath;
	}

	public void setKeypath(String keypath) {
		this.keypath = keypath;
	}

	public String getIdProject() {
		return idProject;
	}

	public void setIdProject(String idProject) {
		this.idProject = idProject;
	}

	public String getParentPath() {
		return parentPath;
	}

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

	public boolean isRemoteFlag() {
		return remoteFlag;
	}

	public void setRemoteFlag(boolean remoteFlag) {
		this.remoteFlag = remoteFlag;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public ConnectionInfo getAddr() {
		return addr;
	}

	public void setAddr(ConnectionInfo addr) {
		this.addr = addr;
	}

	public Component getComponent() {
		return component;
	}

	public void setComponent(CommonComponent component) {
		this.component = component;
	}
}