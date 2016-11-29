package libs.true123.cn.libs;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import libs.true123.cn.indicator.CircleIndicator;

public class MainActivity extends AppCompatActivity {
    ViewPager viewPager;
    CircleIndicator circleIndicator;
    int[] imgsIds = {R.drawable.s1, R.drawable.s2, R.drawable.s3, R.drawable.s4};
    ImageView[] imgs = new ImageView[imgsIds.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int i = 0;
        for (int id : imgsIds) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(id);
            imgs[i] = imageView;
            i++;
        }
        ViewPagerAdapter adapter = new ViewPagerAdapter(imgs);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        circleIndicator = (CircleIndicator) findViewById(R.id.circleIndicator);
        circleIndicator.setViewPager(viewPager);
    }
}
