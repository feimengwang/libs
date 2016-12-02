package libs.true123.cn.indicator;

import android.annotation.TargetApi;
import android.content.Context;
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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Date;
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

    int mRadius = 20;
    int mDistance = 0;

    Paint defaultIndicatorPaint = null;
    Paint movingIndicatorPaint = null;
    float positionOffset = 0f;

    boolean mAuto = false;
    boolean mPause = false;
    int mOrientation = HORIZONTAL;
    boolean hasText = false;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            updatePosition();
        }
    };
    private ScheduledExecutorService mScheduledExecutorService;
    private void updatePosition() {
        Log.i("run", "position1=" + position);
        int count  = mViewPager.getAdapter().getCount();
        if (mViewPager == null || count <= 0) return;
        position++;
        if (position >= count) {
            position = 0;
        }
        Log.i("run", "position2=" + position);
        mViewPager.setCurrentItem(position%count);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.i("run", "action=" + event.getAction());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPause = true;
                Log.i("run", "action1=" + mPause);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mPause = false;
                Log.i("run", "action2=" + mPause);
                break;
        }
        return super.dispatchTouchEvent(event);
    }

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

    private void startLoop() {
        if(!mAuto ||mPause)return;
        if(mScheduledExecutorService!=null)return;
        mScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        mScheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (!mPause) {
                    mHandler.obtainMessage().sendToTarget();
                    Log.i("run", "" + new Date().toString());
                }
            }
        }, 0, 10, TimeUnit.SECONDS);

    }

    private void stopLoop() {
        if (mScheduledExecutorService != null && !mScheduledExecutorService.isShutdown()) {
            mScheduledExecutorService.shutdown();
            mScheduledExecutorService = null;
            mPause = true;
        }
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
        if (mOrientation == HORIZONTAL && hasText) {
            left = getMeasuredWidth() - (2 * mRadius + mDistance) * (count + 1) - getPaddingRight();
            drawText(canvas, left - getPaddingLeft());
        }
        drawIndicator(canvas, top, left);
        drawMovingIndicator(canvas, top, left);
    }

    private void drawText(Canvas canvas, int length) {
        CharSequence title = mViewPager.getAdapter().getPageTitle(position);
        if (title != null && !"".equals(title.toString())) {
            Paint p = new Paint();
            p.setTextSize(DisplayUtil.sp2px(getContext(), 22));
            p.setColor(Color.BLUE);
            String sTitle = TextUtil.getClippedText(title.toString(), length, p);
            canvas.drawText(sTitle, getPaddingLeft(), TextUtil.textBaseLine(p, getMeasuredHeight()), p);
        }
    }

    private void drawMovingIndicator(Canvas canvas, int top, int left) {
        if (positionOffset >= 0f && positionOffset <= 1f) {
            float distanceForFirst = (mRadius * 2 + mDistance) * ((float) position + positionOffset);
            if (mOrientation == HORIZONTAL) {
                left += distanceForFirst;
            } else {
                top += distanceForFirst;
            }
            drawItem(canvas, movingIndicatorPaint, left, top, left + 2 * mRadius, top + 2 * mRadius);
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

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopLoop();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        Log.i("run", "v=" + visibility);
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