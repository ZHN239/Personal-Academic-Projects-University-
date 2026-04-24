package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

// マイリスト（お気に入り）を表示する Activity
public class MyList extends AppCompatActivity {

    private ListView listView;

    // ListView 表示用のデータ
    private ArrayList<HashMap<String, String>> listData = new ArrayList<>();
    // クリック時に取得するフルデータ
    private ArrayList<BookInfo> bookList = new ArrayList<>();

    // 書籍情報を格納する内部クラス
    private class BookInfo {
        String title;
        ArrayList<String> author = new ArrayList<>();
        String publisher;
        String id;
        String imgLink;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // システムバー考慮の全画面設定
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_list);

        listView = findViewById(R.id.listView);

        reloadMyList(); // 初回読み込み

        // システムバー領域分の余白を調整
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadMyList(); // BookInfoActivity から戻った時に再読み込み
    }

    // マイリストをサーバから取得して ListView に表示
    private void reloadMyList() {
        new Thread(() -> {
            try {
                URL url = new URL(MainActivity.baseURL + "/myList/display");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream())
                    );
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    reader.close();

                    JSONArray jsonArray = new JSONArray(sb.toString());

                    listData.clear();
                    bookList.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);

                        BookInfo book = new BookInfo();
                        book.title = obj.getString("title");

                        JSONArray authorArray = obj.getJSONArray("author");
                        for (int j = 0; j < authorArray.length(); j++) {
                            book.author.add(authorArray.getString(j));
                        }

                        book.publisher = obj.optString("publisher");
                        book.id = obj.getString("id");
                        book.imgLink = obj.optString("imgLink");

                        bookList.add(book);

                        HashMap<String, String> map = new HashMap<>();
                        map.put("title", book.title);
                        map.put("author", String.join(", ", book.author));
                        listData.add(map);
                    }

                    runOnUiThread(() -> {
                        // SimpleAdapter で ListView に表示
                        SimpleAdapter adapter = new SimpleAdapter(
                                MyList.this,
                                listData,
                                android.R.layout.simple_list_item_2,
                                new String[]{"title", "author"},
                                new int[]{android.R.id.text1, android.R.id.text2}
                        );

                        listView.setAdapter(adapter);

                        // アイテムクリック時に詳細画面へ遷移
                        listView.setOnItemClickListener((parent, view, position, id) -> {
                            BookInfo book = bookList.get(position);

                            Intent intent = new Intent(MyList.this, BookInfoActivity.class);
                            intent.putExtra(BookInfoActivity.EXTRA_MODE,
                                    BookInfoActivity.MODE_MYLIST);

                            intent.putExtra("title", book.title);
                            intent.putStringArrayListExtra("author", book.author);
                            intent.putExtra("publisher", book.publisher);
                            intent.putExtra("id", book.id);
                            intent.putExtra("imgLink", book.imgLink);

                            startActivity(intent);
                        });
                    });
                }

                conn.disconnect();
            } catch (Exception e) {
                Log.e("MyList", "reloadMyList error", e);
            }
        }).start();
    }
}
