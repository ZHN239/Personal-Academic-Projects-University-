package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // バックエンドAPIのベースURL
    public static String baseURL="http://10.122.27.44:3000";    //自分のIPに合わせる

    // バーコードスキャン結果を受け取るためのリクエストコード
    private static final int REQUEST_SCAN = 2001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // メイン画面のレイアウトを設定
        setContentView(R.layout.activity_main);
    }

    // キーワードによる検索処理
    public void onSearchButtonKeywordClicked(View view) {
        EditText edit = findViewById(R.id.keywordText);
        String query = edit.getText().toString().trim();

        // 入力が空の場合は処理を行わない
        if (query.isEmpty()) return;

        // 検索結果表示用Activityへ画面遷移
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("QUERY", query);
        startActivity(intent);
    }

    // バーコードスキャン画面を起動
    public void onScanButtonClicked(View view) {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivityForResult(intent, REQUEST_SCAN);
    }

    // スキャン画面から返却された結果を受信
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SCAN && resultCode == RESULT_OK && data != null) {
            String isbn = data.getStringExtra("barcode");

            if (isbn != null && !isbn.isEmpty()) {

                // ISBN文字列から数字とX以外を除去
                isbn = isbn.replaceAll("[^0-9X]", "");

                // 取得したISBNを入力欄に反映
                EditText edit = findViewById(R.id.keywordText);
                edit.setText(isbn);

            }
        }
    }
}
