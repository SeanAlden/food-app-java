// app/src/main/java/com/example/foodapp_java/network/model/NewsResponse.java
package com.example.foodapp_java.api;

import com.example.foodapp_java.dataClass.Article;

import java.util.List;

public class NewsResponse {
    private String status;
    private int totalResults;
    private List<Article> articles;

    public String getStatus() { return status; }
    public int getTotalResults() { return totalResults; }
    public List<Article> getArticles() { return articles; }
}
