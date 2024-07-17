package com.so.bytebuddyPryxy;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;

public class Interceptor1 {

	@RuntimeType
	public Object invoke1(@This Object target,
			@AllArguments Object[] args,
			@Origin Method method,
			@SuperCall Callable<?> zuper
			) {
		
		System.out.println("拦截方法名："+method.getName());
//		try {
//			return zuper.call();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return zuper;
		return "代理结果：result";
	}
}
