package com.so.algorithm;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
/**
 * 滑动时间窗口算法
 * 针对某个访问地址的限流示例：
 * 1.创建一个map<String,CurrentLimiting> ，key存放地址，value存放限流对象，
 * 2.根据地址得到对应的限流对象然后调用限流方法计算是否限流；
 * @author Administrator
 *
 */
public class CurrentLimiting {

	private LinkedList<Long> barrel ;
	private Integer timeWindow;//以秒为单位如10秒5次
	private Integer limitCount;

	public CurrentLimiting(Integer timeWindow,Integer limitCount) {
		super();
		barrel = new LinkedList<Long>();
		this.timeWindow = timeWindow;
		this.limitCount = limitCount;
	}

	/**
	 * 计算是否通过的方法-滑动时间窗口算法
	 * @return
	 */
	public boolean calculateLimit() {
		if (barrel.size() < limitCount) {
			barrel.addFirst(System.currentTimeMillis());
			return true;
		}
		//full
		if((System.currentTimeMillis() - barrel.getLast())/1000 <= timeWindow) {
			return false;
		}else {
			barrel.removeLast();
			barrel.addFirst(System.currentTimeMillis());
		}
		return true;
	}
	
	public LinkedList<Long> getBarrel() {
		return barrel;
	}

	public void setBarrel(LinkedList<Long> barrel) {
		this.barrel = barrel;
	}

	public static void main(String[] args) throws InterruptedException {
		CurrentLimiting lim = new CurrentLimiting(10, 5);
		for (int i = 0; i < 6; i++) {
			if (!lim.calculateLimit()) {
				LinkedList<Long> barrel2 = lim.getBarrel();
				System.out.println(barrel2);
			}
		}
	}
	
}
