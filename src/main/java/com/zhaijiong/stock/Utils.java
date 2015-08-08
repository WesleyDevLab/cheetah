package com.zhaijiong.stock;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import org.apache.hadoop.hbase.util.Bytes;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.zhaijiong.stock.Constants.UTF8;

/**
 * Created by eryk on 2015/7/4.
 */
public class Utils {

    public static String YAHOO_DATA_STYLE = "yyyy-MM-dd";

    public static String SIMLE_DATA_STYLE = "yyyy-MM-dd hh:mm:ss";


    public static SimpleDateFormat sdf = new SimpleDateFormat("hh:MM:ss");

    public static String now(){
        Date date = new Date();
        return sdf.format(date);
    }

    public static Date bytes2Date(byte[] bytes,String pattern){
        DateTimeFormatter format = DateTimeFormat.forPattern(pattern);
        DateTime dateTime = DateTime.parse(Bytes.toString(bytes),format);
        return dateTime.toDate();
    }

    public static String getTomorrow(){
        DateTime dateTime = new DateTime();
        dateTime = dateTime.plusDays(1);
        return dateTime.toString("yyyyMMdd");
    }

    public static String getYesterday(){
        DateTime dateTime = new DateTime();
        dateTime = dateTime.plusDays(-1);
        return dateTime.toString("yyyyMMdd");
    }


    public static byte[] getRowkey(Stock stock) {
        return Bytes.add(stock.symbol.getBytes(),
                Bytes.toBytes(Utils.formatDate(stock.date, "yyyyMMddhhmmss")));
    }

    public static byte[] getRowkeyWithMD5Prefix(Stock stock){
        return Bytes.add(md5Prefix(stock.symbol,4),stock.symbol.getBytes());
    }

    public static byte[] getRowkeyWithMD5Prefix(String symbol){
        return Bytes.add(md5Prefix(symbol,4),symbol.getBytes());
    }

    public static byte[] md5Prefix(String rowkey,int length){
        return Bytes.head(Hashing.md5().hashString(rowkey,UTF8).toString().getBytes(),length);
    }

    /**
     * 从rowkey的bytes中获取symbol和date信息
     * @param rowkey
     * @return
     */
    public static String getStockSymbol(byte[] rowkey){
        if(rowkey.length == 20){
            return Bytes.toString(Bytes.head(rowkey,6));
        }
        if(rowkey.length == 24){ //key with 4 byte md5 prefix
            return Bytes.toString(rowkey).substring(4,10);
        }
        return "";
    }

    public static Date getStockDate(byte[] rowkey){
        return Utils.bytes2Date(Bytes.tail(rowkey,14),"yyyyMMddhhmmss");
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

    public static Class getClass(String className){
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
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
