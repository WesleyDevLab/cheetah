package zhaijiong.fetcher;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import zhaijiong.Constants;

import java.io.*;

public abstract class Fetcher {

    public abstract String fetch(String url);

    public String getHTML(String url) throws IOException {
            HttpClient client = new HttpClient();
            GetMethod method = new GetMethod(url);
            client.executeMethod(method);
            InputStream in = new BufferedInputStream(method.getResponseBodyAsStream());
            Reader r = new InputStreamReader(in, Constants.ENCODE);
            StringBuffer buffer = new StringBuffer();
            int c;
            while ((c = r.read()) != -1) {
                buffer.append((char) c);
            }
            r.close();
            in.close();
            method.releaseConnection();
            return buffer.toString();
    }
}
