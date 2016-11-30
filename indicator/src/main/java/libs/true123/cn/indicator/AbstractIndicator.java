package libs.true123.cn.indicator;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import libs.true123.cn.indicator.base.PageIndicator;

/**
 * Created by junbo on 25/11/2016.
 */

public abstract class AbstractIndicator extends View implements PageIndicator {


    ViewPager mViewPager = null;

    ViewPager.OnPageChangeListener mListener = null;
    int position = 0;

    int mRadius = 20;
    int mDistance = 0;

    Paint defaultIndicatorPaint = null;
    Paint movingIndicatorPaint = null;
    float positionOffset = 0f;

    boolean mAuto = false;
    int mOrientation = HORIZONTAL;
    boolean hasText = false;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AbstractIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public AbstractIndicator(Context context) {
        super(context);
        init();
    }

    public AbstractIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AbstractIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public AbstractIndicator setOrientation(@HostTypeChecker int orientation) {
        mOrientation = orientation;
        return this;
    }

    public AbstractIndicator setAuto(boolean auto) {
        mAuto = auto;
        return this;

    }

    public AbstractIndicator setHasText(boolean hasText) {
        this.hasText = hasText;
        return this;
    }

    private void init() {
        defaultIndicatorPaint = new Paint();
        defaultIndicatorPaint.setAntiAlias(true);
        defaultIndicatorPaint.setStrokeWidth(10);
        defaultIndicatorPaint.setColor(getResources().getColor(R.color.white));
        movingIndicatorPaint = new Paint();
        movingIndicatorPaint.setAntiAlias(true);
        movingIndicatorPaint.setStrokeWidth(10);
        movingIndicatorPaint.setColor(getResources().getColor(R.color.orange));
        mDistance = mRadius;
    }

    @Override
    public void setItemPosition(int position) {
        this.position = position;
    }

    @Override
    public void setViewPager(ViewPager viewPager) {
        if (mViewPager != null) {
            mViewPager.addOnPageChangeListener(null);
        }
        mViewPager = viewPager;
        mViewPager.addOnPageChangeListener(this);
        postInvalidate();

    }

    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mListener = listener;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        this.positionOffset = positionOffset;
        this.position = position;
        invalidate();
        if (mListener != null) {
            mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        this.position = position;
        if (mListener != null) {
            mListener.onPageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mListener != null) {
            mListener.onPageScrollStateChanged(state);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mOrientation == HORIZONTAL) {
            setMeasuredDimension(getLong(widthMeasureSpec), getShort(heightMeasureSpec));
        } else {
            setMeasuredDimension(getShort(widthMeasureSpec), getLong(heightMeasureSpec));
        }
    }

    private int getShort(int measureSpec) {
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        int result = 0;
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = 2 * mRadius + getPaddingLeft() + getPaddingRight();
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    private int getLong(int measureSpec) {
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        int result = 0;
        if (mode == MeasureSpec.EXACTLY || mViewPager.getAdapter() == null) {
            result = size;
        } else {
            int pageCount = mViewPager.getAdapter().getCount();
            result = getPaddingLeft() + getPaddingRight() + (2 * mRadius) * pageCount + mDistance * (pageCount - 1);
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        SaveState saveState = new SaveState(parcelable);
        saveState.index = position;
        return saveState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        SaveState saveState = (SaveState) state;
        super.onRestoreInstanceState(saveState.superParcelable);
        position = saveState.index;
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mViewPager == null || mViewPager.getAdapter() == null) {
            return;
        }
        int count = mViewPager.getAdapter().getCount();
        if (count <= 0) {
            return;
        }
        int left = (getMeasuredWidth() - (mRadius * 2 * count + mDistance * (count - 1))) / 2;
        int top = (getMeasuredHeight() - 2 * mRadius) / 2;
        if (mOrientation == VERTICAL) {
            top = (getMeasuredHeight() - (mRadius * 2 * count + mDistance * (count - 1))) / 2;
            left = (getMeasuredWidth() - 2 * mRadius) / 2;
        }
        drawIndicator(canvas, top, left);
        drawMovingIndicator(canvas, top, left);
    }

    private void drawMovingIndicator(Canvas canvas, int top, int left) {
        if (positionOffset >= 0f && positionOffset <= 1f) {
            float distanceForFirst = (mRadius * 2 + mDistance) * ((float) position + positionOffset);
            if (mOrientation == HORIZONTAL) {
                left += distanceForFirst;
            } else {
                top += distanceForFirst;
            }
            drawItem(canvas, movingIndicatorPaint, left, top, left+2*mRadius, top+2*mRadius);
        }
    }

    private void drawIndicator(Canvas canvas, int top, int left) {
        int count = mViewPager.getAdapter().getCount();
        int right = 0;
        int bottom = 0;
        for (int i = 0; i < count; i++) {
            right = left + (mRadius * 2);
            bottom = top + mRadius * 2;
            drawItem(canvas, defaultIndicatorPaint, left, top, right, bottom);
            if (mOrientation == HORIZONTAL) {
                left = right + mDistance;
            } else {
                top = bottom + mDistance;
            }
        }
    }

    protected abstract void drawItem(Canvas canvas, Paint defaultIndicatorPaint, int left, int top, int right, int bottom);


    static class SaveState implements Parcelable {
        int index;
        Parcelable superParcelable;

        protected SaveState(Parcelable parcelable) {
            superParcelable = parcelable;
        }

        protected SaveState(Parcel in) {
            index = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(index);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<SaveState> CREATOR = new Creator<SaveState>() {
            @Override
            public SaveState createFromParcel(Parcel in) {
                return new SaveState(in);
            }

            @Override
            public SaveState[] newArray(int size) {
                return new SaveState[size];
            }
        };
    }
}