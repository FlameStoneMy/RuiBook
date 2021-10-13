package com.ruitech.bookstudy.utils;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;

import com.nostra13.universalimageloader.utils.IoUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public final class ZipUtil {
    private static final String TAG = "ZipUtil";

    public static String compress(String content) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GZIPOutputStream gos = null;
        try {
            gos = new GZIPOutputStream(bos);
            gos.write(content.getBytes());
            gos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeSilently(gos);
        }

        return Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT);
    }

    public static void unzip(File zipFile, File unzipDir) throws IOException {
        unzip(zipFile, unzipDir, true);
    }

    public static void unzip(File zipFile, File unzipDir, boolean clearDir) throws IOException {
        if (!unzipDir.isDirectory()) {
            unzipDir.mkdirs();
        }
        if (clearDir) {
            clear(unzipDir);
        }
        ZipFile zip = new ZipFile(zipFile);
        Enumeration<? extends ZipEntry> entries = zip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry zipEntry = entries.nextElement();
            if (zipEntry.getName().contains("MACOSX")) {
                continue;
            }
            if (zipEntry.isDirectory()) {
                File dir = new File(unzipDir, zipEntry.getName());
                if (!dir.isDirectory()) {
                    dir.mkdirs();
                }
            } else {
                InputStream inputStream = zip.getInputStream(zipEntry);
                File file = new File(unzipDir, zipEntry.getName());
                BufferedSource buffer = Okio.buffer(Okio.source(inputStream));
                BufferedSink buffer1 = Okio.buffer(Okio.sink(file));
                buffer1.writeAll(buffer);
                buffer1.flush();
                buffer1.close();
                inputStream.close();
            }
        }
    }

    public static String unzipPic(String name, File zipFile, File unzipDir) throws IOException {
        clear(unzipDir);
        ZipFile zip = new ZipFile(zipFile);
        ZipEntry zipEntry = zip.getEntry(name);

        if (zipEntry == null) {
            int index = name.lastIndexOf(".");
            if (index > 0) {
                String newName = name.substring(0, index);
                String[] extensions = new String[]{".jpg", ".png", ".jpeg"};
                for (String extension : extensions) {
                    zipEntry = zip.getEntry(String.format("%s%s", newName, extension));
                    if (zipEntry != null) {
                        break;
                    }
                }
            }
        }
        if (zipEntry != null) {
            InputStream inputStream = zip.getInputStream(zipEntry);
            BufferedSource buffer = Okio.buffer(Okio.source(inputStream));
            BufferedSink buffer1 = Okio.buffer(Okio.sink(new File(unzipDir, zipEntry.getName())));
            buffer1.writeAll(buffer);
            buffer1.flush();
            buffer1.close();
            inputStream.close();
            return zipEntry.getName();
        }
        return "";
    }

    public static boolean checkAndExtractHeader(File file, String md5, Map<String, ZipEntry> result) {
        long end = -1;
        ByteArrayOutputStream bos = null;
        try {
            Pair<Long, Long> pair = findEnd(file);
            long size = pair.first;
            end = pair.second;
            bos = new ByteArrayOutputStream((int) size + 128);
        } catch (Exception e) {
            Log.w(TAG, "find zip end exception", e);
        }

        int readLen;
        long readAll = 0;
        boolean needParse = false;
        byte[] buffer = new byte[8192];
        FileInputStream fis = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(file);
            while ((readLen = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, readLen);
                if (needParse) {
                    bos.write(buffer, 0, readLen);
                } else {
                    if (readAll + readLen >= end + 1 && end > 0 && bos != null) {
                        needParse = true;
                        bos.write(buffer,
                                (int) (readLen - (readAll + readLen - end)),
                                (int) (readAll + readLen - end));

                    }
                    readAll += readLen;
                }
            }

            String fileMd5 = Md5Util.bytesToHexString(digest.digest());
            if (!TextUtils.isEmpty(fileMd5) && !TextUtils.equals(md5, fileMd5)) {
                return false;
            }
        } catch (Exception e) {
            Log.w(TAG, "read zip input stream exception", e);
        } finally {
            IoUtils.closeSilently(fis);
        }

        if (bos != null) {
            byte[] bytes = bos.toByteArray();
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            BufferedSource source = Okio.buffer(Okio.source(bis));
            try {
                parseEntry(source, result);
            } catch (Exception e) {
                result.clear();
                Log.w(TAG, "parse zip entry exception", e);
            } finally {
                IoUtils.closeSilently(source);
                IoUtils.closeSilently(bis);
            }
        }

        if (result.isEmpty()) {
            try {
                ZipFile zipFile = new ZipFile(file);
                Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
                while (enumeration.hasMoreElements()) {
                    ZipEntry zipEntry = enumeration.nextElement();
                    result.put(zipEntry.getName(), zipEntry);
                }
            } catch (IOException e) {
                Log.w(TAG, "extract zip entry exception", e);
            }
        }

        return true;
    }

    public static void parseEntry(BufferedSource buffer, Map<String, ZipEntry> result) throws IOException {
        while (true) {
            int magicNumber = buffer.readIntLe();

            if (0x06054b50 == magicNumber) {//end
                break;
            }

            if (0x02014b50 != magicNumber) {
                throw new IllegalStateException();
            }

            buffer.readShortLe();

            int versionNeed = buffer.readShort() & 0xffff;//version need

            int flag = buffer.readShortLe() & 0xffff;//flag

            buffer.readShort();//compression method
            int lastModified = buffer.readInt();//last modified time

            long crc32 = buffer.readIntLe() & 0xffffffffL;//crc32;
            long dataSize = buffer.readIntLe() & 0xffffffffL;//compressed size
            long uncompressedSize = buffer.readIntLe();//uncompressed size
            int nameLength = buffer.readShortLe() & 0xffff;//name length
            int extraLength = buffer.readShortLe() & 0xffff;//extra field length
            int commentLength = buffer.readShortLe() & 0xfff;//comment length
            buffer.readShortLe();

            buffer.readShort();
            buffer.readInt();

            long offset = buffer.readIntLe() & 0xffffffffL;//local header offset

            String name = new String(buffer.readByteArray(nameLength));//name

            ZipEntry entry = new ZipEntry(name);
            entry.setCrc(crc32);
            entry.setSize(uncompressedSize);
            entry.setCompressedSize(dataSize);

            result.put(name, entry);

            System.out.println(name + " : " + crc32 + " : " + dataSize + " : " + uncompressedSize);
            if (extraLength > 0) {
                buffer.skip(extraLength);
            }
            if (commentLength > 0) {
                buffer.skip(commentLength);
            }
        }
    }

    public static Pair<Long, Long> findEnd(File file) throws Exception {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        try {
            long length = randomAccessFile.length();
            randomAccessFile.seek(length - 128);
            byte[] bytes = new byte[128];
            int read = randomAccessFile.read(bytes, 0, 128);
            BufferedSource buffer = null;
            for (int i = 0; i < read; i++) {
                if (bytes[i] == 'P'
                        && bytes[i + 1] == 'K'
                        && bytes[i + 2] == '\005'
                        && bytes[i + 3] == '\006') {

                    buffer = Okio.buffer(Okio.source(new ByteArrayInputStream(bytes, i + 4, read - i - 4)));
                    break;
                }
            }

            if (buffer != null) {
                int nd = buffer.readShortLe() & 0xffff;
                int ds = buffer.readShortLe() & 0xffff;
                int nc = buffer.readShortLe() & 0xffff;
                int tc = buffer.readShortLe() & 0xffff;

                long size = buffer.readIntLe() & 0xffffffffL;
                long offset = buffer.readIntLe() & 0xffffffffL;
                return new Pair<>(size, offset);
            } else {
                throw new IllegalStateException();
            }
        } finally {
            randomAccessFile.close();
        }
    }

    public static boolean checkZipEntry(ZipEntry stash, ZipEntry real) {
        if (stash == null || real == null) {
            return false;
        }
        return stash.getCrc() == real.getCrc()
                && stash.getSize() == real.getSize()
                && stash.getCompressedSize() == real.getCompressedSize();
    }

    public static InputStream read(ZipFile zipFile, ZipEntry zipEntry) {
        try {
            return zipFile.getInputStream(zipEntry);
        } catch (Exception e) {
            Log.w(TAG, "read zip InputStream exception", e);
        }
        return new ByteArrayInputStream(new byte[]{});
    }

    public static InputStream read(ZipFile zipFile, ZipEntry realEntry, ZipEntry stashEntry) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream((int) realEntry.getSize());
        InputStream inputStream = null;
        byte[] buffer = new byte[8192];
        int len;
        try {
            CRC32 crc32 = new CRC32();
            inputStream = zipFile.getInputStream(realEntry);
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
                crc32.update(buffer, 0, len);
            }
            if (crc32.getValue() == stashEntry.getCrc()) {
                return new ByteArrayInputStream(outputStream.toByteArray());
            }
        } catch (Exception e) {
            Log.w(TAG, "read zip InputStream exception", e);
        } finally {
            IoUtils.closeSilently(inputStream);
        }
        return new ByteArrayInputStream(new byte[]{});
    }

    public static void copy(File source, File dest) {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } catch (Exception e) {
            Log.w(TAG, "copy file exception", e);
        } finally {
            IoUtils.closeSilently(inputChannel);
            IoUtils.closeSilently(outputChannel);
        }
    }

    public static boolean clear(File dir) {
        File[] files = dir.listFiles();
        if (files == null)
            return true;

        boolean ret = true;
        for (File file : files) {
            if (file.isDirectory()) {
                return clear(file);
            } else if (file.isFile()) {
                ret = ret && file.delete();
            }
        }
        return ret;
    }

    public static void delete(File... files) {
        if (files == null || files.length == 0) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                clear(file);
            } else if (file.isFile()) {
                file.delete();
            }
        }
    }

    public static class ParseZipException extends IllegalStateException {
        public ParseZipException(Throwable cause) {
            super(cause);
        }
    }
}
