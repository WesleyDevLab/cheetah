package com.show.api.test;

import java.io.File;

import org.junit.Test;

import com.show.api.ShowApiRequest;

public class ShowapiTest {
	String appid="5432";
	String secret="50637f12773b420db5887169cca826e9";
	@Test
	public void ip() {
		String res=new ShowApiRequest("http://route.showapi.com/131-44", appid, secret)
		.addTextPara("code","601886")
		.post();
		System.out.println(res);
	}
	
//	@Test
	public void test() {
		String res=new ShowApiRequest("http://route.showapi.com/27-5", appid, secret)
		 .addTextPara("type","xml2json")
	         .addTextPara("xml","<?xml version=\"1.0\" encoding=\"UTF-8\"?>rn<o><age type=\"number\">12</age><list class=\"array\"><e type=\"number\">1</e><e type=\"number\">2</e><e type=\"number\">3</e></list><name type=\"string\">张三</name></o> ")
		.post();
		System.out.println(res);
	}
	
	public void 车辆违章_参数规则() {
		String res=new ShowApiRequest("http://route.showapi.com/139-121",appid,secret)
	        .addTextPara("preCarNum","蒙L")
	        .post();
		System.out.println(res);
	}
	
	
	public void 车辆违章查询() {
		String res=new ShowApiRequest("http://route.showapi.com/139-117", appid, secret)
		 .addTextPara("carNumber","粤BBT775")
	         .addTextPara("carCode","105103")
	         .addTextPara("carEngineCode","027693")
		.post();
		System.out.println(res);
	}
	@Test
	public void 验证码识别() {
		String res=new ShowApiRequest("http://route.showapi.com/184-1", appid, secret)
		 .addFilePara("image",new File("c:/1.jpg"))
	         .addTextPara("typeId","3040")
		.post();
		System.out.println(res);
	}
	
	public void 车辆违章查罚款() {
		String res=new ShowApiRequest("http://route.showapi.com/139-118", appid, secret)
	         .addTextPara("recordId","0d536c95-0aae-4053-8514-10aaaa15cc66")
		.post();
		System.out.println(res);
	}
	
	public void 稳定代理 () {
		String res=new ShowApiRequest("http://route.showapi.com/22-2", appid, secret)
		.get();
		System.out.println(res);
//		
	}
	

}
