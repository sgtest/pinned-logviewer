package com.so.bytebuddyPryxy;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType.Loaded;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

public class Test {

	public static void main(String[] args) throws NoSuchMethodException, SecurityException, IOException {
		Unloaded<Object> unloaded = new ByteBuddy()
		.subclass(Object.class)
		.name("InterTest1")
//		.method(ElementMatchers.named("toString"))
//		.intercept(FixedValue.nullValue())
		.implement(InterTest.class)
		.method(ElementMatchers.named("getUserName"))//.intercept(FixedValue.value("dalks"))
		.intercept(MethodDelegation.to(new Interceptor1()))
		.make();
		Loaded<Object> load = unloaded.load(unloaded.getClass().getClassLoader());
		load.saveIn(new File("d:\\"));
		Class<? extends Object> loaded = load.getLoaded();
		Object newInstance;
		
		try {
			newInstance = loaded.newInstance();
			Method method = newInstance.getClass().getMethod("getUserName");
			
			Object invoke = method.invoke(newInstance);
			System.out.println(invoke);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
