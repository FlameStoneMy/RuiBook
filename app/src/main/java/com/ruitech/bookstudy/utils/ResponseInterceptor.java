package com.ruitech.bookstudy.utils;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.greenrobot.eventbus.EventBus;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class ResponseInterceptor implements Interceptor {
    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request  = chain.request();
        Response response = null;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
//        }catch (SocketTimeoutException socket){
//            throw socket;
//        }catch (ConnectException connect){
//            throw connect;
//        }catch (InterruptedIOException interruptIO){
//            throw interruptIO;
//        }catch (UnknownHostException unknownHost){
//            throw unknownHost;
//        } catch (IOException ioException){
//            throw ioException;
//        }
        return response;
    }
}