package org.walkmanz.gardenz.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.MessageDigest;
import java.security.PrivilegedAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IO工具类
 * 
 */
public class IoUtils {
	
	private static final Logger LOG = LoggerFactory.getLogger(IoUtils.class);

	/**
	 * 根据文件名，生成存储文件的文件名，规则为：str + 时间,然后做md5 最后在基础之上加上文件后缀 如果生成失败，返回null
	 * 
	 */
	public static String generateStorageKeyId(String str) {
		String type = null;
		type = getFileType(str);
		String result = getMD5(str + System.nanoTime());
		result = (type == null) ? result : (result + "." + type);
		return result;
	}

	/**
	 * 获得字符串的md5编码,如果生成失败，返回null
	 * 
	 * @param str
	 * @return
	 */
	public static String getMD5(String str) {
		MessageDigest messageDigest = null;

		try {
			messageDigest = MessageDigest.getInstance("MD5");

			messageDigest.reset();

			messageDigest.update(str.getBytes("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		byte[] byteArray = messageDigest.digest();

		StringBuffer md5StrBuff = new StringBuffer();

		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}

		return md5StrBuff.toString();
	}

	/**
	 * 获得文件的类型（文件名后缀标识的文件类型）
	 * 
	 * @param fileName
	 * @return 文件类型（例如，jpg、pdf等），如果无法判断文件类型（如没有后缀），返回"unknown"
	 */
	public static String getFileType(String fileName) {
		String type = "unknown";
		int index = fileName.lastIndexOf(".");
		if (index > 0 && index + 1 < fileName.length()) {
			type = fileName.substring(index + 1);
		}
		return type;
	}

	public final static void delete(File file) throws IOException {
		if (!file.exists()) {
			return;
		} else if (file.isDirectory()) {
			deleteDir(file);
		} else {
			deleteFile(file);
		}
	}

	private final static void deleteDir(File dir) throws IOException {
		File[] children = dir.listFiles();

		if (children == null) {
			return;
		}

		for (int i = 0; i < children.length; i++) {
			File file = children[i];
			delete(file);
		}

		if (!dir.delete()) {
			throw new IOException("Failed to delete directory: " + dir);
		}

	}

	private final static void deleteFile(File file) throws IOException {
		if (!file.delete()) {
			// hack around bug where files will sometimes not be deleted on
			// Windows
			if (OSUtils.isFamilyWindows()) {
				System.gc();
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
			if (!file.delete()) {
				throw new IOException("Failed to delete file: " + file);
			}
		}
	}
	
	//释放MappedByteBuffer空间
	public static void clean(final Object buffer) {
        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
                try {
                    Method getCleanerMethod = buffer.getClass().getMethod("cleaner", new Class[0]);
                    getCleanerMethod.setAccessible(true);
                    sun.misc.Cleaner cleaner = (sun.misc.Cleaner) getCleanerMethod.invoke(buffer, new Object[0]);
                    cleaner.clean();
                } catch (Exception e) {
                	LOG.error("close logindexy file error:", e);
                }
                return null;
            }
        });

    }

}
