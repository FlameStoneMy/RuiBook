package com.ruitech.bookstudy.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ruitech.bookstudy.App;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

public class NetworkUtil {


    private static final Object normalLock = new Object();
    private static volatile OkHttpClient normalClient;

    public static OkHttpClient getClient2() {
        OkHttpClient client = null;
        try {
            TLSSocketFactory tlsSocketFactory = new TLSSocketFactory();//new TLSSocketFactory();
//            if (tlsSocketFactory.getTrustManager()!=null) {
                client = new OkHttpClient.Builder()
                        .sslSocketFactory(tlsSocketFactory, tlsSocketFactory.getTrustManager())
                        .build();
//            }
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return client;
    }

    public static OkHttpClient getUnsafeOkHttpClient() {
        try {

            final TrustManager[] trustAllCerts = new TrustManager[]{

                    new X509TrustManager() {

                        @Override

                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {

                        }

                        @Override

                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {

                        }

                        @Override

                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {

                            return new java.security.cert.X509Certificate[]{};

                        }

                    }

            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");

            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            final javax.net.ssl.SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();

            builder.sslSocketFactory(sslSocketFactory);

            builder.hostnameVerifier(new HostnameVerifier() {

                @Override

                public boolean verify(String hostname, SSLSession session) {

                    return true;

                }

            });

            return builder.build();

        } catch (Exception e) {

            throw new RuntimeException(e);

        }


    }

    public static synchronized OkHttpClient getNormalClient() {
//        return getClient2();
//        return getUnsafeOkHttpClient();
        
        if (normalClient == null) {
            synchronized (normalLock) {
                if (normalClient == null) {
                    OkHttpClient baseClient = getClient();
//                    Dns dns = DnsTest.strategy().getDns();
                    normalClient = baseClient.newBuilder().addInterceptor(new ResponseInterceptor()).build();
                }
            }
        }
        return normalClient;

    }
//    public static OkHttpClient getClient() {
//        ExecutorService threadPoolExecutor = Executors.network();
//        Dispatcher dispatcher = new Dispatcher(threadPoolExecutor);
//        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(15 * 1000, TimeUnit.MILLISECONDS)
//                .readTimeout(30 * 1000, TimeUnit.MILLISECONDS).dispatcher(dispatcher)
//                .followRedirects(true).build();
//        return client;
//    }


    private static final Object baseLLock = new Object();
    private static volatile OkHttpClient baseClient;
    public static OkHttpClient getClient() {

        if (baseClient == null) {
            synchronized (baseLLock) {
                if (baseClient == null) {
                    ExecutorService threadPoolExecutor = Executors.network();

                    Dispatcher dispatcher = new Dispatcher(threadPoolExecutor);
                    OkHttpClient client = new OkHttpClient.Builder().connectTimeout(15 * 1000, TimeUnit.MILLISECONDS)
                            .readTimeout(30 * 1000, TimeUnit.MILLISECONDS).dispatcher(dispatcher)
                            .followRedirects(true).build();

                    baseClient = client;
                }
            }
        }

        return baseClient;
    }

    public static boolean isNetWorkConnected() {
        try {
            ConnectivityManager cm = (ConnectivityManager) App.applicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        } catch (Exception ignored) {
        }
        return false;
    }
}
