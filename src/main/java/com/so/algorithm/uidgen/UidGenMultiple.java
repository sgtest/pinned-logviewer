package com.so.algorithm.uidgen;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 多个机器ID对应多个生成器能够使ID尽可能随时间连续间隔变小
 * 使用方法:1、创建对象2、调用gteUid传入workerId
 * @author Administrator
 *
 */
public class UidGenMultiple {

	private ConcurrentHashMap<Integer, UidGenerator> workerMap;

	public UidGenMultiple() {
		super();
		if (workerMap == null) {
			workerMap = new ConcurrentHashMap<Integer, UidGenerator>(16);
		}
	}

	public String getUid(Integer workerId) {
		if (workerMap.get(workerId) ==null ) {
			workerMap.put(workerId, new UidGenerator(workerId, null));
		}
		return workerMap.get(workerId).getUid();
	}

}
