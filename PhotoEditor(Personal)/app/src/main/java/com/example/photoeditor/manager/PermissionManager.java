package com.example.photoeditor.manager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionManager {
    private static final int REQ = 1001;

    // Get appropriate permissions per API level
    private static String[] getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return new String[]{Manifest.permission.READ_MEDIA_IMAGES};
        } else {
            return new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        }
    }

    // Check if all permissions granted
    public static boolean hasPhotoPermission(Context ctx) {
        for (String p : getPermissions()) {
            if (ContextCompat.checkSelfPermission(ctx, p) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    // Request permissions
    public static void requestPhotoPermission(Activity act) {
        ActivityCompat.requestPermissions(act, getPermissions(), REQ);
    }
}