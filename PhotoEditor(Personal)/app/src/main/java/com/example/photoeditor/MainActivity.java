package com.example.photoeditor;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.photoeditor.adapter.PhotoAdapter;
import com.example.photoeditor.manager.DateManager;
import com.example.photoeditor.manager.ImageManager;
import com.example.photoeditor.manager.LanguageManager;
import com.example.photoeditor.manager.PermissionManager;
import com.example.photoeditor.model.ImageModel;
import com.google.android.material.button.MaterialButton;

import android.app.DatePickerDialog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_PHOTO_PERMISSION = 1001;

    private DateManager dateManager;
    private RecyclerView recyclerView;
    private MaterialButton requestPermissionButton;
    private PhotoAdapter photoAdapter;
    private ImageManager imageManager;
    private ImageView photoDisplay;
    private MaterialButton dateSelector;
    private ImageModel curImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply language setting before creating view
        String language = LanguageManager.getLanguage(this);
        LanguageManager.setLanguage(this, language);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Apply system bar insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize managers
        dateManager = new DateManager();
        imageManager = ImageManager.getInstance();

        // Initialize views
        photoDisplay = findViewById(R.id.photo_display);
        recyclerView = findViewById(R.id.img_list);
        requestPermissionButton = findViewById(R.id.request_permission_button);
        MaterialButton previousDay = findViewById(R.id.previous_day_btn);
        MaterialButton nextDay = findViewById(R.id.next_day_btn);
        dateSelector = findViewById(R.id.date_selector);
        CheckBox allDays = findViewById(R.id.all_days);
        ImageButton exit = findViewById(R.id.exit);
        ImageButton settings = findViewById(R.id.settings);

        // Set listeners
        exit.setOnClickListener(this);
        settings.setOnClickListener(this);
        requestPermissionButton.setOnClickListener(this);
        photoDisplay.setOnClickListener(this);

        // All days checkbox logic
        allDays.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                dateSelector.setOnClickListener(this);
                previousDay.setOnClickListener(this);
                nextDay.setOnClickListener(this);
                dateSelector.setText(dateManager.getDate());
                imageManager.setFilteredList(dateManager.getDate());
            } else {
                dateSelector.setClickable(false);
                previousDay.setClickable(false);
                nextDay.setClickable(false);
                dateSelector.setText(R.string.all_days);
                dateManager.resetCalendar();
                imageManager.getFilteredList().clear();
                imageManager.getFilteredList().addAll(imageManager.getOriginalList());
            }
            photoAdapter.notifyDataSetChanged();
            updateMainPreview();
        });

        // Load photos if permission granted
        if (PermissionManager.hasPhotoPermission(this)) {
            renderIfGranted();
        }
    }

    // Update main preview image
    private void updateMainPreview() {
        if (imageManager.getFilteredList().isEmpty()) {
            photoDisplay.setImageResource(0);
            photoDisplay.setClickable(false);
        } else {
            curImage = imageManager.getFilteredList().get(0);
            photoDisplay.setClickable(true);
            Glide.with(this).load(curImage.getUri()).centerCrop().into(photoDisplay);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PHOTO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                renderIfGranted();
            } else {
                Toast.makeText(this, R.string.permission_request, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.photo_display) {
            imageManager.setCurImage(curImage);
            startActivity(new Intent(this, EditImageActivity.class));
        } else if (id == R.id.previous_day_btn) {
            dateManager.previousDay();
            dateSelector.setText(dateManager.getDate());
            imageManager.setFilteredList(dateManager.getDate());
            photoAdapter.notifyDataSetChanged();
            updateMainPreview();
        } else if (id == R.id.next_day_btn) {
            dateManager.nextDay();
            dateSelector.setText(dateManager.getDate());
            imageManager.setFilteredList(dateManager.getDate());
            photoAdapter.notifyDataSetChanged();
            updateMainPreview();
        } else if (id == R.id.date_selector) {
            showDatePickerDialog(dateManager.getDate());
        } else if (id == R.id.settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.exit) {
            finishAndRemoveTask();
        } else if (id == R.id.request_permission_button) {
            PermissionManager.requestPhotoPermission(this);
        }
    }

    // Show date picker dialog
    private void showDatePickerDialog(String rawDate) {
        String[] date = rawDate.split("-");
        int year = Integer.parseInt(date[0]);
        int month = Integer.parseInt(date[1]) - 1;
        int day = Integer.parseInt(date[2]);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
            String selected = y + "-" + (m + 1) + "-" + d;
            dateManager.setDate(selected);
            dateSelector.setText(dateManager.getDate());
            imageManager.setFilteredList(dateManager.getDate());
            photoAdapter.notifyDataSetChanged();
            updateMainPreview();
            Toast.makeText(MainActivity.this, "Date: " + dateManager.getDate(), Toast.LENGTH_SHORT).show();
        }, year, month, day);
        dialog.show();
    }

    // Initialize UI when permission is granted
    private void renderIfGranted() {
        recyclerView.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        requestPermissionButton.setVisibility(View.GONE);
        imageManager.loadDevicePhotos(this);

        imageManager.getFilteredList().clear();
        imageManager.getFilteredList().addAll(imageManager.getOriginalList());

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);

        updateMainPreview();

        photoAdapter = new PhotoAdapter(this, imageManager.getFilteredList());
        recyclerView.setAdapter(photoAdapter);

        photoAdapter.setOnPhotoClickListener((position, uri) -> {
            Glide.with(this).load(uri).centerCrop().into(photoDisplay);
            curImage = imageManager.getFilteredList().get(position);
            curImage.setPosition(position);
        });
    }
}