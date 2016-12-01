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

    /**
     * @param text
     * @param width
     * @param paint
     * @return
     */
    public static String getClippedText(String text, int width, Paint paint) {
        String result = "";
        if (paint == null) return text;
        if (text == null || "".equals(text)) return result;
        Rect rect = new Rect();
        int i = 4;
        for (; i < text.length(); i++) {
            paint.getTextBounds(text, 0, i, rect);
            if (rect.width() >= width) {
                result = text.substring(0, i - 3) + "...";
                break;
            }
        }
        if (i >= text.length()) result = text;
        return result;
    }

    /**
     * In order to center the text in vertical,
     * calculate the base line for drawText method.
     *
     * @param paint
     * @param height
     * @return
     */
    public static int textBaseLine(Paint paint, int height) {

        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return (int) (height - fontMetrics.top - fontMetrics.bottom) / 2;
    }

}
