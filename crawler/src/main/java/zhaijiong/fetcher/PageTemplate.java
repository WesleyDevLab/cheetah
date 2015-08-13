package zhaijiong.fetcher;

import zhaijiong.dao.Image;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: eryk
 * Date: 13-12-28
 * Time: 下午9:27
 * To change this template use File | Settings | File Templates.
 */
public interface PageTemplate {
    public String getTitle();

    public String getAuthor();

    public String getMeta();

    public String getContent();

    public Set<Image> getImageList();

    public String getTags();

    public String getDomain();

    public String getURL();

    public String getComment();
}
