package com.example.photoeditor;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Color;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.photoeditor.manager.ImageManager;
import com.example.photoeditor.model.ImageModel;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditManipulate extends AppCompatActivity {

    private ImageManager imageManager;
    private DrawView drawView;
    private Button btnFlip, btnCrop, btnDraw, btnColor;
    private Bitmap currentBitmap;
    private boolean isDrawMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_manipulate);

        // System bar insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize
        imageManager = ImageManager.getInstance();
        drawView = findViewById(R.id.dv_edit);
        btnFlip = findViewById(R.id.btn_flip);
        btnCrop = findViewById(R.id.btn_crop);
        btnDraw = findViewById(R.id.btn_draw);
        btnColor = findViewById(R.id.btn_color);

        showCurrentImage();

        // Flip image
        btnFlip.setOnClickListener(v -> flipImage());

        // Center crop (simplified)
        btnCrop.setOnClickListener(v -> cropImage());

        // Toggle draw mode
        btnDraw.setOnClickListener(v -> {
            isDrawMode = !isDrawMode;
            drawView.setDrawModeEnabled(isDrawMode);
            btnDraw.setText(isDrawMode ? "关闭涂鸦" : "涂鸦");
            btnColor.setVisibility(isDrawMode ? View.VISIBLE : View.GONE);
            Toast.makeText(this, isDrawMode ? "涂鸦已开启" : "涂鸦已关闭", Toast.LENGTH_SHORT).show();
        });

        // Color picker
        btnColor.setOnClickListener(v -> showColorPicker());

        // Save edited image
        findViewById(R.id.btn_save).setOnClickListener(v -> saveEditedImage());

        // Back
        findViewById(R.id.back_to_edit).setOnClickListener(v -> finish());
    }

    // Load current image from ImageManager
    private void showCurrentImage() {
        try {
            ImageModel model = imageManager.getCurImage();
            Uri uri = Uri.parse(model.getUri());
            currentBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            drawView.setBitmap(currentBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Flip horizontally
    private void flipImage() {
        Matrix matrix = new Matrix();
        matrix.setScale(-1f, 1f);
        Bitmap flipped = Bitmap.createBitmap(currentBitmap, 0, 0,
                currentBitmap.getWidth(), currentBitmap.getHeight(), matrix, true);
        currentBitmap = flipped;
        drawView.setBitmap(currentBitmap);
    }

    // Center crop (80% of original size)
    private void cropImage() {
        int w = currentBitmap.getWidth();
        int h = currentBitmap.getHeight();
        int cropW = (int) (w * 0.8f);
        int cropH = (int) (h * 0.8f);
        int left = (w - cropW) / 2;
        int top = (h - cropH) / 2;
        Bitmap cropped = Bitmap.createBitmap(currentBitmap, left, top, cropW, cropH);
        currentBitmap = cropped;
        drawView.setBitmap(currentBitmap);
    }

    // Show color selection dialog
    private void showColorPicker() {
        int[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.WHITE, Color.BLACK};
        String[] names = {"红", "绿", "蓝", "黄", "白", "黑"};
        new AlertDialog.Builder(this)
                .setTitle("选择画笔颜色")
                .setItems(names, (dialog, which) -> drawView.setPaintColor(colors[which]))
                .show();
    }

    // Save image to gallery with correct time
    private void saveEditedImage() {
        if (currentBitmap == null) return;

        long now = System.currentTimeMillis();
        String fileName = "EDIT_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date(now)) + ".jpg";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/PhotoEditor");
        values.put(MediaStore.Images.Media.DATE_TAKEN, now);
        values.put(MediaStore.Images.Media.DATE_ADDED, now / 1000);
        values.put(MediaStore.Images.Media.DATE_MODIFIED, now / 1000);
        values.put(MediaStore.Images.Media.IS_PENDING, 1);

        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream os = getContentResolver().openOutputStream(uri);
            Bitmap finalBitmap = drawView.exportBitmapRealSize();
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 95, os);
            os.close();

            // Write EXIF date to avoid 1970 issue
            ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "rw");
            if (pfd != null) {
                ExifInterface exif = new ExifInterface(pfd.getFileDescriptor());
                String timeStr = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").format(new Date(now));
                exif.setAttribute(ExifInterface.TAG_DATETIME, timeStr);
                exif.setAttribute(ExifInterface.TAG_DATETIME_ORIGINAL, timeStr);
                exif.saveAttributes();
                pfd.close();
            }

            ContentValues update = new ContentValues();
            update.put(MediaStore.Images.Media.IS_PENDING, 0);
            getContentResolver().update(uri, update, null, null);

            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
        }
    }
}