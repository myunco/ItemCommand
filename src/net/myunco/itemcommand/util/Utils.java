package net.myunco.itemcommand.util;

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

    /**
     * 获取指定字符左边的所有文本。
     * @param text 源文本字符串。
     * @param ch 指定的字符。
     * @return 返回指定字符左边的所有文本；如果没有找到，则返回空字符串。
     */
    public static String getTextLeft(String text, char ch) {
        int index = text.indexOf(ch);
        return index == -1 ? "" : text.substring(0, index);
    }

    /**
     * 获取指定字符右边的所有文本。
     * @param text 源文本字符串。
     * @param ch 指定的字符。
     * @return 返回指定字符右边的所有文本；如果没有找到，则返回空字符串。
     */
    public static String getTextRight(String text, char ch) {
        int index = text.indexOf(ch);
        return index == -1 ? "" : text.substring(index + 1);
    }

    /**
     * 获取指定字符左边的所有文本。
     * 与getTextLeft的区别是本方法从后往前查找指定字符。
     * @param text 源文本字符串。
     * @param ch 指定的字符。
     * @return 返回指定字符左边的所有文本；如果没有找到，则返回空字符串。
     */
    public static String getTextLeft1(String text, char ch) {
        int index = text.lastIndexOf(ch);
        return index == -1 ? "" : text.substring(0, index);
    }

    /**
     * 获取指定字符右边的所有文本。
     * 与getTextRight的区别是本方法从后往前查找指定字符。
     * @param text 源文本字符串。
     * @param ch 指定的字符。
     * @return 返回指定字符右边的所有文本；如果没有找到，则返回空字符串。
     */
    public static String getTextRight1(String text, char ch) {
        int index = text.lastIndexOf(ch);
        return index == -1 ? "" : text.substring(index + 1);
    }

    /**
     * 获取指定字符串左边的所有文本。
     * @param text 源文本字符串。
     * @param s 指定的字符串。
     * @return 返回指定字符串左边的所有文本；如果没有找到，则返回空字符串。
     */
    public static String getTextLeft(String text, String s) {
        int index = text.indexOf(s);
        return index == -1 ? "" : text.substring(0, index);
    }

    /**
     * 获取指定字符串右边的所有文本。
     * @param text 源文本字符串。
     * @param s 指定的字符串。
     * @return 返回指定字符串右边的所有文本；如果没有找到，则返回空字符串。
     */
    public static String getTextRight(String text, String s) {
        int index = text.indexOf(s);
        return index == -1 ? "" : text.substring(index + s.length());
    }

}
