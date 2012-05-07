package org.walkmanz.gardenz.util;

import java.nio.charset.Charset;

public class BytesUtils {
	
	public static final String ENCODER = "UTF-8";
	
	public static byte[] getBytes(short data) {
		byte[] bytes = new byte[2];
		bytes[0] = (byte) (data & 0xff);
		bytes[1] = (byte) ((data & 0xff00) >> 8);
		return bytes;
	}

	public static byte[] getBytes(char data) {
		byte[] bytes = new byte[2];
		bytes[0] = (byte) (data);
		bytes[1] = (byte) (data >> 8);
		return bytes;
	}

	public static byte[] getBytes(int data) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) (data & 0xff);
		bytes[1] = (byte) ((data & 0xff00) >> 8);
		bytes[2] = (byte) ((data & 0xff0000) >> 16);
		bytes[3] = (byte) ((data & 0xff000000) >> 24);
		return bytes;
	}

	public static byte[] getBytes(long data) {
		byte[] bytes = new byte[8];
		bytes[0] = (byte) (data & 0xff);
		bytes[1] = (byte) ((data >> 8) & 0xff);
		bytes[2] = (byte) ((data >> 16) & 0xff);
		bytes[3] = (byte) ((data >> 24) & 0xff);
		bytes[4] = (byte) ((data >> 32) & 0xff);
		bytes[5] = (byte) ((data >> 40) & 0xff);
		bytes[6] = (byte) ((data >> 48) & 0xff);
		bytes[7] = (byte) ((data >> 56) & 0xff);
		return bytes;
	}

	public static byte[] getBytes(float data) {
		int intBits = Float.floatToIntBits(data);
		return getBytes(intBits);
	}

	public static byte[] getBytes(double data) {
		long intBits = Double.doubleToLongBits(data);
		return getBytes(intBits);
	}

	public static byte[] getBytes(String data, String charsetName) {
		Charset charset = Charset.forName(charsetName);
		return data.getBytes(charset);
	}

	public static byte[] getBytes(String data) {
		return getBytes(data, ENCODER);
	}

	public static short getShort(byte[] bytes) {
		return (short) ((0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)));
	}

	public static char getChar(byte[] bytes) {
		return (char) ((0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)));
	}

	public static int getInt(byte[] bytes) {
		return (0xff & bytes[0]) | (0xff00 & (bytes[1] << 8))
				| (0xff0000 & (bytes[2] << 16))
				| (0xff000000 & (bytes[3] << 24));
	}

	public static long getLong(byte[] bytes) {
		return (0xffL & (long) bytes[0]) | (0xff00L & ((long) bytes[1] << 8))
				| (0xff0000L & ((long) bytes[2] << 16))
				| (0xff000000L & ((long) bytes[3] << 24))
				| (0xff00000000L & ((long) bytes[4] << 32))
				| (0xff0000000000L & ((long) bytes[5] << 40))
				| (0xff000000000000L & ((long) bytes[6] << 48))
				| (0xff00000000000000L & ((long) bytes[7] << 56));
	}

	public static float getFloat(byte[] bytes) {
		return Float.intBitsToFloat(getInt(bytes));
	}

	public static double getDouble(byte[] bytes) {
		long l = getLong(bytes);
		System.out.println(l);
		return Double.longBitsToDouble(l);
	}

	public static String getString(byte[] bytes, String charsetName) {
		return new String(bytes, Charset.forName(charsetName));
	}

	public static String getString(byte[] bytes) {
		return getString(bytes, ENCODER);
	}

	/**
	 * �ֽ������е� indexof �������� String ���е� indexOf����
	 * 
	 * @para source Դ�ֽ�����
	 * @para search Ŀ���ַ���
	 * @para start ��������ʼ��
	 * @return ����ҵ�������search�ĵ�һ���ֽ���buffer�е��±꣬û���򷵻�-1
	 */
	private static int byteIndexOf(byte[] source, String search, int start) {
		return byteIndexOf(source, search.getBytes(), start);
	}

	/**
	 * �ֽ������е� indexof �������� String ���е� indexOf����
	 * 
	 * @para source Դ�ֽ�����
	 * @para search Ŀ���ֽ�����
	 * @para start ��������ʼ��
	 * @return ����ҵ�������search�ĵ�һ���ֽ���buffer�е��±꣬û���򷵻�-1
	 */
	private static int byteIndexOf(byte[] source, byte[] search, int start) {
		int i;
		if (search.length == 0) {
			return 0;
		}
		int max = source.length - search.length;
		if (max < 0)
			return -1;
		if (start > max)
			return -1;
		if (start < 0)
			start = 0;
		// ��source���ҵ�search�ĵ�һ��Ԫ��
		searchForFirst: for (i = start; i <= max; i++) {
			if (source[i] == search[0]) {
				// �ҵ���search�еĵ�һ��Ԫ�غ󣬱Ƚ�ʣ��Ĳ����Ƿ����
				int k = 1;
				while (k < search.length) {
					if (source[k + i] != search[k]) {
						continue searchForFirst;
					}
					k++;
				}
				return i;
			}
		}
		return -1;
	}

	/**
	 * ���ڴ�һ���ֽ���������ȡһ���ֽ����� ������ String ���substring()
	 */
	private static byte[] subBytes(byte[] source, int from, int end) {
		byte[] result = new byte[end - from];
		System.arraycopy(source, from, result, 0, end - from);
		return result;
	}

	/**
	 * ���ڴ�һ���ֽ���������ȡһ���ַ��������� String ���substring()
	 */
	private static String subBytesString(byte[] source, int from, int end) {
		return new String(subBytes(source, from, end));
	}

	/**
	 * 
	 * �����ַ���Sת��Ϊ�ֽ������ĳ���
	 * 
	 */
	private static int bytesLen(String s) {
		return s.getBytes().length;
	}
}