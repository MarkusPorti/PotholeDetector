package de.portugall.markus.potholedetector;

import android.content.Context;
import android.graphics.*;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DetectorView extends SurfaceView {

    private final Paint paint;
    private final SurfaceHolder holder;
    private final Context context;

    public DetectorView(Context context) {
        this(context, null);
    }

    public DetectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder = getHolder();
        holder.setFormat(PixelFormat.TRANSPARENT);
        this.context = context;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
    }

    public void paintRectangle(float x, float y, float width, float height) {
        final Canvas canvas = holder.lockCanvas();
        if (canvas != null) {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            canvas.drawRect(x, y, x + width, y + height, paint);
            holder.unlockCanvasAndPost(canvas);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Canvas canvas1 = holder.lockCanvas();
                    if (canvas1 != null) {
                        canvas1.drawColor(0, PorterDuff.Mode.CLEAR);
                        holder.unlockCanvasAndPost(canvas1);
                    }
                }
            }, 2000);
        }
//        holder.unlockCanvasAndPost(canvas);
    }

}
