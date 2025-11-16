// app/src/main/java/com/example/foodapp_java/page/NewsDetailActivity.java
package com.example.foodapp_java.page;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.foodapp_java.R;
import com.example.foodapp_java.dataClass.Article;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NewsDetailActivity extends AppCompatActivity {

    ImageView img;
    TextView tvTitle, tvPublishedAt, tvDesc;
    Button btnReadFull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        img = findViewById(R.id.imgDetail);
        tvTitle = findViewById(R.id.tvTitleDetail);
        tvPublishedAt = findViewById(R.id.tvPublishedAt);
        tvDesc = findViewById(R.id.tvDescDetail);
        btnReadFull = findViewById(R.id.btnReadFull);

        Article a = (Article) getIntent().getSerializableExtra("article");
        if (a != null) {
            tvTitle.setText(a.getTitle() == null ? "-" : a.getTitle());
            tvDesc.setText(a.getDescription() == null ? "-" : a.getDescription());

            String urlImg = a.getUrlToImage();
            Glide.with(this)
                    .load(urlImg)
                    .placeholder(R.drawable.food)
                    .error(R.drawable.food)
                    .into(img);

            String published = a.getPublishedAt();
            tvPublishedAt.setText(formatPublishedAt(published));

            // === Tombol Read Full News ===
            String url = a.getUrl();
            btnReadFull.setOnClickListener(v -> {
                if (url != null && !url.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "No URL available", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private String formatPublishedAt(String iso) {
        if (iso == null) return "-";
        try {
            // contoh ISO: 2025-09-30T12:34:56Z
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            parser.setLenient(true);
            Date d = parser.parse(iso);
            if (d == null) return iso;
            SimpleDateFormat out = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
            return out.format(d);
        } catch (ParseException e) {
            return iso;
        }
    }
}
