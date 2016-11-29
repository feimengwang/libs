package libs.true123.cn.indicator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by junbo on 29/11/2016.
 */

public class CircleIndicator extends AbstractIndicator {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CircleIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public CircleIndicator(Context context) {
        super(context);
    }

    public CircleIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void drawItem(Canvas canvas, Paint defaultIndicatorPaint, int left, int top, int right, int bottom) {
        canvas.drawCircle(right -radius, bottom - radius, radius, defaultIndicatorPaint);
        Log.i("indicator",""+left+";"+top+";"+right+";"+bottom);
    }
}
