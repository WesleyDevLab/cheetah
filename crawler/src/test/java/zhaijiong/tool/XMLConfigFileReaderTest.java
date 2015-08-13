package zhaijiong.tool;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * XMLConfigFileReader Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>十二月 29, 2013</pre>
 */
public class XMLConfigFileReaderTest {

    /**
     * Method: getTemplate(String fileName)
     */
    @Test
    public void testGetTemplate() throws Exception {
        Map<String, Map<String, String>> templates;
        templates = XMLConfigFileReader.getTemplate("crawler/src/main/resources/page.xml");
        Assert.assertTrue(templates.size() != 0);
        for(Map.Entry<String,String> entry:templates.get("qing.blog.sina.com.cn").entrySet()){
            System.out.println(entry.getKey()+":"+entry.getValue());
        }

    }
}
