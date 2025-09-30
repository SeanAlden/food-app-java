// app/src/main/java/com/example/foodapp_java/network/model/Article.java
package com.example.foodapp_java.dataClass;

import java.io.Serializable;

public class Article implements Serializable {
    public static class Source implements Serializable {
        private String id;
        private String name;
        public String getId() { return id; }
        public String getName() { return name; }
    }

    private Source source;
    private String author;
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String publishedAt;
    private String content;

    public Source getSource() { return source; }
    public String getAuthor() { return author; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getUrl() { return url; }
    public String getUrlToImage() { return urlToImage; }
    public String getPublishedAt() { return publishedAt; }
    public String getContent() { return content; }
}
