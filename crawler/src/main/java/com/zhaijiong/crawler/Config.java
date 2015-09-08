package com.zhaijiong.crawler;

import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class Config extends HashMap{
    private static final String TEMPLATES = "site.templates";

    public Config(String name) throws IOException {
        super(Utils.readYamlConf(name, true));
    }

    public Config() throws IOException {
        this("crawler.yaml");
    }

    public String getStr(String key){
        Object o = this.get(key);
        if(o!=null){
            return String.valueOf(o);
        }
        return null;
    }

    public Integer getInt(String key){
        return (Integer) this.get(key);
    }

    public List<Template> getTemplates(){
        List list = (List) get(TEMPLATES);
        List<Template> templates = Lists.newArrayList();
        for(Object site: list){
            Template template =  new Template((java.util.Map) site);
            templates.add(template);
        }
        return templates;
    }

}
