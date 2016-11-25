package libs.true123.cn.indicator;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import libs.true123.cn.indicator.base.PageIndicator;

/**
 * Created by junbo on 25/11/2016.
 */

public abstract class AbstractIndicator extends View implements PageIndicator {

    ViewPager mViewPager;
    ViewPager.OnPageChangeListener mListener;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AbstractIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public AbstractIndicator(Context context) {
        super(context);
    }

    public AbstractIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setItemPosition(int position) {

    }

    @Override
    public void setViewPager(ViewPager viewPager) {
        if (mViewPager != null) {
            mViewPager.addOnPageChangeListener(null);
        }
        mViewPager = viewPager;
        mViewPager.addOnPageChangeListener(this);

    }

    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mListener = listener;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
