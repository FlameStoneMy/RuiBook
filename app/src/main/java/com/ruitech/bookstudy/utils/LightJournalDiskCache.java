package com.ruitech.bookstudy.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.nostra13.universalimageloader.cache.disc.impl.BaseDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.utils.IoUtils;
import com.nostra13.universalimageloader.utils.L;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static android.os.Environment.MEDIA_MOUNTED;

public class LightJournalDiskCache extends BaseDiskCache {
    public static final String JOURNAL = "journalmx/journal";
    private final int maxSize;
    private boolean init;
    private int size;
    private Map<String, Entry> cache = new HashMap<>();
    private int hitCount;

    public static File getCacheDirectory(Context context) {
        File appCacheDir = null;
        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException e) { // (sh)it happens (Issue #660)
            externalStorageState = "";
        } catch (IncompatibleClassChangeError e) { // (sh)it happens too (Issue #989)
            externalStorageState = "";
        }
        if (MEDIA_MOUNTED.equals(externalStorageState)) {
            appCacheDir = getExternalCacheDir(context);
        }
        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }
        if (appCacheDir == null) {
            String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
            L.w("Can't define system cache directory! '%s' will be used.", cacheDirPath);
            appCacheDir = new File(cacheDirPath);
        }
        return appCacheDir;
    }

    private static File getExternalCacheDir(Context context) {
        File externalCacheDir = context.getExternalCacheDir();
//        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
        File appCacheDir = new File(externalCacheDir, "cache");
        if (!appCacheDir.exists()) {
            if (!appCacheDir.mkdirs()) {
                L.w("Unable to create external cache directory");
                return null;
            }
            try {
                new File(appCacheDir, ".nomedia").createNewFile();
            } catch (IOException e) {
                L.i("Can't create \".nomedia\" file in application external cache directory");
            }
        }
        return appCacheDir;
    }

    public LightJournalDiskCache(File cacheDir, int maxSize) {
        super(cacheDir, null, new Md5FileNameGenerator());
        this.maxSize = maxSize;
    }

    @Override
    public synchronized File get(String imageUri) {
        if (!init) {
            init();
        }

        File file = super.get(imageUri);
        if (file != null && file.exists()) {
            hitCount++;
            rememberUsage(file, hitCount % 10 == 0);
        }

        return file;
    }

    @Override
    public synchronized boolean save(String imageUri, Bitmap bitmap) throws IOException {
        if (!init) {
            init();
        }

        boolean save = super.save(imageUri, bitmap);
        if (save) {
            rememberUsage(imageUri, true);
            File file = getFile(imageUri);
            size += file.length();
        }

        tryToTrim();

        return save;
    }

    @Override
    public synchronized boolean save(String imageUri, InputStream imageStream, IoUtils.CopyListener listener) throws IOException {
        if (!init) {
            init();
        }

        boolean save = super.save(imageUri, imageStream, listener);
        if (save) {
            rememberUsage(imageUri, true);
            File file = getFile(imageUri);
            size += file.length();
        }

        tryToTrim();

        return save;
    }

    @Override
    public synchronized boolean remove(String imageUri) {
        if (!init) {
            init();
        }

        long length = 0;

        File file = getFile(imageUri);
        if (file.exists()) {
            length = file.length();
        }

        boolean remove = super.remove(imageUri);
        if (remove) {
            size -= length;
            cache.remove(file.getName());
        }
        return remove;
    }

    @Override
    public synchronized void clear() {

        cache.clear();

        size = 0;
        hitCount = 0;

        super.clear();
    }

    @Override
    public synchronized void close() {
        super.close();

        init = false;
        size = 0;
        cache.clear();
        hitCount = 0;
    }

    private void init() {
        if (init) return;

        init = true;

        try {
            readCache();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readCache() {
        File directory = getDirectory();
        File journalFile = new File(directory, JOURNAL);
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(journalFile));
            String line = bufferedReader.readLine();
            while (line != null) {
                try {
                    Entry entry = parseLine(line);
                    cache.put(entry.name, entry);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && !isIgnored(file)) {
                    size += file.length();

                    Entry entry = cache.get(file.getName());
                    if (entry == null) {
                        entry = new Entry(file.getName(), file.lastModified());
                        cache.put(entry.name, entry);
                    }
                }
            }
        }

    }

    private void flushJournal() throws Exception {
        if (!init)
            return;

        File directory = getDirectory();
        File journalFile = new File(directory, JOURNAL);
        journalFile.getParentFile().mkdirs();
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(journalFile));
            for (Entry entry : cache.values()) {
                bufferedWriter.write(entry.name + "_" + entry.lastModified);
                bufferedWriter.newLine();
            }
        } finally {
            IoUtils.closeSilently(bufferedWriter);
        }
    }

    private Entry parseLine(String line) {
        int find = line.indexOf('_');
        if (find == -1)
            return null;

        String name = line.substring(0, find);
        long lastModified = Long.parseLong(line.substring(find + 1));

        return new Entry(name, lastModified);
    }

    private void rememberUsage(String imageUri, boolean save) {
        File file = getFile(imageUri);
        rememberUsage(file, save);
    }

    private void rememberUsage(File file, boolean save) {
        Entry entry = cache.get(file.getName());
        if (entry == null) {
            entry = new Entry(file.getName(), file.lastModified());
            cache.put(entry.name, entry);
        }

        entry.lastModified = System.currentTimeMillis();
        file.setLastModified(entry.lastModified);

        if (save) {
            try {
                flushJournal();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void tryToTrim() {
        if (size < maxSize)
            return;

        size = trim(getDirectory(), size, maxSize);

    }

    protected int trim(File directory, int size, int maxSize) {
        File[] files = directory.listFiles();
        if (files != null) {
            ArrayList<File> list = new ArrayList<>(Arrays.asList(files));
            sortByModified(list);

            for (File file : list) {
                if (isIgnored(file))
                    continue;

                long length = file.length();
                if (file.delete()) {
                    size -= length;
                }

                if (size < maxSize)
                    break;
            }
        }

        return size;
    }

    private boolean isIgnored(File file) {
        return file.getName().startsWith("journal");
    }

    protected void sortByModified(ArrayList<File> list) {
        Collections.sort(list, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                long f1 = getLastModified(o1);
                long f2 = getLastModified(o2);
                if (f1>f2){
                    return 1;
                } else if (f1<f2){
                    return -1;
                } else {
                    return 0;
                }
//                return (int) (getLastModified(o1) - getLastModified(o2));
            }
        });
    }

    protected long getLastModified(File file) {
        Entry entry = cache.get(file.getName());
        if (entry != null) {
            return entry.lastModified;
        }

        return file.lastModified();
    }

    static class Entry {
        String name;
        long lastModified;

        public Entry(String name, long lastModified) {
            this.name = name;
            this.lastModified = lastModified;
        }
    }
}
