package com.so.algorithm.uidgen;

import java.time.LocalDate;
import java.time.LocalTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * uid生成器：格式为前前四位机器ID+八位当天日期+当前时间毫秒数 考虑到时钟回拨不报错，能够返回不重复的ID
 * 此类相比UidGenerator少了缓冲和多线程支持，适合当工具类使用
 * @author Administrator
 *
 */

public class UidGeneratorUtil {

	private static final Logger log = LoggerFactory.getLogger(UidGeneratorUtil.class);
	private Integer workerId = 1;// 机器ID最大9999
	private Integer serial = 1;//序列号
	private LocalTime generateTime = LocalTime.now();
	private Integer backSerial = 1;

	private String nextId() {
		LocalTime currentSystemTime = LocalTime.now();
		if (currentSystemTime.equals(generateTime)) {
			serial++;
			return timeGen() + serial;
		} else if (LocalTime.now().isAfter(generateTime)) {
			serial = 1;
			return timeGen() + serial;
		} else {
			// 时钟回拨
			log.warn("发现时钟回拨现象=================");
			backSerial++;
			long nanoTime = System.nanoTime();
			return nanoTime + backSerial + "";
		}
	}
	
	public String getUid() {
		return nextId();
	}

	private String timeGen() {
		String date = LocalDate.now().toString().replace("-", "");
		generateTime = LocalTime.now();
		String time = generateTime.toString().replace(":", "").replace(".", "");
		return workerId + date + time;
	}

	public Integer getWorkerId() {
		return workerId;
	}

	public UidGeneratorUtil(Integer workerId) {
		setWorkerId(workerId);
	}

	public void setWorkerId(Integer workerId) {
		if (workerId <= 0 || workerId >= 10000) {
			throw new RuntimeException("workerId must between 0 and 10000");
		}
		this.workerId = workerId;
	}

}
