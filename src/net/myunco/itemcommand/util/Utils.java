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
     * 获取指定字符左边的指定长度内的文本。
     * @param text 源文本字符串。
     * @param ch 指定的字符。
     * @param maxLength 指定要获取文本的最大长度。
     * @return 返回指定字符左边的指定长度内的文本，如果超过指定长度，返回空字符串；如果没有找到，则返回空字符串。
     */
    public static String getTextLeft(String text, char ch, int maxLength) {
        int index = text.indexOf(ch);
        if (index >= maxLength) {
            return "";
        }
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
