package com.example.myapplication;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

// サーバから検索結果を取得する非同期タスク
public class SearchTask extends AsyncTask<String, Void, String> {
    private Listener listener;

    @Override
    protected String doInBackground(String... params) {
        String result = "[]"; // デフォルトは空配列

        try {
            // URL パラメータをエンコード
            String valueParam = URLEncoder.encode(params[0], "UTF-8");

            URL url = new URL(MainActivity.baseURL + "/search?value=" + valueParam);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            try {
                InputStream in = urlConnection.getInputStream();
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    StringBuilder buffer = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    result = buffer.toString(); // 取得した JSON 文字列
                } finally {
                    in.close();
                }
            } finally {
                urlConnection.disconnect();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (listener != null) {
            listener.onSuccess(result); // 結果を呼び出し元に返す
        }
    }

    // リスナーを設定
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    // 結果受け取り用コールバックインターフェース
    public interface Listener {
        void onSuccess(String result);
    }
}
