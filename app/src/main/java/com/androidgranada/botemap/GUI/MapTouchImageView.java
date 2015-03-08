package com.androidgranada.botemap.GUI;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.androidgranada.botemap.R;
import com.androidgranada.botemap.utils.Session;

/**
 * Created by jmorenov on 7/03/14.
 */

public class MapTouchImageView extends TouchImageView {

    private TouchImageViewListener touchImageViewListener;
    private GestureDetector gestureDetector;

    private Bitmap bitmap;
    private Bitmap bitmap_original;
    private Bitmap location_mark;
    private Paint paint = new Paint();

    private int LOCATION_MARK_SCALED_WIDTH = 70;
    private int LOCATION_MARK_SCALED_HEIGHT = 70;

    private MapFragment listener;

    private boolean image_adjust = false;

    private PointF position_map;

    private float[] m = new float[9];

    private BitmapFactory.Options dimensions;

    private boolean positionEnabled = false;

    public MapTouchImageView(Context context) {
        super(context);
        sharedConstructing(context);
    }

    public MapTouchImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        sharedConstructing(context);
    }

    public MapTouchImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        sharedConstructing(context);
    }

    public void setFragmentListener(MapFragment fragment) {
        listener = fragment;
    }

    private void sharedConstructing(Context context) {
        touchImageViewListener = new MapTouchImageView.TouchImageViewListener();
        gestureDetector = new GestureDetector(context, new MapGestureListener());
        setOnTouchListener(new MapTouchImageViewListener());

        LOCATION_MARK_SCALED_HEIGHT = getResources().getDrawable(R.drawable.location_mark).getIntrinsicHeight() * 2;
        LOCATION_MARK_SCALED_WIDTH = getResources().getDrawable(R.drawable.location_mark).getIntrinsicWidth() * 2;
        location_mark = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.location_mark),
                LOCATION_MARK_SCALED_WIDTH, LOCATION_MARK_SCALED_HEIGHT, false);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        if (bitmap != null)
            bitmap.recycle();
        bitmap = null;
        if (bitmap_original != null)
            bitmap_original.recycle();
        bitmap_original = null;
        super.setImageBitmap(bm);
    }

    public void setLocationMark(PointF point) {
        super.setImageBitmap(bitmap_original);
        Bitmap bitmapCopy = BitmapFactory.decodeResource(getResources(), R.drawable.map_total_recortado);
        bitmap = bitmapCopy.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(location_mark, point.x - LOCATION_MARK_SCALED_WIDTH / 2, point.y - LOCATION_MARK_SCALED_HEIGHT, paint);
        super.setImageBitmap(bitmap);
    }

    public void eraseLocationMark() {
        super.setImageBitmap(bitmap_original);
    }

    private void setColorBitmap(int color) {
        ColorFilter filter = new LightingColorFilter(color, 1);
        paint.setColorFilter(filter);
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        dimensions = new BitmapFactory.Options();
        dimensions.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), resId, dimensions);
        bitmap_original = BitmapFactory.decodeResource(getResources(), resId);
    }

    public PointF getBitmapPositionFromTouch(float x, float y) {
        getImageViewMatrix().getValues(m);
        int origH = dimensions.outHeight;
        int origW = dimensions.outWidth;
        float transX = m[Matrix.MTRANS_X];
        float transY = m[Matrix.MTRANS_Y];
        float finalX = ((x - transX) * origW) / getImageWidth();
        float finalY = ((y - transY) * origH) / getImageHeight();
        finalX = Math.min(Math.max(finalX, 0), origW);
        finalY = Math.min(Math.max(finalY, 0), origH);

        return new PointF(finalX, finalY);
    }

    public PointF getTouchPositionFromBitmap(float bx, float by) {
        getImageViewMatrix().getValues(m);
        int origH = dimensions.outHeight;
        int origW = dimensions.outWidth;
        float px = bx / origW;
        float py = by / origH;
        float finalX = m[Matrix.MTRANS_X] + getImageWidth() * px;
        float finalY = m[Matrix.MTRANS_Y] + getImageHeight() * py;
        return new PointF(finalX, finalY);
    }

    public PointF getPositionMap() {
        return position_map;
    }

    public void setPositionMapFromRealPoint(PointF bitmapPoint) {
        PointF viewPoint = getTouchPositionFromBitmap(bitmapPoint.x, bitmapPoint.y);
        setPositionMapFromViewPoint(viewPoint);
    }

    public void setPositionMapFromViewPoint(PointF viewPoint) {
        PointF bitmapPoint = getBitmapPositionFromTouch(viewPoint.x, viewPoint.y);
        PointF drawablePoint = getDrawablePointFromTouchPoint(viewPoint.x, viewPoint.y);
        setLocationMark(drawablePoint);
        position_map = bitmapPoint;
    }

    public String getPalabraPosition() {
        if (!isPositionEnabled())
            return "";

        return "" + (int) position_map.x + "" + (int) position_map.y + "";
    }

    public void printMap() {

        if (!isPositionEnabled()) {
            return;
        }

        File ruta = Environment.getExternalStoragePublicDirectory((Environment.DIRECTORY_DCIM));
        File dir = new File(ruta.getAbsolutePath() + "/" + getResources().getString(R.string.app_name));
        dir.mkdirs();
        File file = new File(dir, "IMG_" + getPalabraPosition() + ".jpg");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, fos);
            fos.flush();
            fos.close();
            //MediaStore.Images.Media.insertImage(getContext().getContentResolver(),file.getAbsolutePath(), file.getName(), file.getName());
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Toast.makeText(getContext(), "Mapa creado: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        adjustImageToView();
        super.onDraw(canvas);
    }

    public void enablePosition() {
        positionEnabled = true;
    }

    public void disablePosition() {
        positionEnabled = false;
        eraseLocationMark();
    }

    public void backOldPosition() {
        positionEnabled = true;
        setPositionMapFromRealPoint(Session.user.getPosicion());
    }

    public void setPositionCode(String code) {

    }

    public boolean checkPositionCode(String code) {
        return code.equalsIgnoreCase("Prueba");
    }

    public boolean isPositionEnabled() {
        return positionEnabled;
    }

    public void adjustImageToView() {
        if (!image_adjust) {
            float height = getViewHeight();
            float width = getViewWidth();
            float image_height = getImageHeight();
            float image_width = getImageWidth();
            float zoom_height = 0;
            float zoom_width = 0;
            float zoom;

            if (height > image_height)
                zoom_height = height / image_height;

            if (width > image_width)
                zoom_width = width / image_width;

            if (zoom_height >= zoom_width)
                zoom = zoom_height;
            else
                zoom = zoom_width;

            if (zoom > getMaxZoom())
                zoom = getMaxZoom();

            setZoom(zoom, 0.75f, 0.75f);
            setMinZoom(zoom);
            image_adjust = true;
            if (Session.user != null && Session.user.getPosicion().x != 0 && Session.user.getPosicion().y != 0)
                setPositionMapFromRealPoint(Session.user.getPosicion());
        }
    }

    private class MapGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent event) {
            setPositionMapFromViewPoint(new PointF(event.getX(), event.getY()));
            listener.locationChange();
        }
    }

    private class MapTouchImageViewListener implements OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            touchImageViewListener.onTouch(v, event);
            gestureDetector.onTouchEvent(event);
            return true;
        }
    }
}
