package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.SimpleAdapter;
import android.widget.EditText;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

public class ResultActivity extends AppCompatActivity {

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
        // レイアウトを設定
        setContentView(R.layout.activity_result);

        // キーワード入力画面から渡された検索クエリを取得
        Intent intent = getIntent();
        String query = intent.getStringExtra("QUERY");

        // 検索結果のタイトル表示用 TextView
        TextView textView = findViewById(R.id.ResultTitle);
        if (query != null && !query.trim().isEmpty()) {
            // 入力された検索語をタイトルに表示
            textView.setText("『" + query + "』の検索結果：");
        } else {
            // デフォルトタイトル表示
            textView.setText("検索結果：");
        }

        // ListView に表示するデータ格納用
        final ArrayList<HashMap<String, String>> listData = new ArrayList<>();

        // 非同期でサーバ通信を行うタスクを作成
        SearchTask task = new SearchTask();
        // 通信成功時の処理を設定
        task.setListener(new SearchTask.Listener() {
            @Override
            public void onSuccess(String result) {
                listData.clear();
                final ArrayList<BookInfo> bookList = new ArrayList<>();

                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        final BookInfo bookInfo = new BookInfo();
                        bookInfo.title = jsonObject.getString("title");

                        // 作者情報を解析してリストに格納
                        bookInfo.author.clear();
                        JSONArray authorArray = jsonObject.getJSONArray("author");
                        for (int j = 0; j < authorArray.length(); j++) {
                            bookInfo.author.add(authorArray.getString(j));
                        }

                        if (jsonObject.has("publisher")) {
                            bookInfo.publisher = jsonObject.getString("publisher");
                        }

                        bookInfo.id = jsonObject.getString("id");
                        bookInfo.imgLink = jsonObject.getString("imgLink");
                        bookList.add(bookInfo);

                        // ListView 用の HashMap を作成
                        HashMap<String, String> map = new HashMap<>();
                        map.put("title", bookInfo.title);

                        // 作者配列をカンマ区切りで表示
                        map.put("author", String.join(", ", bookInfo.author));
                        listData.add(map);
                    }

                } catch (JSONException e) {
                    Log.d("SearchTask", "err result: " + e.getMessage());
                    e.printStackTrace();
                    TextView resultTitle = findViewById(R.id.ResultTitle);
                    if (query != null && !query.trim().isEmpty()) {
                        resultTitle.setText("『" + query + "』の検索結果がエラー");
                    } else {
                        resultTitle.setText("検索結果がエラー");
                    }
                    return;
                }

                // 検索結果が空の場合のメッセージ
                if (bookList.isEmpty()) {
                    TextView resultTitle = findViewById(R.id.ResultTitle);
                    if (query != null && !query.trim().isEmpty()) {
                        resultTitle.setText("『" + query + "』が見つかりませんでした");
                    } else {
                        resultTitle.setText("見つかりませんでした");
                    }
                }

                // SimpleAdapter で ListView に表示
                SimpleAdapter adapter = new SimpleAdapter(ResultActivity.this,
                        listData,
                        android.R.layout.simple_list_item_2,
                        new String[]{"title", "author"},
                        new int[]{android.R.id.text1, android.R.id.text2}
                );

                ListView listView = findViewById(R.id.listView);
                listView.setAdapter(adapter);

                // 各アイテムクリック時に詳細画面へ遷移
                listView.setOnItemClickListener((parent, view, position, id) -> {
                    Intent intent = new Intent(ResultActivity.this, BookInfoActivity.class);
                    BookInfo bookInfo = bookList.get(position);

                    intent.putExtra(BookInfoActivity.EXTRA_MODE, BookInfoActivity.MODE_SEARCH);
                    intent.putExtra("title", bookInfo.title);
                    intent.putStringArrayListExtra("author", bookInfo.author);
                    intent.putExtra("publisher", bookInfo.publisher);
                    intent.putExtra("id", bookInfo.id);
                    intent.putExtra("imgLink", bookInfo.imgLink);

                    startActivity(intent);
                });
            }
        });

        // サーバとの通信を非同期で開始
        task.execute(query);
    }
}
