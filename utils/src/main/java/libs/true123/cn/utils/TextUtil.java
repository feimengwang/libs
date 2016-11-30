package libs.true123.cn.utils;

import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by junbo on 30/11/2016.
 */

public class TextUtil {
    public static String getClippedText(String text, int length) {
        String result = "";
        if (text == null || "".equals(text) || length < 3) return result;
        if (text.length() > length) {
            result = text.substring(0, length - 3) + "...";
        } else {
            result = text;
        }
        return result;
    }

    public static String getClippedText(String text, int width, Paint paint) {
        String result = "";
        if (paint == null) return text;
        if (text == null || "".equals(text)) return result;
        Rect rect = new Rect();
        int i = 3;
        for (; i < text.length(); i++) {
            paint.getTextBounds(text, 0, i, rect);
            if (rect.width() >= width) {
                result = text.substring(0, i);
                break;
            }
        }
        if (i >= text.length()) result = text;
        return result;
    }

}
