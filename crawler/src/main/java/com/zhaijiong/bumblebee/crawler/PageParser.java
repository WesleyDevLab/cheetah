package com.zhaijiong.bumblebee.crawler;

import com.google.common.collect.Maps;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Map;
import java.util.regex.Pattern;

public class PageParser implements Parser{

    private Template template;

    public PageParser(Template template){
        this.template = template;
    }

    @Override
    public Map parse(Page page) {
        Document doc = Jsoup.parse(page.getSourceHTML());

        Elements elements;
        if(Pattern.matches(template.getListURL(),page.getUrl())){
            elements = doc.select(String.format("a[href=%s]", template.getContentURL()));
            //TODO 查重 并且 add url to redis queue
            return null;
        }else if(Pattern.matches(template.getContentURL(),page.getUrl())){

            Map<String,String> content = Maps.newHashMap();
            content.put(Template.TEMPLATE_NAME,template.getSourceName());
            content.put(Template.TEMPLATE_CATEGORY,template.getCategory());

            Map<String, String> rules = template.getRules();
            for(Map.Entry<String,String> rule: rules.entrySet()){
                Element element = doc.select(rule.getValue()).get(0);
                content.put(rule.getKey(),element.text());
            }
            return content;
        }
        return null;
    }
}
