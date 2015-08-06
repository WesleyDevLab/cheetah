package com.zhaijiong.stock;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by eryk on 2015/7/4.
 */
public class Utils {

    public static String YAHOO_DATA_STYLE = "yyyy-MM-dd";

    public static String SIMLE_DATA_STYLE = "yyyy-MM-dd hh:mm:ss";

    public static final Charset UTF8 = Charset.forName("UTF-8");

    public static SimpleDateFormat sdf = new SimpleDateFormat("hh:MM:ss");

    public static String now(){
        Date date = new Date();
        return sdf.format(date);
    }

    public Set<String> getUserList(String filePath){
        File userFile = new File(filePath);
        Set<String> userSet = Sets.newHashSet();
        try {
            if(userFile.exists() && userFile.isFile()){
                userSet.addAll(Files.readLines(userFile, Charset.forName("utf-8")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userSet;
    }

    public static List<URL> findResources(String name) throws IOException {
        List<URL> urls = new ArrayList<URL>();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> res = cl.getResources(name);
        while (res.hasMoreElements())
            urls.add(res.nextElement());
        return urls;
    }

    public static Integer getInt(Object o) {
        if (o == null)
            return 0;
        else if (o instanceof Long)
            return ((Long) o).intValue();
        else if (o instanceof Integer)
            return (Integer) o;
        else if (o instanceof Short)
            return ((Short) o).intValue();
        else
            throw new IllegalArgumentException("Don't know how to convert " + o
                    + " + to integer");
    }

    public static Long getLong(Object o) {
        if (o == null)
            return 0l;
        else if (o instanceof Long)
            return (Long) o;
        else if (o instanceof Integer)
            return ((Integer) o).longValue();
        else if (o instanceof Short)
            return ((Short) o).longValue();
        else
            throw new IllegalArgumentException("Don't know how to convert " + o
                    + " + to long integer");
    }

    static Double getDouble(Object o) {
        if (o == null)
            return 0.0;
        else if (o instanceof Long)
            return ((Long) o).doubleValue();
        else if (o instanceof Integer)
            return ((Integer) o).doubleValue();
        else if (o instanceof Short)
            return ((Short) o).doubleValue();
        else if (o instanceof Float)
            return ((Float) o).doubleValue();
        else if (o instanceof Double)
            return (Double) o;
        throw new IllegalArgumentException("Don't know how to convert " + o
                + " to double");
    }

    public static Map readYamlConf(String name, boolean asResource)
            throws IOException {
        InputStream input = null;
        try {
            if (asResource) {
                List<URL> urls = findResources(name);
                if (urls.isEmpty())
                    throw new IOException("Resource `" + name + "' not found");
                else if (urls.size() > 1)
                    throw new IOException("Multiple resources `" + name
                            + "' found");
                else
                    input = urls.get(0).openStream();
            } else
                input = new FileInputStream(name);
            return readYamlConf(input);
        } finally {
            if (input != null)
                input.close();
        }
    }

    public static Map readYamlConf(InputStream input) throws IOException {
        InputStreamReader reader = new InputStreamReader(input, UTF8);
        Yaml yaml = new Yaml();
        Map conf = (Map) yaml.load(reader);
        return conf == null ? new HashMap() : conf;
    }


    public static Map loadConf(String file) throws IOException {
        Yaml yaml = new Yaml();
        Map conf = (Map) yaml.load(new FileInputStream(file));
        return conf;
    }

    public static String toStr(Object obj){
        if(obj !=null){
            return String.valueOf(obj);
        }
        return "";
    }

    public static String getStrOrEmpty(Map conf,String key){
        Preconditions.checkNotNull(conf);
        Preconditions.checkNotNull(key);
        return toStr(conf.get(key));
    }

    public static Date parseDate(String date){
        return DateTimeFormat.forPattern(YAHOO_DATA_STYLE).parseDateTime(date).toDate();
    }


    public static String formatDate(Date date){
        DateTime dateTime = new DateTime(date);
        return dateTime.toString("yyyy/MM/dd hh:mm:ss");
    }

    public static String formatDate(Date date,String format){
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(format);
    }

    public static double parseDouble(String numStr){

        if(Strings.isNullOrEmpty(numStr)){
            return 0d;
        }else{
            return Double.parseDouble(numStr);
        }
    }

    public static double formatDouble(double num){
        return formatDouble(num,"#.####");
    }

    public static double formatDouble(double num,String format){
        DecimalFormat df=new DecimalFormat(format);
        return Double.parseDouble(df.format(num));
    }

    public static long parseLong(String numStr){
        if(Strings.isNullOrEmpty(numStr)){
            return 0l;
        }else{
            return  Long.parseLong(numStr);
        }
    }

    /**
     * 雅虎股票接口需要将股票代码后面加上.ss或者.sz
     * @param symbol
     * @return
     */
    public static String yahooSymbol(String symbol){
        if(Strings.isNullOrEmpty(symbol) && symbol.length() != 6){
            return "";
        }
        if(symbol.startsWith("0") || symbol.startsWith("3")){
            return symbol + ".sz";
        }
        if(symbol.startsWith("6")){
            return symbol + ".ss";
        }
        return "";
    }

    public static String netEaseSymbol(String symbol){
        if(Strings.isNullOrEmpty(symbol) && symbol.length() != 6){
            return "";
        }
        if(symbol.startsWith("6")){
            return "0" + symbol;
        }
        if(symbol.startsWith("0") || symbol.startsWith("3")){
            return "1" + symbol;
        }
        return "";
    }
}
