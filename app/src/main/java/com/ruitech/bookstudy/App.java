package com.ruitech.bookstudy;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.ruitech.bookstudy.utils.Executors;
import com.ruitech.bookstudy.utils.LightJournalDiskCache;

public class App extends Application {

    private static Context context;
    public static final Context applicationContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = getApplicationContext();
        initImageLoader(context);
    }


    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.taskExecutor(Executors.network());
        config.taskExecutorForCachedImages(Executors.io());
        LightJournalDiskCache diskCache = new LightJournalDiskCache(LightJournalDiskCache.getCacheDirectory(context), 50 * 1024 * 1024);
        config.diskCache(diskCache);
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
		config.memoryCache(new LruMemoryCache(10 * 1024 * 1024));
        ImageLoaderConfiguration configuration = config.build();
        ImageLoader.getInstance().init(configuration);
    }

}
