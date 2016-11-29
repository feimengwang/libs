package libs.true123.cn.indicator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import libs.true123.cn.indicator.base.PageIndicator;

/**
 * Created by junbo on 25/11/2016.
 */

public abstract class AbstractIndicator extends View implements PageIndicator {

    ViewPager mViewPager;

    ViewPager.OnPageChangeListener mListener;
    int position = 0;

    int radius = 20;
    int defaultRadius = 10;
    int itemMargin = 20;
    Paint defaultIndicatorPaint = null;
    Paint movingIndicatorPaint = null;
    int windowWith = 0;
    float positionOffset;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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

    private void init() {
        defaultIndicatorPaint = new Paint();
        defaultIndicatorPaint.setAntiAlias(true);
        defaultIndicatorPaint.setStrokeWidth(10);
        defaultIndicatorPaint.setColor(getResources().getColor(R.color.white));
        movingIndicatorPaint = new Paint();
        movingIndicatorPaint.setAntiAlias(true);
        movingIndicatorPaint.setStrokeWidth(10);
        movingIndicatorPaint.setColor(getResources().getColor(R.color.orange));
        windowWith = getResources().getDisplayMetrics().widthPixels;
        Log.i("indicator", "windowWith=" + windowWith);
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
        Log.i("Indicator", "" + positionOffset + ";;" + positionOffsetPixels);
        this.positionOffset = positionOffset;
        this.position = position;
        invalidate();
    }

    @Override
    public void onPageSelected(int position) {
        this.position = position;
        Log.i("indicator","onpageSelected="+position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, 100);

    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawIndicator(canvas);
        drawMovingIndicator(canvas);
    }

    private void drawMovingIndicator(Canvas canvas) {
        if (positionOffset >= 0f && positionOffset <= 1f) {
            int count = mViewPager.getAdapter().getCount();
            float left = (windowWith - (radius * 2 + itemMargin) * count) / 2;
            left += (radius * 2 + itemMargin) * ((float)position  + positionOffset);
            canvas.drawCircle(left + radius, radius + 10, radius, movingIndicatorPaint);
        }
    }

    private void drawIndicator(Canvas canvas) {
        if (mViewPager != null && mViewPager.getAdapter() != null) {
            int count = mViewPager.getAdapter().getCount();
            if (count <= 0) {
                return;
            }
            int left = 0;
            int top = 10;
            int right = 0;
            int bottom = top + radius * 2;
            left = (windowWith - (radius * 2 + itemMargin) * count) / 2;
            for (int i = 0; i < count; i++) {
                right = left + (radius * 2);
                drawItem(canvas, defaultIndicatorPaint, left, top, right, bottom);
                left = right + itemMargin;
            }
        }
    }

    protected abstract void drawItem(Canvas canvas, Paint defaultIndicatorPaint, int left, int top, int right, int bottom);


    class SaveState extends BaseSavedState {
        int index;

        public SaveState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(index);
        }
    }
}