package com.so.util;

import javax.money.MonetaryOperator;

import org.javamoney.moneta.Money;
import org.javamoney.moneta.function.MonetaryOperators;

import cn.hutool.core.util.NumberUtil;

public class MonetaryCal {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Double d1 = 1.23d;
		Double d2 = 2.12d;
		Double add = NumberUtil.add(d1,d2);
//		System.out.println(add);
		System.out.println(d2-d1);//double存在精度问题，使用工具类不会
		
		Double d3 = 1d;
		Double d4 = 3d;
//		Double div = NumberUtil.div(d3, d4);//金额如果要平均分配则存在除不尽的现象，所以应该使用money的allocate方法，将剩余零头顺序分配，保证零头不丢失。
//		System.out.println(div);
		
//		moneta工具类库提供的货币计算
		Money money = Money.of(d2, "CNY").with(MonetaryOperators.rounding(6));
		Money money2 = Money.of(d1, "CNY").with(MonetaryOperators.rounding(6));
		System.out.println(money.subtract(money2));//精度存在问题
		
		
	}
	
	

}
