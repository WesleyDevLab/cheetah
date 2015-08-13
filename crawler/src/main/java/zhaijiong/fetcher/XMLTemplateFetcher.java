package zhaijiong.fetcher;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import zhaijiong.Constants;
import zhaijiong.dao.Image;
import zhaijiong.dao.Page;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class XMLTemplateFetcher extends Fetcher implements PageTemplate {
    private Document doc;
    private final Map<String, String> properties;

    public XMLTemplateFetcher(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public String fetch(String url) {
        try {
            doc = Jsoup.parse(getHTML(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Page page = parserStr2Page();
        return new Gson().toJson(page, Page.class);
    }

    private Page parserStr2Page() {
        Page page = new Page();
        if (!Strings.isNullOrEmpty(getAuthor()))
            page.setAuthor(getAuthor());
        if (!Strings.isNullOrEmpty(getContent()))
            page.setContent(getContent());
        page.setCreateTime(new Date().getTime());
        if (getImageList().size() != 0) {
            page.setImages(getImageList());
        }
        if (!Strings.isNullOrEmpty(getMeta()))
            page.setMeta(getMeta());
        if (!Strings.isNullOrEmpty(getTags()))
            page.setTags(getTags());
        if (!Strings.isNullOrEmpty(getURL()))
            page.setUrl(getURL());
        if (!Strings.isNullOrEmpty(getTitle()))
            page.setTitle(getTitle());
        return page;
    }

    @Override
    public String getTitle() {
        return doc.title();
    }

    @Override
    public String getAuthor() {
        String author = properties.get(Constants.XML_PROPERTY_AUTHOR);
        if(Strings.isNullOrEmpty(author)){
            return "";
        }
        Elements authorNode = doc.select(author);
        Preconditions.checkNotNull(authorNode, "author info tag don't exist");
        return authorNode.html();
    }

    @Override
    public String getMeta() {
        String meta = properties.get(Constants.XML_PROPERTY_KEYWORD);
        if(Strings.isNullOrEmpty(meta)){
            return "";
        }
        Elements metaNode = doc.select(meta);
        Preconditions.checkNotNull(metaNode, "meta info tag don't exist");
        return metaNode.attr(Constants.XML_PROPERTY_CONTENT);
    }

    @Override
    public String getContent() {
        String content = properties.get(Constants.XML_PROPERTY_CONTENT);
        if(Strings.isNullOrEmpty(content)){
            return "";
        }
        Elements contentNode = doc.select(content);
        Preconditions.checkNotNull(contentNode, "content tag don't exist");
        return contentNode.text();
    }

    @Override
    public Set<Image> getImageList() {
        String image = properties.get(Constants.XML_PROPERTY_IMAGES);
        if(Strings.isNullOrEmpty(image)){
            return Sets.newHashSet();
        }
        String imageHTML = doc.select(image).html();
        Preconditions.checkNotNull(imageHTML, "images tag don't exist");
        Set<Image> images = getImages(imageHTML);
        return images;
    }

    @Override
    public String getTags() {
        String tags = properties.get(Constants.XML_PROPERTY_TAGS);
        if(Strings.isNullOrEmpty(tags)){
            return "";
        }
        Elements tagsNode = doc.select(tags);
        Preconditions.checkNotNull(tagsNode, "tags tag don't exist");
        return tagsNode.html();
    }

    @Override
    public String getDomain() {
        String domain = properties.get(Constants.XML_PROPERTY_DOMAIN);
        if(Strings.isNullOrEmpty(domain)){
            return "";
        }
        return properties.get(Constants.XML_PROPERTY_DOMAIN);
    }

    @Override
    public String getURL() {
        return doc.location();
    }

    @Override
    public String getComment() {
        String comment = properties.get(Constants.XML_PROPERTY_COMMENT);
        if(Strings.isNullOrEmpty(comment)){
            return "";
        }
        Elements commentNode = doc.select(comment);
        Preconditions.checkNotNull(commentNode, "comment tag don't exist");
        return commentNode.html();
    }

    public Set<Image> getImages(String content) {
        Set<Image> images = new HashSet<Image>();
        try {
            Document html = Jsoup.parse(content);
            Elements elements = html.select("img");
            for (Element e : elements) {
                Image image = new Image();
                image.setAlt(e.attr("alt"));
                image.setSrc(e.attr("src"));
                images.add(image);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return images;
    }
}
