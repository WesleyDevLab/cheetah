package zhaijiong;

import zhaijiong.tool.Utils;
import zhaijiong.tool.XMLConfigFileReader;

import java.util.Map;

public class CrawlerMain {
    private String configfile;
    private Map<String,Map<String,String>> templates;

    public CrawlerMain(String configfile) {
        this.configfile = configfile;
    }

    public void prepare(){
        Utils.setup(configfile);
        templates = XMLConfigFileReader.getTemplate(Utils.get(Constants.TEMPLATE));
    }

    public void start(){
        prepare();
        for(Map.Entry<String,Map<String,String>> seed : templates.entrySet()){

        }
    }
}
