package zhaijiong.fetcher;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: eryk
 * Date: 14-1-4
 * Time: 下午11:54
 * To change this template use File | Settings | File Templates.
 */
public class UrlFetchTest {

    @Test
    public void test(){
        String url = "http://qing.blog.sina.com.cn/tj/b800734f33004h3t.html";
        Assert.assertTrue(Pattern.matches(".*/tj/.*", url));
    }
}
