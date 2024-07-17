package com.so.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.so.util.Util;

/**
 * 接口测试
 * 
 * @author Administrator
 *
 */
@RestController
public class TestController {

	private static final Logger log = LoggerFactory.getLogger(TestController.class);

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public String name(String id, String name) {
		System.out.println(id + name);
//		Integer executeSellScript = Util.executeSellScript("./startup.sh","/root/apache-tomcat-9.0.36/bin");
//		log.info(executeSellScript+"");
		return "test";
	}

//	@GetMapping(value = "/test")
//	public String getname(String id, String name) {
//		System.out.println(id + name);
//		return "test";
//	}

	@RequestMapping(value = "/test", method = RequestMethod.POST)
	public String testPost(@RequestParam(value = "id", required = true, defaultValue = "defaultvalue") String post, String name) {
		System.out.println(post + name);
		return "testpost";
	}

	// http://localhost:9999/op/fetch/888
	@RequestMapping(value = "/fetch/{id}", method = RequestMethod.GET)
	String getDynamicUriValue(@PathVariable String id) {
		System.out.println("ID is " + id);
		return "Dynamic URI parameter fetched";
	}

	// http://localhost:9999/op/fetch/adb/999
	@RequestMapping(value = "/fetch/{id:[a-z]+}/{name}", method = RequestMethod.GET)
	String getDynamicUriValueRegex(@PathVariable("id") String id, @PathVariable("name") String name) {
		System.out.println(id + "Name is " + name);
		return "Dynamic URI parameter fetched using regex";
	}


}
