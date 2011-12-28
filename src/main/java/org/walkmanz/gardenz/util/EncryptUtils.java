<<<<<<< HEAD


package org.walkmanz.gardenz.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class EncryptUtils {

    /**
     * Encrypt byte array.
     */
    public final static byte[] encrypt(byte[] source, String algorithm)
            throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        md.reset();
        md.update(source);
        return md.digest();
    }

    /**
     * Encrypt string
     */
    public final static String encrypt(String source, String algorithm)
            throws NoSuchAlgorithmException {
        byte[] resByteArray = encrypt(source.getBytes(), algorithm);
        return toHexString(resByteArray);
    }

    /**
     * Encrypt string using MD5 algorithm
     */
    public final static String encryptMD5(String source) {
        if (source == null) {
            source = "";
        }

        String result = "";
        try {
            result = encrypt(source, "MD5");
        } catch (NoSuchAlgorithmException ex) {
            // this should never happen
            throw new RuntimeException(ex);
        }
        return result;
    }

    /**
     * Encrypt string using SHA algorithm
     */
    public final static String encryptSHA(String source) {
        if (source == null) {
            source = "";
        }

        String result = "";
        try {
            result = encrypt(source, "SHA");
        } catch (NoSuchAlgorithmException ex) {
            // this should never happen
            throw new RuntimeException(ex);
        }
        return result;
    }
    
    private final static String toHexString(byte[] res) {
        StringBuilder sb = new StringBuilder(res.length << 1);
        for (int i = 0; i < res.length; i++) {
            String digit = Integer.toHexString(0xFF & res[i]);
            if (digit.length() == 1) {
                sb.append('0');
            }
            sb.append(digit);
        }
        return sb.toString().toUpperCase();
    }

}
=======


package org.walkmanz.gardenz.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class EncryptUtils {

    /**
     * Encrypt byte array.
     */
    public final static byte[] encrypt(byte[] source, String algorithm)
            throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        md.reset();
        md.update(source);
        return md.digest();
    }

    /**
     * Encrypt string
     */
    public final static String encrypt(String source, String algorithm)
            throws NoSuchAlgorithmException {
        byte[] resByteArray = encrypt(source.getBytes(), algorithm);
        return toHexString(resByteArray);
    }

    /**
     * Encrypt string using MD5 algorithm
     */
    public final static String encryptMD5(String source) {
        if (source == null) {
            source = "";
        }

        String result = "";
        try {
            result = encrypt(source, "MD5");
        } catch (NoSuchAlgorithmException ex) {
            // this should never happen
            throw new RuntimeException(ex);
        }
        return result;
    }

    /**
     * Encrypt string using SHA algorithm
     */
    public final static String encryptSHA(String source) {
        if (source == null) {
            source = "";
        }

        String result = "";
        try {
            result = encrypt(source, "SHA");
        } catch (NoSuchAlgorithmException ex) {
            // this should never happen
            throw new RuntimeException(ex);
        }
        return result;
    }
    
    private final static String toHexString(byte[] res) {
        StringBuilder sb = new StringBuilder(res.length << 1);
        for (int i = 0; i < res.length; i++) {
            String digit = Integer.toHexString(0xFF & res[i]);
            if (digit.length() == 1) {
                sb.append('0');
            }
            sb.append(digit);
        }
        return sb.toString().toUpperCase();
    }

}
>>>>>>> 98ae9342edf48ad40a999cec1b2213e8fb3c2eae
