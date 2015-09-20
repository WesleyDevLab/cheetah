package com.zhaijiong.crawler;


import java.util.Map;

import static com.zhaijiong.crawler.Constants.*;

public class Template {



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
        return (String) items.get(TEMPLATE_DETAIL_URL);
    }

    public Map<String,String> getRules(){
        return (Map<String, String>) items.get(TEMPLATE_RULES);
    }

}
