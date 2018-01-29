package com.caseyellow.server.central.common;

public class UrlProperty {

    private int size;
    private String url;
    private String md5;

    public UrlProperty() {
    }

    public UrlProperty(String url) {
        this(url, 0, null);
    }

    public UrlProperty(String url, int size, String md5) {
        this.url = url;
        this.size = size;
        this.md5 = md5;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
