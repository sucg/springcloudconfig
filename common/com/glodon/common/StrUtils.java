package com.glodon.common;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName StrUtils
 * @description 字符串处理工具类
 * @author xiaobl
 * @Copyright Glodon (c) 2014
 * @time Sep 11, 2014 4:40:36 PM
 * @version V1.0.0
 * 
 */
public class StrUtils {

	/**
	 * 处理null字符串 ，如果参数为null则返回“”；否则进行trim处理
	 * 
	 * @param str
	 *            待处理的字符串
	 * @return 如果参数为null则返回“”；否则返回trim后的值
	 */
	public static String doNull(String str) {
		if (str == null)
			return "";
		return str.trim();
	}

	/**
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str) {
		if (str == null || "".equals(str.trim()) || "null".equals(str.trim()))
			return true;
		else
			return false;
	}

	/**
	 * string的json用正则获取值
	 *
	 * @param json
	 * @param key
	 * @return
	 */
	public static String jsonVlausStr(String json, String key) {
		Matcher m = Pattern.compile("\"(.*?)\":\"(.*?)\"").matcher(json);
		while (m.find()) {
			if (key.equals(m.group(1))) {
				// System.out.println(m.group(2));
				return m.group(2);
			}
		}
		return "";
	}

	/**
	 * 利用正则表达式判断字符串是否为数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		if (str.matches("\\d*")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isConsition(String str, String equalString) {
		if (str != null) {
			try {
				str = new String(str.getBytes("ISO-8859-1"), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if (!str.trim().equals(equalString)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * String 转为byte数组
	 * 
	 * @param str
	 * @return
	 */
	public static byte[] StringToByte(String str) {
		byte b[] = null;
		try {
			b = str.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return b;
	}

	/**
	 * byte[] 数组转成string
	 * 
	 * @param b
	 * @return
	 */
	public static String byteToString(byte[] b) {
		if(null == b){
			return null;
		}
		String t = null;
		try {
			t = new String(b, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return t.toString();
	}

	/**
	 * 获取字符串的匹配度
	 * @param str
	 * @param target
	 * @return
	 */
	public static float getSimilarityRatio(String str, String target) {
		if (null == str){
			str = "";
		}
		if (null == target){
			target = "";
		}
		if (str.isEmpty() && target.isEmpty()){
			return 1;
		}
		return 1 - (float) compare(str, target)
				/ Math.max(str.length(), target.length());
	}

	private static int compare(String str, String target) {
		int d[][]; // 矩阵
		int n = str.length();
		int m = target.length();
		int i; // 遍历str的
		int j; // 遍历target的
		char ch1; // str的
		char ch2; // target的
		int temp; // 记录相同字符,在某个矩阵位置值的增量,不是0就是1
		if (n == 0) {
			return m;
		}
		if (m == 0) {
			return n;
		}
		d = new int[n + 1][m + 1];
		for (i = 0; i <= n; i++) { // 初始化第一列
			d[i][0] = i;
		}

		for (j = 0; j <= m; j++) { // 初始化第一行
			d[0][j] = j;
		}

		for (i = 1; i <= n; i++) { // 遍历str
			ch1 = str.charAt(i - 1);
			// 去匹配target
			for (j = 1; j <= m; j++) {
				ch2 = target.charAt(j - 1);
				if (ch1 == ch2) {
					temp = 0;
				} else {
					temp = 1;
				}

				// 左边+1,上边+1, 左上角+temp取最小
				d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1]
						+ temp);
			}
		}
		return d[n][m];
	}
	public static boolean isEmpty(String str) {
		return (str == null) || (str.trim().length() == 0);
	}
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}
	private static int min(int one, int two, int three) {
		return (one = one < two ? one : two) < three ? one : three;
	}
}
