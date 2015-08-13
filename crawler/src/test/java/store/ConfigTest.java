package store;

import com.zhaijiong.bumblebee.crawler.Template;
import com.zhaijiong.bumblebee.utils.Config;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ConfigTest {

    @Test
    public void testGetTemplates() throws Exception {
        try {
            Config conf = new Config("crawler.yaml");
            List templates = conf.getTemplates();
            for(Object site: templates){
                Template template =  new Template((java.util.Map) site);
                System.out.println(template.getSourceName());
                System.out.println(template.getCategory());
                System.out.println(template.getStartURL());
                System.out.println(template.getListURL());
                System.out.println(template.getContentURL());
                Map<String, String> rules = template.getRules();
                for(Map.Entry<String,String> rule: rules.entrySet()){
                    System.out.println(rule.getKey()+":"+rule.getValue());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}