package zhaijiong.dao;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

public class Image {
	private String src;

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    private String alt;
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Image){
			Image a = (Image)obj;
			if(ComparisonChain.start().compare(src, a.src).result()==0){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(src);
	}

}
