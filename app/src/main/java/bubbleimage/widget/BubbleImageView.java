package bubbleimage.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.facebook.drawee.view.SimpleDraweeView;

public class BubbleImageView extends SimpleDraweeView {

    private final static int CORNER_WIDTH = 20;

    private Paint mPaint = new Paint();

    public BubbleImageView(Context context) {
        super(context);
    }

    public BubbleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BubbleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (null != drawable) {
            //Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Bitmap bitmap = drawableToBitmap(drawable);
            Bitmap b = clipit(bitmap, 0);
            //Bitmap b = getRoundBitmap(bitmap, 20);
            final Rect rectSrc = new Rect(0, 0, b.getWidth(), b.getHeight());
            final Rect rectDest = new Rect(0, 0, getWidth(), getHeight());

            mPaint.reset();
            canvas.drawBitmap(b, rectSrc, rectDest, mPaint);

        } else {
            super.onDraw(canvas);
        }
    }

    /**
     * @param bitmapimg
     * @param direct    1 = right , 0 = left
     * @return
     */
    public static Bitmap clipit(Bitmap bitmapimg, int direct) {
        Bitmap output = Bitmap.createBitmap(bitmapimg.getWidth(), bitmapimg.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmapimg.getWidth(), bitmapimg.getHeight());
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        if (direct == 0) {
            canvas.drawRect(0, 0, bitmapimg.getWidth() - 15, bitmapimg.getHeight(), paint);
            Path path = new Path();
            path.moveTo(bitmapimg.getWidth() - 15, 10);
            path.lineTo(bitmapimg.getWidth(), 20);
            path.lineTo(bitmapimg.getWidth() - 15, 30);
            path.lineTo(bitmapimg.getWidth() - 15, 10);
            canvas.drawPath(path, paint);
        }
        if (direct == 1) {
            canvas.drawRect(15, 0, bitmapimg.getWidth(), bitmapimg.getHeight(), paint);
            Path path = new Path();
            path.moveTo(15, 10);
            path.lineTo(0, 20);
            path.lineTo(15, 30);
            path.lineTo(15, 10);
            canvas.drawPath(path, paint);
        }
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmapimg, rect, rect, paint);
        return output;
    }

    /**
     * 获取圆角矩形图片方法
     */
    private Bitmap getRoundBitmap(Bitmap bitmap, int roundPx) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        mPaint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        mPaint.setColor(color);
        int x = bitmap.getWidth();

        canvas.drawRoundRect(rectF, roundPx, roundPx, mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, mPaint);
        return output;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;

        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

}
