package com.ruitech.bookstudy.upgrade;

import android.os.AsyncTask;
import android.util.Log;

import com.ruitech.bookstudy.utils.APIUtil;
import com.ruitech.bookstudy.utils.UrlInvalidException;
import com.ruitech.bookstudy.utils.ZipUtil;

import java.io.File;
import java.io.IOException;

import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;

public class UpgradeTask extends AsyncTask<Object, Object, File> {
    private static final String TAG = "UpgradeTask";
    private UpgradeBean upgradeBean;
    private Callback callback;
    public UpgradeTask(UpgradeBean upgradeBean, Callback callback) {
        this.upgradeBean = upgradeBean;
        this.callback = callback;
    }

    @Override
    protected File doInBackground(Object... objects) {
        Log.d(TAG, "doInBackground: ");
        File apkFile = null;

        while (true) {
            File upgradeDir = UpgradeUtil.getUpgradeDir(upgradeBean.versionCode);
            if (!upgradeDir.exists()) {
                if (!upgradeDir.mkdirs()) {
                    break;
                }
            }

            Response response = null;
            try {
                response = APIUtil.getResponse(upgradeBean.url);
                if (response.code() != 200) {
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (UrlInvalidException e) {
                e.printStackTrace();
            }


            BufferedSink bufferedSink = null;
            File zipFile = UpgradeUtil.getUpgradeZipFile(upgradeBean.versionCode);
            try {
                Sink sink = Okio.sink(zipFile);
                bufferedSink = Okio.buffer(sink);
                bufferedSink.writeAll(response.body().source());

                bufferedSink.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bufferedSink != null) {
                    try {
                        bufferedSink.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                ZipUtil.unzip(zipFile, upgradeDir, false);
                zipFile.delete();

                apkFile = UpgradeUtil.getUpgradeApkFile(upgradeBean.versionCode);
            } catch (IOException e) {
                e.printStackTrace();
            }
            break;
        }

        return apkFile;
    }

    @Override
    protected void onPostExecute(File apkFile) {
        Log.d(TAG, "onPostExecute: " + apkFile);
        callback.onNewVersionFile(apkFile);
    }

    public interface Callback {
        void onUpgradeProgress(int progress);
        void onNewVersionFile(File apkFile);
    }
}
