package com.so.algorithm;

import java.util.HashMap;
import java.util.Map.Entry;

import cn.hutool.core.util.RandomUtil;
/**
 * 平滑加权轮询算法
 * @author Administrator
 *
 */
public class SmoothWeightRound {

	public static HashMap<String, Server> serverMap = new HashMap<String, Server>() {
		private static final long serialVersionUID = -890456061425075613L;
		{
			put("192.168.1.1", new Server(5, 5, "192.168.1.1"));
			put("192.168.1.2", new Server(1, 1, "192.168.1.2"));
			put("192.168.1.3", new Server(1, 1, "192.168.1.3"));
		}
	};
	public static HashMap<Integer, Server> serverMap2 = new HashMap<Integer, Server>() ;
	private static int totalWeight;

	/**
	 * 方式一
	 * @return
	 */
	public static String go() {
		Server maxWeightServer = null;
		int allWeight = serverMap.values().stream().mapToInt(Server::getWeight).sum();
		for (Entry<String, Server> item : serverMap.entrySet()) {
			Server currentServer = item.getValue();
			if (maxWeightServer == null || currentServer.getCurrentWeight() > maxWeightServer.getCurrentWeight()) {
				maxWeightServer = currentServer;
			}
		}
		maxWeightServer.setCurrentWeight(maxWeightServer.getCurrentWeight() - allWeight);
		for (Entry<String, Server> item : serverMap.entrySet()) {
			Server currentServer = item.getValue();
			currentServer.setCurrentWeight(currentServer.getCurrentWeight() + currentServer.getWeight());
		}
		return maxWeightServer.getIp();
	}
	/**
	 * 方式二
	 * @return
	 */
	public static String go2() {
		//go2Init该步骤是初始化步骤，实际使用中，调用一次即可；除非server数据有变更。
		go2Init();
		int randomInt = RandomUtil.randomInt(0, totalWeight);
		Server server = serverMap2.get(randomInt);
		return server.getIp();
	}
	
	public static void go2Init() {
		totalWeight = serverMap.values().stream().mapToInt(Server::getWeight).sum();
		int count = 0;
		for (Entry<String, Server> item : serverMap.entrySet()) {
			for (int i = 0; i < item.getValue().getWeight(); i++) {
				serverMap2.put(count, item.getValue());
				count ++;
			}
		}
	}

	public static void main(String[] args) {
		long currentTimeMillis = System.currentTimeMillis();
		for (int i = 0; i < 15; i++) {
			System.out.println(go2());
		}
//		for (int i = 0; i < 15; i++) {
//			System.out.println(go());
//		}
		System.out.println(System.currentTimeMillis() - currentTimeMillis);

	}

}
