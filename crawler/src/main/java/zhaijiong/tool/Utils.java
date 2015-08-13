package zhaijiong.tool;

import com.google.common.base.Preconditions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static String parserDomain(String seedURL){
        Pattern p = Pattern.compile("(?<=http://|https://)[\\w\\d.]*", Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(seedURL);
        matcher.find();
        return matcher.group();
    }

    static Properties p;
    public static void setup(String filePath){
        p = new Properties();
        try {
            p.load(new FileInputStream(new File(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String get(String key){
        Preconditions.checkNotNull(p, "can't find config.properties file,run Utils.prepare()");
        return p.getProperty(key);
    }
}
