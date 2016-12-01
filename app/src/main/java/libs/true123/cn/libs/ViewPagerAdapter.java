package libs.true123.cn.libs;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by junbo on 29/11/2016.
 */

public class ViewPagerAdapter extends PagerAdapter {
    ImageView[] imgs;
    String[] res = {"nihhhhhhhhhhhhhhhhhhhhhhhhhhh", "helloooooooo", "cacacaacaaca你你你你你你你", "我测试时是是是是是是我测试时是是是是是是"};

    public ViewPagerAdapter(ImageView[] imgs) {
        this.imgs = imgs;
    }

    @Override
    public int getItemPosition(Object object) {
        for (int i = 0; i < imgs.length; i++) {
            if (object == imgs[i]) return i;
        }
        return 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return res[position % res.length];
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        container.addView(imgs[position % imgs.length]);

        return imgs[position % imgs.length];
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView(imgs[position % imgs.length]);
    }

    @Override
    public int getCount() {
        return imgs.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
