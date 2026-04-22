package com.example.photoeditor;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.photoeditor.manager.ImageManager;
import com.example.photoeditor.model.ImageModel;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditImageActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "EditImageActivity";
    private ImageModel curImage;
    private ImageManager imageManager;
    private ImageView ivEdit;
    private MaterialButton edit, delete, details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_image);

        // System bar insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        imageManager = ImageManager.getInstance();
        curImage = imageManager.getCurImage();

        // Views
        ImageButton back = findViewById(R.id.edit_back);
        edit = findViewById(R.id.btn_edit);
        delete = findViewById(R.id.btn_delete);
        details = findViewById(R.id.btn_details);
        ivEdit = findViewById(R.id.iv_edit);

        // Listeners
        back.setOnClickListener(this);
        edit.setOnClickListener(this);
        delete.setOnClickListener(this);
        details.setOnClickListener(this);

        showCurrentImage();
        setSwipeListener();
    }

    // Show current image
    private void showCurrentImage() {
        try {
            Uri uri = Uri.parse(curImage.getUri());
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            ivEdit.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Swipe to switch images
    private void setSwipeListener() {
        GestureDetector detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            private static final int MIN = 200;

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float vX, float vY) {
                float dx = e2.getX() - e1.getX();
                if (dx > MIN) previousImage();
                else if (-dx > MIN) nextImage();
                return false;
            }
        });

        findViewById(android.R.id.content).setOnTouchListener((v, e) -> {
            detector.onTouchEvent(e);
            return true;
        });
    }

    private void previousImage() {
        if (curImage.getPosition() > 0) {
            curImage = imageManager.getFilteredList().get(curImage.getPosition() - 1);
            showCurrentImage();
        } else {
            Toast.makeText(this, "Already first", Toast.LENGTH_SHORT).show();
        }
    }

    private void nextImage() {
        if (curImage.getPosition() < imageManager.getFilteredList().size() - 1) {
            curImage = imageManager.getFilteredList().get(curImage.getPosition() + 1);
            showCurrentImage();
        } else {
            Toast.makeText(this, "Already last", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_edit) {
            Intent intent = new Intent(this, EditManipulate.class);
            startActivity(intent);
        } else if (id == R.id.edit_back) {
            finish();
        } else if (id == R.id.btn_details) {
            showDetailsDialog();
        } else if (id == R.id.btn_delete) {
            deleteCurrentImage();
        }
    }

    // Show image info dialog
    private void showDetailsDialog() {
        String info = String.format("Taken: %s\nSize: %.2f MB",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(curImage.getTakenDate())),
                curImage.getSize() / (1024f * 1024f));

        new AlertDialog.Builder(this)
                .setTitle("詳情")
                .setMessage(info)
                .setPositiveButton("確定", null)
                .show();
    }

    // Delete image with Android Q+ permission handling
    private void deleteCurrentImage() {
        imageManager.deleteImage(curImage, this, new ImageManager.DeleteCallback() {
            @Override
            public void onSuccess() {
                imageManager.getOriginalList().remove(curImage);
                imageManager.getFilteredList().remove(curImage);

                if (imageManager.getFilteredList().isEmpty()) {
                    ivEdit.setImageDrawable(null);
                    edit.setEnabled(false);
                    delete.setEnabled(false);
                    details.setEnabled(false);
                    return;
                }

                int pos = curImage.getPosition();
                curImage = (pos < imageManager.getFilteredList().size())
                        ? imageManager.getFilteredList().get(pos)
                        : imageManager.getFilteredList().get(pos - 1);

                curImage.setPosition(imageManager.getFilteredList().indexOf(curImage));
                Glide.with(EditImageActivity.this).load(curImage.getUri()).into(ivEdit);
            }

            @Override
            public void onFail() {
                Toast.makeText(EditImageActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}