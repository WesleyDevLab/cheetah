package com.zhaijiong.crawler.parse;

import com.google.common.collect.Maps;
import com.zhaijiong.crawler.Config;
import com.zhaijiong.crawler.Page;
import com.zhaijiong.crawler.Template;
import com.zhaijiong.crawler.fetch.Fetcher;
import com.zhaijiong.crawler.fetch.PageFetcher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

public class PageParser implements Parser {

    private Template template;

    public PageParser(Template template){
        this.template = template;
    }

    @Override
    public Map<String,String> parse(Page page) {
        Document doc = Jsoup.parse(page.getSource());

        if(Pattern.matches(template.getListURL(),page.getUrl())){
            Elements elements = doc.select(String.format("a[href=%s]", template.getContentURL()));
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

    public static void main(String[] args) throws IOException {
        Config config = new Config();
        Template template = config.getTemplates().get(0);

        Fetcher fetcher = new PageFetcher();
        Page page =fetcher.fetch("http://stock.10jqka.com.cn/20150831/c581435532.shtml");

        PageParser parser = new PageParser(template);
        Map<String,String> parse = parser.parse(page);
        for(Map.Entry<String,String> entry:parse.entrySet()){
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
    }
}
