package com.so.algorithm.uidgen;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * uid生成器：格式为前前四位机器ID+八位当天日期+当前时间毫秒数 考虑到时钟回拨不报错，能够返回不重复的ID
 * 注：有人觉得为什么不用long类型使用位移操作提升速度呢，
 * 解释，该ID的优点是在ID中肉眼能看到年月日时分秒等信息，其次在速度上面没有妥协，使用队列预加载缓存大小可设置，获取ID是通过队列获取，不是现成生成的，所以性能已经达到了最高；
 * 如果将队列初始缓存设置为百万，则性能更高；
 * @author Administrator
 *
 */

public class UidGenerator {

	private static final Logger log = LoggerFactory.getLogger(UidGenerator.class);

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		UidGenerator gen = new UidGenerator(22, 100000);
//		try {
//			Thread.sleep(1000L);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		long currentTimeMillis = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			gen.getUid();
		}
		System.out.println(System.currentTimeMillis() - currentTimeMillis);
	}

	private Integer workerId = 1;// 机器ID最大9999
	private Map<String, LinkedList<String>> map;
	private LinkedBlockingQueue<String> buffer;
	private Integer serial = 1;//序列号
	private LocalTime generateTime = LocalTime.now();
	private Integer backSerial = 1;
	private Integer bufferSize = 10000;//缓冲的大小默认1万
	private boolean forceUpdate = false;

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
	/**
	 * 当返回null时可重新尝试获取
	 * @return
	 */
	public String getUid() {
		//判断如果队列当前存储的ID时间已经不是当日则清空队列重新生成；
		if (forceUpdate) {
			int length = String.valueOf(workerId).length();
			try {
				if (!buffer.take().substring(length, length+8).equals(LocalDate.now().toString().replace("-", ""))) {
					buffer.clear();
				}
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		try {
			return buffer.take();
		} catch (InterruptedException e) {
			log.error("get uid error :{}",e.getMessage());
			e.printStackTrace();
		}
		return null;
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

	public UidGenerator(Integer workerId,Integer bufferSize) {
		setWorkerId(workerId);
		if (null != bufferSize && bufferSize > this.bufferSize) {
			this.bufferSize = bufferSize;
		}
		buffer = new LinkedBlockingQueue<String>(bufferSize);
		new task().start();
	}
	
	public UidGenerator(Integer workerId,Integer bufferSize,boolean forceUpdate) {
		setWorkerId(workerId);
		if (null != bufferSize && bufferSize > this.bufferSize) {
			this.bufferSize = bufferSize;
		}
		this.forceUpdate = forceUpdate;
		buffer = new LinkedBlockingQueue<String>(bufferSize);
		new task().start();
	}

	
	public boolean isForceUpdate() {
		return forceUpdate;
	}
	public void setForceUpdate(boolean forceUpdate) {
		this.forceUpdate = forceUpdate;
	}
	public void setWorkerId(Integer workerId) {
		if (workerId <= 0 || workerId >= 10000) {
			throw new RuntimeException("workerId must between 0 and 10000");
		}
		this.workerId = workerId;
	}
	
	public class task extends Thread{
		
		@Override
		public void run() {
			while (true) {
				if (buffer.size() < bufferSize) {
					try {
						buffer.put(nextId());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
