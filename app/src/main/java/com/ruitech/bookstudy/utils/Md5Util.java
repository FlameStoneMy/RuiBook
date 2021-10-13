package com.ruitech.bookstudy.utils;

import android.text.TextUtils;

import com.nostra13.universalimageloader.utils.IoUtils;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

/**
 * Created by pengwei.liao on 2020/5/27.
 */
public class Md5Util {
    public static boolean checkMd5(File file, String md5) {
        if (file == null || !file.isFile() || TextUtils.isEmpty(md5)) {
            return true;
        }
        String fileMd5 = md5(file);
        if (TextUtils.isEmpty(fileMd5)) {
            return true;
        }
        return TextUtils.equals(md5, fileMd5);
    }

    public static String md5(File file) {
        if (!file.isFile()) {
            return "";
        }
        int len;
        byte[] buffer = new byte[8192];
        FileInputStream fis = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(file);
            while ((len = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
            return bytesToHexString(digest.digest());
        } catch (Exception ignore) {
        } finally {
            IoUtils.closeSilently(fis);
        }
        return "";
    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String h = Integer.toHexString(0xFF & b);
            if (h.length() < 2) {
                sb.append("0");
            }
            sb.append(h);
        }
        return sb.toString();
    }
}
