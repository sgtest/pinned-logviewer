package com.so.algorithm;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Random;

public class LoadBalanceAlogrthm {

	private List<String> serverList = new LinkedList<String>();
	
	static LinkedHashMap<String, Integer> servers;

	static int index;
	/**
	 * 加权轮询算法
	 * @return
	 */
	public static String go() {
		int allWeight = servers.values().stream().mapToInt(a -> a).sum();
		int number = (index++) % allWeight;
		for (Entry<String, Integer> item : servers.entrySet()) {
			if (item.getValue() > number) {
				return item.getKey();
			}
			number -= item.getValue();
		}
		return "";
	}
	/**
	 * 随机加权轮询
	 */
	public static String randomWeighted() {
		 int allWeight = servers.values().stream().mapToInt(a -> a).sum();
	        Integer number = new Random().nextInt(allWeight);
	        for (Entry<String, Integer> item : servers.entrySet()) {
	            if (item.getValue() >= number) {
	                return item.getKey();
	            }
	            number -= item.getValue();
	        }
	        return "";
	}

	public static void main(String[] args) {
		servers = new LinkedHashMap<String, Integer> ();
		servers.put("a", 2);
		servers.put("b", 7);
		servers.put("c", 1);
		for (int i = 0; i < 10; i++) {
			System.out.println(go());

		}

	}

}
