package com.so;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.system.SystemUtil;
import com.so.util.Util;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


@SpringBootApplication(exclude = { DataSourceTransactionManagerAutoConfiguration.class,
		DataSourceAutoConfiguration.class, HazelcastAutoConfiguration.class})
@MapperScan("com.so.mapper")
public class Application1 extends org.springframework.boot.web.servlet.support.SpringBootServletInitializer{
	
	private static Logger logger = LoggerFactory.getLogger(Application1.class);
	
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Application1.class); 
        ConfigurableApplicationContext ctx = app.run(args);
		logger.info("the application start success!!!");
		String port = ctx.getEnvironment().getProperty("server.port");
		String path = ctx.getEnvironment().getProperty("server.servlet.context-path");
		System.out.println("====================address============================");
		for (NetworkInterface networkInterface : NetUtil.getNetworkInterfaces()) {
			try {
				if (networkInterface.isUp()){
					Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
					while (inetAddresses.hasMoreElements()){
						InetAddress inetAddress = inetAddresses.nextElement();
						if (!inetAddress.isSiteLocalAddress()){
							continue;
						}
						System.out.println(inetAddress.getHostAddress()+":"+port+path);
					}
				}
			} catch (SocketException e) {
				throw new RuntimeException(e);
			}
		}
		try {
			if (SystemUtil.getOsInfo().isLinux()){
				mkdirBin();
			}
		} catch (IOException e) {
			logger.error("生成server.sh脚本错误{}",e.getMessage());
			throw new RuntimeException(e);
		}
	}

	private static void mkdirBin() throws IOException {
		String property = System.getProperty("user.dir");
		String pathname = property + File.separator + "bin";
		File file = new File(pathname);
		if (!file.exists()){
			boolean mkdirs = file.mkdirs();
		}
		ClassPathResource res = new ClassPathResource("server.sh");
		try (InputStream in = res.getInputStream()) {
			ArrayList<String> readLines = IoUtil.readLines(in, "UTF-8", new ArrayList<String>());
			FileUtil.writeUtf8Lines(readLines,pathname + File.separator+"server.sh");
		}
		if (SystemUtil.getOsInfo().isLinux()){
			Util.executeLinuxCmd("chmod 777 "+pathname + File.separator+"server.sh");
		}
	}

	@Override
	protected SpringApplicationBuilder configure (SpringApplicationBuilder application){
		return application.sources(Application1.class);
	}
}
