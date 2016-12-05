package libs.true123.cn.indicator;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import libs.true123.cn.indicator.base.PageIndicator;
import libs.true123.cn.utils.DisplayUtil;
import libs.true123.cn.utils.TextUtil;

/**
 * Created by junbo on 25/11/2016.
 */

public abstract class AbstractIndicator extends View implements PageIndicator {


    ViewPager mViewPager = null;
    ViewPager.OnPageChangeListener mListener = null;
    int position = 0;
    float mRadius = 0;
    Paint defaultIndicatorPaint = null;
    Paint movingIndicatorPaint = null;
    float positionOffset = 0f;
    int mColor;
    int mSelectedColor;
    int mStrokeWidth;
    int mTextSize;
    boolean mAuto = false;
    boolean mPause = false;
    int mOrientation = HORIZONTAL;
    boolean hasText = false;
    private long mPeriod = 3;
    private TimeUnit mTimeUnit = TimeUnit.SECONDS;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            updatePosition();
        }
    };
    private ScheduledExecutorService mScheduledExecutorService;

    private void updatePosition() {
        int count = mViewPager.getAdapter().getCount();
        if (mViewPager == null || count <= 0) return;
        position++;
        if (position >= count) {
            position = 0;
        }
        mViewPager.setCurrentItem(position % count);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPause = true;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mPause = false;
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AbstractIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    public AbstractIndicator(Context context) {
        super(context);
        init(context, null, 0);
    }

    public AbstractIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public AbstractIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AbstractIndicator);
        mAuto = typedArray.getBoolean(R.styleable.AbstractIndicator_isAuto, false);
        mColor = typedArray.getColor(R.styleable.AbstractIndicator_color, getResources().getColor(R.color.white));
        mSelectedColor = typedArray.getColor(R.styleable.AbstractIndicator_selectedColor,getResources().getColor(R.color.orange));
        mRadius = typedArray.getDimension(R.styleable.AbstractIndicator_raduis,20f);
        mStrokeWidth = (int) typedArray.getDimension(R.styleable.AbstractIndicator_strokeWidth,10);
        mTextSize = (int) typedArray.getDimension(R.styleable.AbstractIndicator_textSize,22);
        typedArray.recycle();
        defaultIndicatorPaint = new Paint();
        defaultIndicatorPaint.setAntiAlias(true);
        defaultIndicatorPaint.setStrokeWidth(mStrokeWidth);
        defaultIndicatorPaint.setColor(mColor);
        movingIndicatorPaint = new Paint();
        movingIndicatorPaint.setAntiAlias(true);
        movingIndicatorPaint.setStrokeWidth(mStrokeWidth);
        movingIndicatorPaint.setColor(mSelectedColor);
    }

    public AbstractIndicator setOrientation(@HostTypeChecker int orientation) {
        mOrientation = orientation;
        hasText = false;
        return this;
    }

    public AbstractIndicator setAuto(boolean auto) {
        mAuto = auto;
        startLoop();
        return this;
    }

    public AbstractIndicator setHasText(boolean hasText) throws Exception {
        if (mOrientation == VERTICAL) {
            throw new Exception("The orientation is vertical, do not allow to set text!");
        }
        this.hasText = hasText;
        return this;
    }

    private void setPeriod(long period, TimeUnit unit) {
        if (period > 0) {
            mPeriod = period;
        }
        if (unit != null) {
            mTimeUnit = unit;
        }
    }

    private void startLoop() {
        if (!mAuto || mPause) return;
        if (mScheduledExecutorService != null) return;
        mScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        mScheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (!mPause) {
                    mHandler.obtainMessage().sendToTarget();
                }
            }
        }, 0, mPeriod, mTimeUnit);

    }

    private void stopLoop() {
        if (mScheduledExecutorService != null && !mScheduledExecutorService.isShutdown()) {
            mScheduledExecutorService.shutdown();
            mScheduledExecutorService = null;
            mPause = true;
        }
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
            result = (int) (2 * mRadius + getPaddingLeft() + getPaddingRight());
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
            result = (int) (getPaddingLeft() + getPaddingRight() + (2 * mRadius) * pageCount + mRadius * (pageCount - 1));
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
        int left = (int) ((getMeasuredWidth() - (mRadius * 2 * count + mRadius * (count - 1))) / 2);
        int top = (int) ((getMeasuredHeight() - 2 * mRadius) / 2);
        if (mOrientation == VERTICAL) {
            top = (int) ((getMeasuredHeight() - (mRadius * 2 * count + mRadius * (count - 1))) / 2);
            left = (int) ((getMeasuredWidth() - 2 * mRadius) / 2);
        }
        if (mOrientation == HORIZONTAL && hasText) {
            left = (int) (getMeasuredWidth() - (2 * mRadius + mRadius) * (count + 1) - getPaddingRight());
            drawText(canvas, left - getPaddingLeft());
        }
        drawIndicator(canvas, top, left);
        drawMovingIndicator(canvas, top, left);
    }

    private void drawText(Canvas canvas, int length) {
        CharSequence title = mViewPager.getAdapter().getPageTitle(position);
        if (title != null && !"".equals(title.toString())) {
            Paint p = new Paint();
            p.setTextSize(DisplayUtil.sp2px(getContext(), mTextSize));
            p.setColor(Color.BLUE);
            String sTitle = TextUtil.getClippedText(title.toString(), length, p);
            canvas.drawText(sTitle, getPaddingLeft(), TextUtil.textBaseLine(p, getMeasuredHeight()), p);
        }
    }

    private void drawMovingIndicator(Canvas canvas, int top, int left) {
        if (positionOffset >= 0f && positionOffset <= 1f) {
            float distanceForFirst = (mRadius * 2 + mRadius) * ((float) position + positionOffset);
            if (mOrientation == HORIZONTAL) {
                left += distanceForFirst;
            } else {
                top += distanceForFirst;
            }
            drawItem(canvas, movingIndicatorPaint, left, top, (int)(left + 2 * mRadius), (int)(top + 2 * mRadius));
        }
    }

    private void drawIndicator(Canvas canvas, int top, int left) {
        int count = mViewPager.getAdapter().getCount();
        int right = 0;
        int bottom = 0;
        for (int i = 0; i < count; i++) {
            right = (int) (left + (mRadius * 2));
            bottom = (int) (top + mRadius * 2);
            drawItem(canvas, defaultIndicatorPaint, left, top, right, bottom);
            if (mOrientation == HORIZONTAL) {
                left = (int) (right + mRadius);
            } else {
                top = (int) (bottom + mRadius);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopLoop();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (View.VISIBLE == visibility) {
            mPause = false;
            startLoop();
        } else if (View.INVISIBLE == visibility || View.GONE == visibility) {
            stopLoop();
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