package com.zhaijiong.crawler;


import java.util.Map;

public class Template {

    public static final String TEMPLATE_NAME = "source";

    public static final String TEMPLATE_CATEGORY = "category";

    public static final String TEMPLATE_START_URL = "start.url";

    public static final String TEMPLATE_LIST_URL = "list.url";

    public static final String TEMPLATE_CONTENT_URL = "content.url";

    public static final String TEMPLATE_RULES = "rules";

    private Map<String,Object> items;

    public Template(Map items){
        this.items = items;
    }

    public String getSourceName(){
        return (String) items.get(TEMPLATE_NAME);
    }

    public String getCategory(){
        return (String) items.get(TEMPLATE_CATEGORY);
    }

    public String getStartURL(){
        return (String) items.get(TEMPLATE_START_URL);
    }

    public String getListURL(){
        return (String) items.get(TEMPLATE_LIST_URL);
    }

    public String getContentURL(){
        return (String) items.get(TEMPLATE_CONTENT_URL);
    }

    public Map<String,String> getRules(){
        return (Map<String, String>) items.get(TEMPLATE_RULES);
    }

}
