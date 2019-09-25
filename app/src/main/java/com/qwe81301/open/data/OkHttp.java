package com.qwe81301.open.data;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * author:       bearshih
 * project:      OpenData
 * date:         2019/9/20
 * version:
 * description:
 */
public class OkHttp {

    private String TAG = getClass().getSimpleName();
    private final OkHttpClient mOkHttpClient;
    private static ConcurrentHashMap<String, List<Cookie>> cookieStore = new ConcurrentHashMap<>();

    private Context mContext;
    private Activity mActivity;

    public OkHttp(Context context, Activity activity) {

        //這範例暫時還沒用到context 和 activity
        mContext = context;
        mActivity = activity;

        mOkHttpClient = new OkHttpClient.Builder()
                //新增 EX: OkHttp: --> POST Log
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))//todo 保留一下當初舊式的打印方法(這邊先註解就好)
                .connectTimeout(15, TimeUnit.SECONDS)//todo 正式上線 三個設定參數要改回 網友建議值 15秒　(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                // TODO 拿掉authenticator 看看

                .cookieJar(new CookieJar() {//这里可以做cookie传递，保存等操作

                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {//可以做保存cookies操作
                        cookieStore.put(url.host(), cookies);
                        List<Cookie> cookie = cookieStore.get(url.host());
                        Log.v(TAG, "cookieStore.get(url.host()): " + cookie);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {//加载新的cookies
                        List<Cookie> cookies = cookieStore.get(url.host());
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                })
                .build();
    }

    // todo 看有沒有機會優化 可擴充性  (String url , String ... raws , String ...  dataTransferToServerJsons) {}
    public String get(String url) {

        Request request = new Request.Builder()
                .url(url)
                .build();

        String resStr = "";
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            //todo  或是改寫成最下面call back的寫法
            resStr = response.body().string();
            Log.v(TAG, "response success");
        } catch (IOException e) {
            e.printStackTrace();

            //紅米:無網路狀況
            //System.err: java.net.ConnectException: Failed to connect to /192.168.20.16:8001
            //System.err: Caused by: java.net.ConnectException: Network is unreachable

            //三星:無網路狀況
            //System.err: java.net.ConnectException: Failed to connect to /192.168.20.16:8001
            //System.err: Caused by: java.net.ConnectException: Network is unreachable

            //紅米:只開行動網路狀況 因測試環境只開行動網路連不上伺服器
            //System.err: java.net.SocketTimeoutException: connect timed out

            //為了捕捉不在預期的錯誤 而新增的提示訊息
            //(下面執行完沒蓋過 resStr 就是預設的Unpredictable error[不在預期的錯誤])
            resStr = "Unpredictable error";

            if (e instanceof ConnectException ||
                    e instanceof SocketException) {
                //網路不穩(關閉wifi 後 右打開 wifi 並迅速點擊打卡按鈕 那連線的瞬間就跟沒有網路一樣)
                //會有兩種況狀 1.Network is unreachable 或是 2.Software caused connection abort
                Log.v(TAG, "無網路狀況 or 網路不穩(關閉wifi 後 右打開 wifi 並迅速點擊打卡按鈕)");
                resStr = "Network is unstable";
            } else if (e instanceof SocketTimeoutException) {
                Log.v(TAG, "只開行動網路狀況 因測試環境只開行動網路連不上伺服器");
                resStr = "Connect timed out";
            }
        }

        Log.v(TAG, "resStr: " + resStr);
        return resStr;
    }

}
