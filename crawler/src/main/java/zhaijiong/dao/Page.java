package zhaijiong.dao;

import com.google.common.base.Objects;
import com.google.gson.Gson;

import java.util.Set;

public class Page {
	private String meta;
	private String title;
	private Set<Image> images;
    private String url;
    private String content;
    private long createTime;
    private long modifyTime;
    private String postIp;
    private String author;
    private String tags;

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getPostIp() {
        return postIp;
    }

    public void setPostIp(String postIp) {
        this.postIp = postIp;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Set<Image> getImages() {
        return images;
    }

    public void setImages(Set<Image> images) {
        this.images = images;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

	@Override
	public int hashCode() {
		return Objects.hashCode(url);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Page){
			Page a = (Page)obj;
			if(url.equals(a.url)){
				return true;
			}
		}
		return false;
	}

    @Override
    public String toString() {
        return "Page{" +
                "meta='" + meta + '\'' +
                ", title='" + title + '\'' +
                ", images=" + new Gson().toJson(images,Set.class) +
                ", url='" + url + '\'' +
                ", content='" + content + '\'' +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                ", postIp='" + postIp + '\'' +
                ", author='" + author + '\'' +
                ", tags='" + tags + '\'' +
                '}';
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }
}
