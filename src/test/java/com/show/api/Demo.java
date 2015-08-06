package com.show.api;

public class Demo {

	public static void main(String adfas[]) throws  Exception{ 
		   String res=new ShowApiRequest("http://route.showapi.com/6-1","23","187f6d7af5cd4207a83ce5c3a963af60")
		   .addTextPara("num","13629476846")
		   .post();
		System.out.println(res);
		
	}
}
