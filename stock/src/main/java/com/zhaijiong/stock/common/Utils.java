package com.zhaijiong.stock.common;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.hash.Hashing;
import com.zhaijiong.stock.model.Stock;
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
import java.text.DecimalFormat;
import java.util.*;

import static com.zhaijiong.stock.common.Constants.ROWKEY_DATA_FORMAT;
import static com.zhaijiong.stock.common.Constants.UTF8;

/**
 * Created by eryk on 2015/7/4.
 */
public class Utils {

    public static Date bytes2Date(byte[] bytes,String pattern){
        DateTimeFormatter format = DateTimeFormat.forPattern(pattern);
        DateTime dateTime = DateTime.parse(Bytes.toString(bytes),format);
        return dateTime.toDate();
    }

    public static String getNow(){
        DateTime dateTime = new DateTime();
        return dateTime.toString(ROWKEY_DATA_FORMAT);
    }

    public static String getNow(String pattern){
        DateTime dateTime = new DateTime();
        return dateTime.toString(pattern);
    }

    //TODO 时间取整
    public static String getTomorrow(){
        DateTime dateTime = new DateTime();
        dateTime = dateTime.plusDays(1);
        return dateTime.toString(ROWKEY_DATA_FORMAT);
    }

    public static String getTomorrow(String pattern){
        DateTime dateTime = new DateTime();
        dateTime = dateTime.plusDays(1);
        return dateTime.toString(pattern);
    }

    //TODO 时间取整
    public static String getYesterday(){
        DateTime dateTime = new DateTime();
        dateTime = dateTime.plusDays(-1);
        return dateTime.toString(ROWKEY_DATA_FORMAT);
    }

    public static String getYesterday(String pattern){
        DateTime dateTime = new DateTime();
        dateTime = dateTime.plusDays(-1);
        return dateTime.toString(pattern);
    }

    public static Date parseDate(String date,String format){
        return DateTimeFormat.forPattern(format).parseDateTime(date).toDate();
    }


    public static String formatDate(Date date){
        DateTime dateTime = new DateTime(date);
        return dateTime.toString("yyyy/MM/dd HH:mm:ss");
    }

    public static Date getDailyClosingTime(Date date){
        DateTime dateTime = new DateTime(date);
        dateTime = dateTime.plusHours(15);
        return dateTime.toDate();
    }

    public static String formatDate(Date date,String format){
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(format);
    }

    public static byte[] getRowkeyWithMd5PrefixAndDaySuffix(Stock stock) {
        byte[] md5 = md5Prefix(stock.symbol,4);
        byte[] symbol = Bytes.toBytes(stock.symbol);
        byte[] date = Bytes.toBytes(Utils.formatDate(stock.date, ROWKEY_DATA_FORMAT));
        return Bytes.add(md5,symbol,date);
    }

    public static byte[] getRowkeyWithMd5PrefixAndDateSuffix(String symbol,String date){
        return Bytes.add(getRowkeyWithMD5Prefix(symbol.getBytes()), date.getBytes());
    }

    public static byte[] getRowkeyWithMD5Prefix(Stock stock){
        return Bytes.add(md5Prefix(stock.symbol,4),stock.symbol.getBytes());
    }

    public static byte[] getRowkeyWithMD5Prefix(String symbol){
        return Bytes.add(md5Prefix(symbol,4),symbol.getBytes());
    }

    public static byte[] getRowkeyWithMD5Prefix(byte[] symbol){
        return Bytes.add(md5Prefix(Bytes.toString(symbol),4),symbol);
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
        if(rowkey.length == 22){ //key with 4 byte md5 prefix
            return Bytes.toString(rowkey).substring(4,10);
        }
        return "";
    }

    public static Date getStockDate(byte[] rowkey){
        return Utils.bytes2Date(Bytes.tail(rowkey,12),ROWKEY_DATA_FORMAT);
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
        else if (o instanceof String)
            return Integer.parseInt((String) o);
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
        else if (o instanceof String)
            return Long.parseLong((String)o);
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

    public static Double getDouble(Object o) {
        if (o == null)
            return 0.0;
        else if (o instanceof String)
            return Double.parseDouble((String)o);
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

    public static boolean isNotNullorZero(Double val){
        if(val!=null || val>0){
            return true;
        }else{
            return false;
        }
    }
}
