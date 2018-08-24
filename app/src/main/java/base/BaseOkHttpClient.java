package base;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;

public class BaseOkHttpClient {
    private static final String TAG = BaseOkHttpClient.class.getSimpleName();

    private static OkHttpClient instance;

    public static OkHttpClient getInstance(Context context) {
        if (instance == null) {
            //Log.e(TAG, "instance == null");
            instance = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .cache(null)
                    .build();
        }

        return instance;
    }

    public static void cancelRequest(String tag) {
        if (instance != null) {
            for (Call call : instance.dispatcher().queuedCalls()) {
                if (call.request().tag().equals(tag)) {
                    call.cancel();
                    break;
                }
            }
//            for (Call call : instance.dispatcher().runningCalls()) {
//                if (call.request().tag().equals(tag)) {
//                    call.cancel();
//                    break;
//                }
//            }
        }
    }
}