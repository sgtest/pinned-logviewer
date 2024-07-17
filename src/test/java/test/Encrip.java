package test;

import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.digest.SM3;

public class Encrip {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SM3 sm3 = SmUtil.sm3();
		
		String digestHex = sm3.digestHex("admin");
		System.out.println(digestHex);
	}

}
