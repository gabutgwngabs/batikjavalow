package com.example.batikjavalow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class DrawingView extends View {
    private static final float TOUCH_TOLERANCE = 4;

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    private Paint mPaint;

    private float mX, mY;

    // Untuk menyimpan semua path yang digambar
    private List<DrawPath> paths = new ArrayList<>();
    private List<DrawPath> undonePaths = new ArrayList<>();

    // Mode menggambar
    private int currentMode = 0;
    public static final int PEN_MODE = 0;
    public static final int LINE_MODE = 1;
    public static final int RECT_MODE = 2;
    public static final int CIRCLE_MODE = 3;

    // Titik awal dan akhir untuk bentuk
    private PointF startPoint = new PointF();
    private boolean isDrawingShape = false;

    // Warna dan ukuran stroke
    private int currentColor = Color.BLACK;
    private int strokeWidth = 5;

    public DrawingView(Context context) {
        this(context, null);
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    private void setupDrawing() {
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(currentColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(strokeWidth);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

        // Gambar semua path yang tersimpan
        for (DrawPath dp : paths) {
            canvas.drawPath(dp.path, dp.paint);
        }

        // Gambar path saat ini
        canvas.drawPath(mPath, mPaint);
    }

    private void touchStart(float x, float y) {
        if (currentMode == PEN_MODE) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        } else {
            startPoint.set(x, y);
            isDrawingShape = true;
        }
    }

    private void touchMove(float x, float y) {
        if (currentMode == PEN_MODE) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);

            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;
            }
        } else if (isDrawingShape) {
            mPath.reset();
            drawShape(startPoint.x, startPoint.y, x, y);
        }
    }

    private void touchUp() {
        if (currentMode == PEN_MODE) {
            mPath.lineTo(mX, mY);
        } else {
            isDrawingShape = false;
        }

        // Simpan path yang telah selesai digambar
        paths.add(new DrawPath(new Path(mPath), new Paint(mPaint)));
        mPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();
                break;
        }
        return true;
    }

    private void drawShape(float startX, float startY, float endX, float endY) {
        switch (currentMode) {
            case LINE_MODE:
                mPath.moveTo(startX, startY);
                mPath.lineTo(endX, endY);
                break;
            case RECT_MODE:
                mPath.addRect(startX, startY, endX, endY, Path.Direction.CW);
                break;
            case CIRCLE_MODE:
                float radius = (float) Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));
                mPath.addCircle(startX, startY, radius, Path.Direction.CW);
                break;
        }
    }

    public void setMode(int mode) {
        currentMode = mode;
    }

    public void clearCanvas() {
        paths.clear();
        undonePaths.clear();
        invalidate();
    }

    public void undo() {
        if (paths.size() > 0) {
            undonePaths.add(paths.remove(paths.size() - 1));
            invalidate();
        }
    }

    public void redo() {
        if (undonePaths.size() > 0) {
            paths.add(undonePaths.remove(undonePaths.size() - 1));
            invalidate();
        }
    }

    // Kelas untuk menyimpan path dan paint-nya
    private class DrawPath {
        Path path;
        Paint paint;

        DrawPath(Path path, Paint paint) {
            this.path = path;
            this.paint = paint;
        }
    }
}