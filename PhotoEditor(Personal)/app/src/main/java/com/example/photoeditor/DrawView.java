package com.example.photoeditor;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawView extends View {
    private Paint paint;
    private Path path;
    private Bitmap bitmap;
    private Matrix drawMatrix;
    private boolean isDrawModeEnabled = false;

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    // Initialize paint and drawing settings
    private void init() {
        path = new Path();
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8f);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        drawMatrix = new Matrix();
    }

    // Enable or disable drawing
    public void setDrawModeEnabled(boolean enabled) {
        this.isDrawModeEnabled = enabled;
    }

    // Set brush color
    public void setPaintColor(int color) {
        paint.setColor(color);
    }

    // Set image bitmap and auto fit center
    public void setBitmap(Bitmap bmp) {
        this.bitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);
        updateMatrix();
        invalidate();
    }

    // Calculate matrix to fit center
    private void updateMatrix() {
        if (bitmap == null) return;
        float vw = getWidth();
        float vh = getHeight();
        float bw = bitmap.getWidth();
        float bh = bitmap.getHeight();
        float scale = Math.min(vw / bw, vh / bh);
        float tx = (vw - bw * scale) / 2f;
        float ty = (vh - bh * scale) / 2f;
        drawMatrix.reset();
        drawMatrix.postScale(scale, scale);
        drawMatrix.postTranslate(tx, ty);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateMatrix();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmap == null) return;

        // Draw scaled and centered bitmap
        canvas.drawBitmap(bitmap, drawMatrix, null);

        // Adjust stroke width for scaling
        float[] values = new float[9];
        drawMatrix.getValues(values);
        float scale = values[Matrix.MSCALE_X];
        float oldWidth = paint.getStrokeWidth();
        paint.setStrokeWidth(oldWidth / scale);

        // Draw drawing path
        canvas.save();
        canvas.concat(drawMatrix);
        canvas.drawPath(path, paint);
        canvas.restore();

        paint.setStrokeWidth(oldWidth);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isDrawModeEnabled) return false;

        // Map touch position to original image coordinates
        float[] pts = {event.getX(), event.getY()};
        Matrix inv = new Matrix();
        drawMatrix.invert(inv);
        inv.mapPoints(pts);
        float x = pts[0];
        float y = pts[1];

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                // Draw path to bitmap permanently
                Canvas canvas = new Canvas(bitmap);
                canvas.drawPath(path, paint);
                path.reset();
                break;
        }
        invalidate();
        return true;
    }

    // Export final bitmap with drawings for saving
    public Bitmap exportBitmapRealSize() {
        if (bitmap == null) return null;
        Bitmap result = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(result);
        canvas.drawPath(path, paint);
        return result;
    }
}