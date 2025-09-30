// app/src/main/java/com/example/foodapp_java/network/NewsService.java
package com.example.foodapp_java.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsService {
    // gunakan 'q' untuk kata kunci, pageSize untuk jumlah berita
    @GET("v2/everything")
    Call<NewsResponse> searchArticles(
            @Query("q") String q,
            @Query("pageSize") int pageSize,
            @Query("apiKey") String apiKey,
            @Query("language") String language // optional: "en" atau "id"
    );
}
