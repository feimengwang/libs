package libs.true123.cn.indicator.base;

import android.support.v4.view.ViewPager;

/**
 * Created by junbo on 25/11/2016.
 */

public interface PageIndicator extends ViewPager.OnPageChangeListener {

    public void setItemPosition(int position);

    public void setViewPager(ViewPager viewPager);

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener);
}
