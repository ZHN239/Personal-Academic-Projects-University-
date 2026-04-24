package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// 画面遷移の起点となるナビゲーション用 Activity
public class navigation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // システムバー（ステータスバー／ナビゲーションバー）を考慮した全画面表示を有効化
        EdgeToEdge.enable(this);

        // レイアウトXMLをActivityに関連付け
        setContentView(R.layout.activity_navigation);

        // システムバーの領域分だけ余白（padding）を動的に調整
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // 検索画面（MainActivity）へ遷移するボタンのクリックハンドラ
    public void onSearchButtonClicked(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // マイリスト画面（MyList）へ遷移するボタンのクリックハンドラ
    public void onMyListButtonClicked(View view){
        Intent intent = new Intent(this, MyList.class);
        startActivity(intent);
    }

}
