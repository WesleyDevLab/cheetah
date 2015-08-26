package com.zhaijiong.crawler;

import com.google.common.collect.Lists;
import com.zhaijiong.bumblebee.crawler.Template;

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
