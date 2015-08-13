package zhaijiong.tool;

import com.google.common.collect.Maps;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import zhaijiong.Constants;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: eryk
 * Date: 13-12-28
 * Time: 下午9:46
 * To change this template use File | Settings | File Templates.
 */
public class XMLConfigFileReader {

    public static Map<String, Map<String, String>> getTemplate(String fileName) {
        Map<String, Map<String, String>> pageTemplate = Maps.newHashMap();
        SAXReader saxReader = new SAXReader();
        try {
            Document configFile = saxReader.read(new File(fileName));
            List<Node> templates = configFile.selectNodes(Constants.XML_ROOTPATH);
            for(Node n :templates){
                Element site = (Element)n;
                String domain = site.attributeValue(Constants.XML_PROPERTY_DOMAIN);
                Map<String,String> fetchElement = Maps.newHashMap();
                Iterator iterator = site.elementIterator();
                while(iterator.hasNext()){
                    Element prop = (Element)iterator.next();
                    fetchElement.put(prop.getName(),prop.getText());
                }
                pageTemplate.put(domain,fetchElement);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return pageTemplate;
    }

}
