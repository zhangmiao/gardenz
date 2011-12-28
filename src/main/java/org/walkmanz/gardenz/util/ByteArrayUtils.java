<<<<<<< HEAD
package org.walkmanz.gardenz.util;

public final class ByteArrayUtils {

	private static final int INT_SIZE = 4;
	private static final long LOW_8BIT_MASK = 0xFF;
	private static final int LONG_SIZE = 8;

	public static String toHexString(byte[] bb) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bb) {
			String bStr = Integer.toHexString(b & 0xff).toUpperCase();
			sb.append(bStr.length() == 1 ? ("0" + bStr) : bStr);
			sb.append(" ");
		}
		return sb.toString();
	}

	public static byte[] toByteArray(Object o) {
		byte[] bb = null;
		if (o != null) {
			if (!o.getClass().isArray()) {
				if (o instanceof Byte) {
					return new byte[] { (Byte) o };
				} else if (o instanceof Integer) {
					bb = intToByteArray(o);
				} else if (o instanceof Long) {
					bb = longToByteArray(o);
				}
			} else {
				if (o instanceof byte[]) {
					return (byte[]) o;
				} else if (o instanceof int[]) {
					int[] ii = (int[]) o;
					bb = new byte[ii.length * INT_SIZE];
					for (int i = 0; i < ii.length; i++) {
						byte[] bbb = toByteArray(ii[i]);
						System.arraycopy(bbb, 0, bb, i * INT_SIZE, INT_SIZE);
					}
				} else if (o instanceof long[]) {
					long[] ii = (long[]) o;
					bb = new byte[ii.length * LONG_SIZE];
					for (int i = 0; i < ii.length; i++) {
						byte[] bbb = toByteArray(ii[i]);
						System.arraycopy(bbb, 0, bb, i * LONG_SIZE, LONG_SIZE);
					}
				}
			}
		} else {
			throw new IllegalArgumentException("UNSUPPORTED TYPE:"
					+ o.getClass().getSimpleName());
		}
		return bb;
	}

	public static byte[] assemble(byte[] dest, byte[]... src) {
		int index = 0;
		int length = dest.length;
		for (byte[] bb : src) {
			for (byte b : bb) {
				dest[index++] = b;
				if (index >= length) {
					return dest;
				}
			}
		}
		return dest;
	}

	/**
	 * @param o
	 * @return
	 */
	private static byte[] longToByteArray(Object o) {
		byte[] bb;
		long i = (Long) o;
		bb = new byte[LONG_SIZE];
		for (int j = 7; j >= 0; j--) {
			bb[7 - j] = (byte) ((i >> (j * 8)) & LOW_8BIT_MASK);
		}
		return bb;
	}

	/**
	 * 
	 * @param bb
	 * @return
	 */
	public static int byteArrayToInt(byte[] bb) {
		if (bb.length != 4) {
			throw new IllegalArgumentException("byte array's length must be 4.");
		}
		int i = 0x0;
		i = (int) (i | ((bb[0] & LOW_8BIT_MASK) << 24));
		i = (int) (i | ((bb[1] & LOW_8BIT_MASK) << 16));
		i = (int) (i | ((bb[2] & LOW_8BIT_MASK) << 8));
		i = (int) (i | ((bb[3] & LOW_8BIT_MASK)));
		return i;
	}

	/**
	 * 
	 * @param bb
	 * @return
	 */
	public static long byteArrayToLong(byte[] bb) {
		if (bb.length != 8) {
			throw new IllegalArgumentException("byte array's length must be 8.");
		}
		long i = 0;
		i = i | ((bb[0] & LOW_8BIT_MASK) << 56);
		i = i | ((bb[1] & LOW_8BIT_MASK) << 48);
		i = i | ((bb[2] & LOW_8BIT_MASK) << 40);
		i = i | ((bb[3] & LOW_8BIT_MASK) << 32);
		i = i | ((bb[4] & LOW_8BIT_MASK) << 24);
		i = i | ((bb[5] & LOW_8BIT_MASK) << 16);
		i = i | ((bb[6] & LOW_8BIT_MASK) << 8);
		i = i | ((bb[7] & LOW_8BIT_MASK));
		return i;
	}

	/**
	 * @param o
	 * @return
	 */
	private static byte[] intToByteArray(Object o) {
		byte[] bb;
		int i = (Integer) o;
		bb = new byte[INT_SIZE];
		for (int j = 3; j >= 0; j--) {
			bb[3 - j] = (byte) ((i >> (j * 8)) & LOW_8BIT_MASK);
		}
		return bb;
	}

	/**
	 * 
	 * @param bb
	 * @return
	 */
	public static int[] byteArrayToIntArray(byte[] bb) {
		// TODO Auto-generated method stub
		if (bb == null || bb.length % INT_SIZE != 0) {
			throw new IllegalArgumentException(
					"byte array's length must divisible by 4.");
		} else {
			int[] ii = new int[bb.length / INT_SIZE];
			int index = 0;
			for (int i = 0; i < bb.length; i += INT_SIZE) {
				byte[] bb4 = new byte[INT_SIZE];
				System.arraycopy(bb, i, bb4, 0, INT_SIZE);
				ii[index++] = byteArrayToInt(bb4);
			}
			return ii;
		}
	}
}
=======
package org.walkmanz.gardenz.util;

