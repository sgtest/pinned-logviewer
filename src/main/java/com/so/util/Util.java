package com.so.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import com.so.component.ComponentUtil;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.SM3;

public class Util {

	private static final Logger log = LoggerFactory.getLogger(Util.class);

	private static final String USERS_CONFIG = "users.properties";

	/**
	 * 判断文件的编码格式
	 * 
	 * @param fileName
	 *            :file
	 * @return 文件编码格式
	 * @throws Exception
	 */
	public static String getFileEncode(File fileName) throws Exception {
		BufferedInputStream bin = new BufferedInputStream(new FileInputStream(fileName));
		int p = (bin.read() << 8) + bin.read();
		String code = null;

		switch (p) {
		case 0xefbb:
			code = "UTF-8";
			break;
		case 0xfffe:
			code = "Unicode";
			break;
		case 0xfeff:
			code = "UTF-16BE";
			break;
		default:
			code = "GBK";
		}
		IoUtil.close(bin);
		return code;
	}

	/**
	 * 查询配置文件中的用户
	 */
	public static Map<String, String> getUsers() {
		Map<String, String> users = new HashMap<String, String>();
		List<String> readLines = getUsersConfigFile();
		for (String e : readLines) {
			if (e.trim().equals("")) {
				continue;
			} else if (e.contains("=")) {
				users.put(e.split("=")[0], e.split("=")[1]);
			}
		}

		return users;
	}

	public static List<String> getUsersAsLine() {
		return getUsersConfigFile();
	}

