package ml.mcos.itemcommand.util;

public class Utils {

    /**
     * 将一个字符串解析为int类型
     * @param str 数字字符串
     * @return 返回str表示的int. 如果 str < 0 则返回0, 无效数字格式返回-1.
     */
    public static int parseInt(String str) {
        try {
            int i = Integer.parseInt(str);
            return Math.max(i, 0);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * 将一个字符串解析为double类型
     * @param str 数字字符串
     * @return 返回str表示的double. 如果 str < 0 则返回0, 无效数字格式返回-1.
     */
    public static double parseDouble(String str) {
        try {
            double d = Double.parseDouble(str);
            return Math.max(d, 0.0);
        } catch (NumberFormatException | NullPointerException e) {
            return -1.0;
        }
    }

    public static String getTextLeft(String text, char ch) {
        int index = text.indexOf(ch);
        return index == -1 ? "" : text.substring(0, index);
    }

    public static String getTextRight(String text, char ch) {
        int index = text.indexOf(ch);
        return index == -1 ? "" : text.substring(index + 1);
    }

}
