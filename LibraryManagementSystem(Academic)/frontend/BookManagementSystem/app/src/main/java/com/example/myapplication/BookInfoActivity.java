package com.example.myapplication;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.URL;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

// 書籍詳細情報を表示する Activity
public class BookInfoActivity extends AppCompatActivity {

    public static final String EXTRA_MODE = "mode";
    public static final String MODE_SEARCH = "search";
    public static final String MODE_MYLIST = "mylist";

    private static String title;
    private static ArrayList<String> author;
    private static String publisher;
    private static String id;
    private static String imgLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);

        // Intent から情報を取得
        Intent intent = getIntent();

        title = intent.getStringExtra("title");
        author = intent.getStringArrayListExtra("author");
        publisher = intent.getStringExtra("publisher");
        id = intent.getStringExtra("id");
        imgLink = intent.getStringExtra("imgLink");

        // 著者名をカンマ区切りで結合
        String author_join = author.get(0);
        for(int i = 1; i < author.size(); i++){
            author_join += "," + author.get(i);
        }

        // 画像表示用 ImageView
        ImageView imageView = findViewById(R.id.bookImage);

        // Glide を使って画像を読み込む
        Glide.with(this)
                .load(imgLink)
                .into(imageView);

        String mode = getIntent().getStringExtra(EXTRA_MODE);

        // UI コンポーネントを取得
        TextView titleView = (TextView) findViewById(R.id.titleView);
        TextView authorView = (TextView) findViewById(R.id.authorView);
        TextView publisherView = (TextView) findViewById(R.id.publisherView);
        TextView idView = (TextView) findViewById(R.id.idView);

        // 書籍情報を UI に表示
        titleView.setText("タイトル：" + title);
        authorView.setText("著者：" + author_join);
        publisherView.setText("出版社：" + publisher);
        idView.setText(id);

        Button addToMyListBtn = (Button)findViewById(R.id.addToMyList);
        Button deleteFromMyListBtn = (Button)findViewById(R.id.deleteFromMyList);

        if (MODE_MYLIST.equals(mode)) {
            // マイリスト画面から遷移
            addToMyListBtn.setVisibility(View.GONE);
            deleteFromMyListBtn.setVisibility(View.VISIBLE);
        } else {
            // 検索結果画面から遷移
            addToMyListBtn.setVisibility(View.VISIBLE);
            deleteFromMyListBtn.setVisibility(View.GONE);
        }
    }

    // マイリストに追加する処理
    public void onAddToMyListButtonClicked(View view) {
        new Thread(() -> {
            try {
                String encodedValue = URLEncoder.encode(imgLink, StandardCharsets.UTF_8.toString());

                // サーバーの URL を構築
                URL url = new URL(MainActivity.baseURL + "/myList/add?title=" + title + "&author=" + author + "&publisher=" + publisher + "&id=" + id + "&imgLink=" + encodedValue);

                // HTTP 接続を開く
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);

                // レスポンスコード取得（任意）
                int responseCode = urlConnection.getResponseCode();
                System.out.println("Response code: " + responseCode);

                // 接続を閉じる
                urlConnection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start(); // 子スレッドで実行
    }

    // マイリストから削除する処理
    public void onDeleteFromMyListButtonClicked(View view) {
        new Thread(() -> {
            try {
                String encodedValue = URLEncoder.encode(imgLink, StandardCharsets.UTF_8.toString());

                // サーバーの URL を構築
                URL url = new URL(MainActivity.baseURL + "/myList/delete?title=" + title + "&author=" + author + "&publisher=" + publisher + "&id=" + id + "&imgLink=" + encodedValue);

                // HTTP 接続を開く
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);

                // レスポンスコード取得（任意）
                int responseCode = urlConnection.getResponseCode();
                System.out.println("Response code: " + responseCode);

                // 接続を閉じる
                urlConnection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start(); // 子スレッドで実行
    }
}