	public static List<String> getConfigFileAsLineByClasspathResource(String fileName) {
		ClassPathResource res = new ClassPathResource(fileName);
		try (InputStream in = res.getInputStream()) {
			ArrayList<String> readLines2 = IoUtil.readLines(in, "UTF-8", new ArrayList<String>());
			return readLines2;
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return null;
	}

	public static List<String> getUserGuide() {
		ClassPathResource res = new ClassPathResource("userGuide.txt");
		try (InputStream in = res.getInputStream()) {
			ArrayList<String> readLines2 = IoUtil.readLines(in, "UTF-8", new ArrayList<String>());
			return readLines2;
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return null;
	}

	/**
	 * 将用户保存到配置文件中，覆盖模式
	 * 
	 * @param lines
	 */
	public static void saveUsers(List<String> lines) {
		// 移除空白行
		ListIterator<String> listIterator = lines.listIterator();
		while (listIterator.hasNext()) {
			String str = listIterator.next();
			if (str.trim().equals("")) {
				listIterator.remove();
			}
		}
		if (null == lines || lines.isEmpty()) {
			return;
		}
		String path = System.getProperty("user.dir") + File.separator + USERS_CONFIG;
		// 写入
		File file = new File(path);
		FileUtil.writeLines(lines, file, "UTF-8");

	}

	public static List<String> getUsersConfigFile() {
		List<String> configs = new ArrayList<String>();
		List<String> defaultUser = getDefaultUser();
		configs.addAll(defaultUser);
		String path = System.getProperty("user.dir") + File.separator + USERS_CONFIG;
		File file = new File(path);
		if (!FileUtil.exist(file)) {
			log.error("请在jar包同级路径下放置users.properties文件，以便登录使用，格式：用户名=密码,默认用户admin");
			log.error(path);
			return configs;
		} else {
			configs.addAll(defaultUser);
			List<String> readLines = FileUtil.readLines(file, Charset.forName("UTF-8"));
			configs.addAll(readLines);
			return readLines;
		}
	}

	/**
	 * 写入用户配置文件，普通路径为##后缀 ssh配置为##ssh后缀
	 * 
	 * @param lines
	 * @throws IOException
	 */
	public static void saveUserConfigToFile(String line) {
		if (null == line) {
			return;
		}
		String path = System.getProperty("user.dir") + File.separator + ComponentUtil.getCurrentUserName() + ".properties";
		try {
			if (!FileUtil.exist(new File(path))) {
				// 不存在，创建
				File file = new File(path);
				file.createNewFile();
			}
		} catch (IOException e) {
			log.error("创建文件失败{}", e.getMessage());
			e.printStackTrace();
		}
		// 写入
		FileUtil.appendString(System.lineSeparator() + line, path, Charset.forName("UTF-8"));
	}

	/**
	 * 查询用户配置文件，普通路径为##后缀 ssh配置为##ssh后缀
	 * 
	 * @param lines
	 * @throws IOException
	 */
	public static List<String> getUserConfig() {
		List<String> configs = new ArrayList<String>();
		String path = System.getProperty("user.dir") + File.separator + ComponentUtil.getCurrentUserName() + ".properties";
		File file = new File(path);
		if (!FileUtil.exist(file)) {
			return configs;
		} else {
			List<String> readLines = FileUtil.readLines(file, Charset.forName("UTF-8"));
			return readLines;
		}
	}

	/**
	 * 检查用户配置文件是否存在某个key 存在返回true
	 * 
	 * @param lines
	 * @throws IOException
	 */
	public static boolean checkUserConfig(String key) {
		String path = System.getProperty("user.dir") + File.separator + ComponentUtil.getCurrentUserName() + ".properties";
		File file = new File(path);
		if (!FileUtil.exist(file)) {
		} else {
			List<String> readLines = FileUtil.readLines(file, Charset.forName("UTF-8"));
			for (String l : readLines) {
				if (!l.trim().equals("") && l.contains("=")) {
					if (l.split("=")[0].equals(key)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 读取服务器列表配置
	 * 
	 * @return
	 */
	public static List<String> getRemoteServerList() {
		List<String> configFileAsLine = getConfigFileAsLineByClasspathResource("remoteServerList.conf");

		ListIterator<String> listIterator = configFileAsLine.listIterator();
		while (listIterator.hasNext()) {
			String type = listIterator.next();
			if (type.startsWith("#") || type.trim().equals("") || !type.contains("=") || type.endsWith("=path")) {
				listIterator.remove();
			}
		}
		return configFileAsLine;
	}

	/**
	 * 读取classpath下的默认用户
	 * 
	 * @return
	 */
	public static List<String> getDefaultUser() {
		List<String> configFileAsLine = getConfigFileAsLineByClasspathResource("users.properties");

		ListIterator<String> listIterator = configFileAsLine.listIterator();
		while (listIterator.hasNext()) {
			String type = listIterator.next();
			if (type.startsWith("#") || type.trim().equals("") || !type.contains("=") || type.endsWith("=path")) {
				listIterator.remove();
			}
		}
		return configFileAsLine;
	}

	/**
	 * 查询配置服务器列表
	 */
	public static List<String> getServerList() {
		List<String> serverList = new ArrayList<String>();
		List<String> configFileAsLine = getConfigFileAsLineByClasspathResource("remoteServerList.conf");
		for (String e : configFileAsLine) {
			if (e.startsWith("#") || e.trim().equals("")) {
				continue;
			} else if (e.contains("=")) {
				serverList.add(e.trim());
			}
		}
		return serverList;
	}

	/**
	 * 查询文件后缀配置
	 */
	public static List<String> getFileSuffix() {
		List<String> suffixList = new ArrayList<String>();
		List<String> configFileAsLine = getConfigFileAsLineByClasspathResource("fileSuffix.conf");
		for (String e : configFileAsLine) {
			if (e.startsWith("#") || e.trim().equals("")) {
				continue;
			} else {
				suffixList.add(e.trim());
			}
		}
		return suffixList;
	}

	/**
	 * 原RequestDateConvert类中的方法
	 * 
	 * @param inDate
	 * @return
	 */
	public static Integer getMonth(String inDate) {
		String s = "0";
		if (inDate.equals("Jan")) {
			s = "01";
		} else if (inDate.equals("Feb")) {
			s = "02";
		} else if (inDate.equals("Mar")) {
			s = "03";
		} else if (inDate.equals("Apr")) {
			s = "04";
		} else if (inDate.equals("May")) {
			s = "05";
		} else if (inDate.equals("Jun")) {
			s = "06";
		} else if (inDate.equals("Jul")) {
			s = "07";
		} else if (inDate.equals("Aug")) {
			s = "08";
		} else if (inDate.equals("Sep")) {
			s = "09";
		} else if (inDate.equals("Oct")) {
			s = "10";
		} else if (inDate.equals("Nov")) {
			s = "11";
		} else if (inDate.equals("Dec")) {
			s = "12";
		}
		return Integer.parseInt(s);
	}

	private static ThreadLocal<DateFormat> threadLocalDateTime = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
		}
	};

	private static ThreadLocal<DateFormat> threadLocalDate = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		}
	};

	public static Date parseDate(String dateStr) throws ParseException {
		if (StrUtil.isBlank(dateStr)) {
			return null;
		}
		return threadLocalDate.get().parse(dateStr);
	}

	public static String formatDate(Date date) {
		if (date != null) {
			return threadLocalDate.get().format(date);
		} else {
			return "";
		}
	}

	public static Date parseDateTime(String dateStr) throws ParseException {
		if (StrUtil.isBlank(dateStr)) {
			return null;
		}
		return threadLocalDateTime.get().parse(dateStr);
	}

	public static String formatDateTime(Date date) {
		if (date != null) {
			return threadLocalDateTime.get().format(date);
		} else {
			return "";
		}
	}

	public static String getSm3DigestStr(String str) {
		SM3 s = new SM3();
		String digestHex = s.digestHex(str);
		return digestHex.toUpperCase();
	}

	/**
	 * 用于将list进行分页
	 * 
	 * @param sourceList
	 * @param pageSize
	 * @return
	 */
	public static <E> List<List<E>> splitListToPage(List<E> sourceList, Integer pageSize) {
		List<List<E>> pageResult = new ArrayList<List<E>>();
		if (null == sourceList || sourceList.isEmpty()) {
			return pageResult;
		}
		List<E> sub = new ArrayList<E>();
		for (E e : sourceList) {
			if (sub.size() >= pageSize) {
				pageResult.add(sub);
				sub = new ArrayList<E>();
			}
			sub.add(e);
		}
		if (!sub.isEmpty()) {
			pageResult.add(sub);
		}
		return pageResult;
	}

	public static <T> List<List<T>> zip(List<T>... lists) {
		List<List<T>> zipped = new ArrayList<List<T>>();
		for (List<T> list : lists) {
			for (int i = 0, listSize = list.size(); i < listSize; i++) {
				List<T> list2;
				if (i >= zipped.size()) {
					zipped.add(list2 = new ArrayList<T>());
				}else {
					list2 = zipped.get(i);
				}list2.add(list.get(i));
			}
		}
		return zipped;
	}

	/**
	 * 执行一条命令
	 * @param cmd
	 * @return
	 */
	public static String executeLinuxCmd(String cmd) {
		System.out.println("got cmd job : " + cmd);
		Runtime run = Runtime.getRuntime();
		try {
			Process process = run.exec(cmd);
			InputStream in = process.getInputStream();
			BufferedReader bs = new BufferedReader(new InputStreamReader(in));
			StringBuffer out = new StringBuffer();
			bs.lines().forEach(e -> out.append(e));
			System.out.println("job result [" + out.toString() + "]");
			in.close();
			 process.waitFor();
			process.destroy();
			return "success";
		} catch (IOException e) {
			log.error(ExceptionUtil.stacktraceToString(e));
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 打开一个连接，
	 * 
	 * @param commands
	 * @return
	 */
	public static List<String> executeNewFlow(List<String> commands) {
		List<String> results = new ArrayList<String>();
		Runtime run = Runtime.getRuntime();
		try {
			Process proc = run.exec("/bin/bash", null, null);
			BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(proc.getOutputStream())), true);
			for (String line : commands) {
				out.println(line);
				log.info("发送命令："+line);
			}
			// out.println("cd /home/test");
			// out.println("pwd");
			// out.println("rm -fr /home/proxy.log");
			out.println("exit");// 这个命令必须执行，否则in流不结束。
			String rspLine = "";
			while ((rspLine = in.readLine()) != null) {
//				System.out.println(rspLine);
				results.add(rspLine);
			}
			proc.waitFor();
			in.close();
			out.close();
			proc.destroy();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return results;
	}
	/**
	 * 执行python脚本
	 */
	public static void executePythonScript(String path) {
//       String path = "/data/logs/newblog/getbaidu.py";
        Process proc;
        try {
            proc = Runtime.getRuntime().exec("python3 " + path);// 执行py文件
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                log.info(line);
            }
            in.close();
            proc.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
	}

	public static void main(String[] args) {
		String sm3DigestStr = getSm3DigestStr("sunlong567");
		System.out.println(sm3DigestStr);
		String getkeyByAlgorithm = EncryptionUtils.getkeyByAlgorithm("SM3", "admin");
		System.out.println(getkeyByAlgorithm);
	}

}
