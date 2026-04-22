package com.example.photoeditor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.photoeditor.manager.LanguageManager;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply language first
        String lang = LanguageManager.getLanguage(this);
        LanguageManager.setLanguage(this, lang);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        // Inset
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        ImageButton back = findViewById(R.id.settings_back);
        ConstraintLayout langItem = findViewById(R.id.item_language);
        ConstraintLayout infoItem = findViewById(R.id.item_info);

        back.setOnClickListener(this);
        langItem.setOnClickListener(this);
        infoItem.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.item_language) {
            showLanguageDialog();
        } else if (id == R.id.item_info) {
            showAppInfoDialog();
        } else if (id == R.id.settings_back) {
            finish();
        }
    }

    // Language selection dialog
    private void showLanguageDialog() {
        String[] langs = {"简体中文", "繁體中文", "English", "日本語"};
        new AlertDialog.Builder(this)
                .setTitle("選擇語言")
                .setItems(langs, (d, w) -> {
                    String code;
                    switch (w) {
                        case 0:
                            code = "zh";
                            break;
                        case 1:
                            code = "zh-rTW";
                            break;
                        case 2:
                            code = "en";
                            break;
                        case 3:
                            code = "ja";
                            break;
                        default:
                            code = "zh";
                    }
                    LanguageManager.setLanguage(this, code);
                    startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                })
                .show();
    }

    // App info dialog
    private void showAppInfoDialog() {
        new AlertDialog.Builder(this)
                .setTitle("關於本APP")
                .setMessage(R.string.information_content)
                .setPositiveButton("確定", null)
                .show();
    }
}