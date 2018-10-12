package test.com.mvpsampleproject.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by umair.irshad on 5/8/2018.
 */

public class CustomView extends ImageView {

    /** Bits defining the horizontal axis. */
    public static final int VERTICAL_SHIFT = 0;
    /** Bits defining the vertical axis. */
    public static final int HORIZONTAL_SHIFT = 4;

    public final static int VERTICAL_TOP = 0x0001 << VERTICAL_SHIFT;
    public final static int VERTICAL_CENTER = 0x0002 << VERTICAL_SHIFT;
    public final static int VERTICAL_BOTTOM = 0x0004 << VERTICAL_SHIFT;

    private final static int VERTICAL_MASK = 0x000F;

    public final static int HORIZONTAL_LEFT = 0x0001 << HORIZONTAL_SHIFT;
    public final static int HORIZONTAL_CENTER = 0x0002 << HORIZONTAL_SHIFT;
    public final static int HORIZONTAL_RIGHT = 0x0004 << HORIZONTAL_SHIFT;

    private final static int HORIZONTAL_MASK = 0x00F0;

    private final static int LOUPE_RADIUS_DP = 100;
    private final static int MAGNIFICATION_FACTOR_DEFAULT = 2;
    private final static int EXTRA_OFFSET = 15;

    private int mFactor = MAGNIFICATION_FACTOR_DEFAULT;
    private int mLoupeRadius;
    private Path mLoupePath = new Path();
    private Paint mLupeBorderPaint;
    private Matrix mDrawMatrix = new Matrix();
    private RectF mDrawableBounds = new RectF();
    private int mGravity = VERTICAL_BOTTOM | HORIZONTAL_RIGHT;
    private int mCenterX;
    private int mCenterY;

    private int mOffsetX = 0;
    private int mOffsetY = 0;
    private int mExtraOffsetX;
    private int mExtraOffsetY;

    private boolean mIsTouching = false;

    public CustomView(Context context) {
        this(context, null);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaints();
        mLoupeRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, LOUPE_RADIUS_DP, getResources().getDisplayMetrics());
        mExtraOffsetX = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EXTRA_OFFSET, getResources().getDisplayMetrics());
        mExtraOffsetY = mExtraOffsetX;
    }

    private void initPaints() {
        mLupeBorderPaint = new Paint();
        mLupeBorderPaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));
        mLupeBorderPaint.setStyle(Paint.Style.STROKE);
        mLupeBorderPaint.setColor(Color.WHITE);
        mLupeBorderPaint.setAlpha(127);
    }

    public void setMFactor(int factor) {
        mFactor = factor;
    }

    public void setRadius(int radiusDp) {
        mLoupeRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, radiusDp, getResources().getDisplayMetrics());
        setGravity(mGravity);
    }

    public void setGravity(int gravity) {
        int vertical = (gravity & VERTICAL_MASK);
        switch (vertical) {
            case VERTICAL_TOP:
                mOffsetY = (mLoupeRadius / 2 + mExtraOffsetY);
                break;
            case VERTICAL_CENTER:
                mOffsetY = 0;
                break;
            case VERTICAL_BOTTOM:
                mOffsetY = -(mLoupeRadius / 2 + mExtraOffsetY);
                break;
        }
        int horizontal = (gravity & HORIZONTAL_MASK);
        switch (horizontal) {
            case HORIZONTAL_LEFT:
                mOffsetX = (mLoupeRadius / 2 + mExtraOffsetX);
                break;
            case HORIZONTAL_CENTER:
                mOffsetX = 0;
                break;
            case HORIZONTAL_RIGHT:
                mOffsetX = -(mLoupeRadius / 2 + mExtraOffsetX);
                break;
        }
        mGravity = gravity;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        mIsTouching = !(action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP);
        mCenterX = (int) (event.getX()) + mOffsetX;
        mCenterY = (int) (event.getY()) + mOffsetY;
        invalidateSelf();
        return true;
    }

    @Override
    public void invalidateDrawable(Drawable dr) {
        Matrix matrix = getImageMatrix();
        if(getDrawable() != null){
            mDrawableBounds.set(getDrawable().getBounds());
            matrix.mapRect(mDrawableBounds);
            super.invalidateDrawable(dr);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw lupe effect
        if (mIsTouching) {
            drawLoupe(canvas);
        }
    }

    private void drawLoupe(Canvas canvas){
        canvas.save();
        clipCircle(canvas);

        mDrawMatrix.reset();
        mDrawMatrix.preScale(mFactor, mFactor);
        mDrawMatrix.postConcat(getImageMatrix());

        float px = mCenterX - mDrawableBounds.left;
        float py = mCenterY - mDrawableBounds.top;

        mDrawMatrix.postTranslate(-px * (mFactor - 1), -py * (mFactor - 1));
        canvas.drawBitmap(((BitmapDrawable) getDrawable()).getBitmap(), mDrawMatrix, null);
        // draw border
        canvas.drawCircle(mCenterX, mCenterY, mLoupeRadius, mLupeBorderPaint);
        canvas.restore();
    }

    private void clipCircle(Canvas canvas) {
        mLoupePath.reset();
        mLoupePath.addCircle(mCenterX, mCenterY, mLoupeRadius, Path.Direction.CW);
        canvas.clipPath(mLoupePath);
    }

    public void invalidateSelf() {
        try{
            getDrawable().invalidateSelf();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

