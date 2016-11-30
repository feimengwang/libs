package libs.true123.cn.indicator.base;

import android.support.annotation.IntDef;
import android.support.v4.view.ViewPager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by junbo on 25/11/2016.
 */

public interface PageIndicator extends ViewPager.OnPageChangeListener {
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    @IntDef({HORIZONTAL, VERTICAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface HostTypeChecker {

    }

    public void setItemPosition(int position);

    public void setViewPager(ViewPager viewPager);

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener);
}
