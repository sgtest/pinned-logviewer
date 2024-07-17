package com.so.util.consistencehash;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 一致性哈希算法
 */
public class ConsistentHash {

    //一致性hash简单实现
    //1 确定区间值
    //2 节点散列到区间值（物理节点+虚拟节点，虚拟节点解决数据倾斜不均匀（1 本身节点散列不均匀；2 添加删除节点不均匀））
    //3 元素散列到区间值
    //4 顺时针获取下一个节点位置
    //虚拟节点的个数，经验：物理节点个位数时，每个物理节点160个虚拟节点时，数据会分散很平均
    //上述实现原理中， 元素散列比节点散列频繁的多，即读多写少， 使用链表维护区间值圆环效率不高，二叉树查找非常适合，使用TreeMap-红黑树，按照key排序，查找效率高

    //物理节点s
    private List<String> nodes = null;
    //使用TreeMap存储所有节点（物理+虚拟），有序且查找效率高
    private SortedMap<Long,String> virtualNodes = null;
    //每个物理节点应该对应的虚拟节点shu
    private int virtual = 160;

    public ConsistentHash(String[] nodeNames){
        this.nodes = new ArrayList<>(Arrays.asList(nodeNames));
        this.refreshHashCircle();
    }

    /**
     * 物理节点变动后，刷新节点和圆环区间的映射
     */
    private void refreshHashCircle(){
        if(virtualNodes == null){
            virtualNodes = new TreeMap<>();
        } else {
            virtualNodes.clear();
        }
        for(String node:nodes){
            for(int i = 0;i < virtual;i++){
                String nn = "pn"+node + "&&vm"+i;
                long hash = getHash(nn);
                virtualNodes.put(hash,node);
            }
        }
    }

    /**
     * 添加一个物理节点，重新构建节点环
     * @param nodeName  新节点
     */
    public void addNode(String nodeName){
        nodes.add(nodeName);
        this.refreshHashCircle();
    }

    /**
     * 删除一个物理节点，重新构建节点环
     * @param nodeName 旧节点
     */
    public void removeNode(String nodeName) {
        Iterator<String> iterator = nodes.iterator();
        while(iterator.hasNext()){
            String sss = iterator.next();
            if(sss.equals(nodeName))
                iterator.remove();
        }
        this.refreshHashCircle();
    }
    
    public static void main(String[] args) {
		ConsistentHash consistentHash = new ConsistentHash(new String [] {"asa","dksas"});
		consistentHash.addNode("sdfaa");
		String node = consistentHash.getNode("fasdfsasaaa");
		System.out.println(node);
		
	}

    /**
     * 获取服务节点
     * @param key 请求关键字
     * @return  服务节点
     */
    public String getNode(String key){
        long hash = getHash(key);
        SortedMap<Long, String> subMap = virtualNodes.tailMap(hash);
        if (subMap == null || subMap.isEmpty()) {
            return virtualNodes.get(virtualNodes.firstKey());
        }
        String zzz = subMap.get(subMap.firstKey());
        return zzz;
    }

    public static long getHash(String str) {
        final int p = 16777619;
        int hash = (int)2166136261L;
        for (int i = 0; i < str.length(); i++) {
            hash =( hash ^ str.charAt(i) ) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;

        if (hash < 0) {
            hash = Math.abs(hash);
        }
        return hash;
    }
}