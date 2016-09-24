package xyz.garywzh.loveleak.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import xyz.garywzh.loveleak.util.LogUtils;

/**
 * Created by garywzh on 2016/9/12.
 */
public class NetworkHelper {
    public static String TAG = NetworkHelper.class.getSimpleName();
    public static String BASE_URL = "http://www.liveleak.com/";
    public static int VIDEO_ONCE_LOAD_COUNT = 12;
    public static int COMMENT_ONCE_LOAD_COUNT = 25;

    public static LiveLeakApi liveLeakApi = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(new OkHttpClient.Builder()
                    .addInterceptor(new HttpLoggingInterceptor(new SimpleLogger())
                            .setLevel(HttpLoggingInterceptor.Level.BASIC))
                    .build())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LiveLeakApi.class);

    public static LiveLeakApi getApi() {
        return liveLeakApi;
    }

    static class SimpleLogger implements HttpLoggingInterceptor.Logger {
        @Override
        public void log(String message) {
            LogUtils.d(TAG, message);
        }
    }
}