public final class ByteArrayUtils {

	private static final int INT_SIZE = 4;
	private static final long LOW_8BIT_MASK = 0xFF;
	private static final int LONG_SIZE = 8;

	public static String toHexString(byte[] bb) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bb) {
			String bStr = Integer.toHexString(b & 0xff).toUpperCase();
			sb.append(bStr.length() == 1 ? ("0" + bStr) : bStr);
			sb.append(" ");
		}
		return sb.toString();
	}

	public static byte[] toByteArray(Object o) {
		byte[] bb = null;
		if (o != null) {
			if (!o.getClass().isArray()) {
				if (o instanceof Byte) {
					return new byte[] { (Byte) o };
				} else if (o instanceof Integer) {
					bb = intToByteArray(o);
				} else if (o instanceof Long) {
					bb = longToByteArray(o);
				}
			} else {
				if (o instanceof byte[]) {
					return (byte[]) o;
				} else if (o instanceof int[]) {
					int[] ii = (int[]) o;
					bb = new byte[ii.length * INT_SIZE];
					for (int i = 0; i < ii.length; i++) {
						byte[] bbb = toByteArray(ii[i]);
						System.arraycopy(bbb, 0, bb, i * INT_SIZE, INT_SIZE);
					}
				} else if (o instanceof long[]) {
					long[] ii = (long[]) o;
					bb = new byte[ii.length * LONG_SIZE];
					for (int i = 0; i < ii.length; i++) {
						byte[] bbb = toByteArray(ii[i]);
						System.arraycopy(bbb, 0, bb, i * LONG_SIZE, LONG_SIZE);
					}
				}
			}
		} else {
			throw new IllegalArgumentException("UNSUPPORTED TYPE:"
					+ o.getClass().getSimpleName());
		}
		return bb;
	}

	public static byte[] assemble(byte[] dest, byte[]... src) {
		int index = 0;
		int length = dest.length;
		for (byte[] bb : src) {
			for (byte b : bb) {
				dest[index++] = b;
				if (index >= length) {
					return dest;
				}
			}
		}
		return dest;
	}

	/**
	 * @param o
	 * @return
	 */
	private static byte[] longToByteArray(Object o) {
		byte[] bb;
		long i = (Long) o;
		bb = new byte[LONG_SIZE];
		for (int j = 7; j >= 0; j--) {
			bb[7 - j] = (byte) ((i >> (j * 8)) & LOW_8BIT_MASK);
		}
		return bb;
	}

	/**
	 * 
	 * @param bb
	 * @return
	 */
	public static int byteArrayToInt(byte[] bb) {
		if (bb.length != 4) {
			throw new IllegalArgumentException("byte array's length must be 4.");
		}
		int i = 0x0;
		i = (int) (i | ((bb[0] & LOW_8BIT_MASK) << 24));
		i = (int) (i | ((bb[1] & LOW_8BIT_MASK) << 16));
		i = (int) (i | ((bb[2] & LOW_8BIT_MASK) << 8));
		i = (int) (i | ((bb[3] & LOW_8BIT_MASK)));
		return i;
	}

	/**
	 * 
	 * @param bb
	 * @return
	 */
	public static long byteArrayToLong(byte[] bb) {
		if (bb.length != 8) {
			throw new IllegalArgumentException("byte array's length must be 8.");
		}
		long i = 0;
		i = i | ((bb[0] & LOW_8BIT_MASK) << 56);
		i = i | ((bb[1] & LOW_8BIT_MASK) << 48);
		i = i | ((bb[2] & LOW_8BIT_MASK) << 40);
		i = i | ((bb[3] & LOW_8BIT_MASK) << 32);
		i = i | ((bb[4] & LOW_8BIT_MASK) << 24);
		i = i | ((bb[5] & LOW_8BIT_MASK) << 16);
		i = i | ((bb[6] & LOW_8BIT_MASK) << 8);
		i = i | ((bb[7] & LOW_8BIT_MASK));
		return i;
	}

	/**
	 * @param o
	 * @return
	 */
	private static byte[] intToByteArray(Object o) {
		byte[] bb;
		int i = (Integer) o;
		bb = new byte[INT_SIZE];
		for (int j = 3; j >= 0; j--) {
			bb[3 - j] = (byte) ((i >> (j * 8)) & LOW_8BIT_MASK);
		}
		return bb;
	}

	/**
	 * 
	 * @param bb
	 * @return
	 */
	public static int[] byteArrayToIntArray(byte[] bb) {
		// TODO Auto-generated method stub
		if (bb == null || bb.length % INT_SIZE != 0) {
			throw new IllegalArgumentException(
					"byte array's length must divisible by 4.");
		} else {
			int[] ii = new int[bb.length / INT_SIZE];
			int index = 0;
			for (int i = 0; i < bb.length; i += INT_SIZE) {
				byte[] bb4 = new byte[INT_SIZE];
				System.arraycopy(bb, i, bb4, 0, INT_SIZE);
				ii[index++] = byteArrayToInt(bb4);
			}
			return ii;
		}
	}
}
>>>>>>> 98ae9342edf48ad40a999cec1b2213e8fb3c2eae
