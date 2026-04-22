package com.example.photoeditor.manager;

import android.app.Activity;
import android.app.RecoverableSecurityException;
import android.content.ContentUris;
import android.content.Context;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.Toast;

import com.example.photoeditor.model.ImageModel;

import java.util.ArrayList;
import java.util.List;

public class ImageManager {

    private static final ImageManager INSTANCE = new ImageManager();
    private final List<ImageModel> originalList = new ArrayList<>();
    private final List<ImageModel> filteredList = new ArrayList<>();
    private ImageModel curImage;

    private ImageManager() {}

    public static ImageManager getInstance() {
        return INSTANCE;
    }

    public List<ImageModel> getOriginalList() { return originalList; }
    public List<ImageModel> getFilteredList() { return filteredList; }
    public ImageModel getCurImage() { return curImage; }
    public void setCurImage(ImageModel img) { this.curImage = img; }

    // Filter images by date
    public void setFilteredList(String date) {
        filteredList.clear();
        int idx = 0;
        for (ImageModel img : originalList) {
            String d = DateManager.getYearMonthDayString(img.getTakenDate());
            if (date.equals(d)) {
                img.setPosition(idx++);
                filteredList.add(img);
            }
        }
    }

    // Load all images from external storage
    public void loadDevicePhotos(Context context) {
        originalList.clear();
        originalList.addAll(getAllPhotos(context));
        Toast.makeText(context, "Loaded: " + originalList.size(), Toast.LENGTH_SHORT).show();
    }

    // Query photos from MediaStore
    private List<ImageModel> getAllPhotos(Context context) {
        List<ImageModel> list = new ArrayList<>();
        try (Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.DATE_TAKEN,
                        MediaStore.Images.Media.SIZE},
                null, null, MediaStore.Images.Media.DATE_TAKEN + " DESC")) {

            if (cursor == null) return list;
            int idx = 0;
            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                long date = cursor.getLong(1);
                long size = cursor.getLong(2);
                Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                list.add(new ImageModel(uri.toString(), date, size, idx++));
            }
        }
        return list;
    }

    // Delete image with Android Q+ support
    public interface DeleteCallback {
        void onSuccess();
        void onFail();
    }

    public void deleteImage(ImageModel img, Activity act, DeleteCallback cb) {
        try {
            act.getContentResolver().delete(Uri.parse(img.getUri()), null, null);
            cb.onSuccess();
        } catch (Exception e) {
            // Only handle RecoverableSecurityException on Android Q+ (API 29+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && e instanceof RecoverableSecurityException) {
                try {
                    IntentSender intentSender = ((RecoverableSecurityException) e).getUserAction().getActionIntent().getIntentSender();
                    act.startIntentSenderForResult(intentSender, 1001, null, 0, 0, 0, null);
                } catch (Exception ex) {
                    cb.onFail();
                }
            } else {
                cb.onFail();
            }
        }
    }
}
