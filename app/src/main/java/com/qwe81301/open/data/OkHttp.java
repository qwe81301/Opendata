package com.qwe81301.open.data;

import android.app.Activity;
import android.content.Context;
import android.net.Credentials;
import android.util.Log;

import java.io.IOException;
import java.net.Authenticator;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.internal.platform.Platform;
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

        mContext = context;
        mActivity = activity;
//        singletonProgressDialog = SingletonProgressDialog.getInstance();
//
//        ToolsHelper.initialToolsHelperContext(context);

//        client.addInterceptor(new LoggingInterceptor.Builder()
//                .loggable(BuildConfig.DEBUG)
//                .setLevel(Level.BASIC)
//                .log(Platform.INFO)
//                .request("Request")
//                .response("Response")
//                .addHeader("version", BuildConfig.VERSION_NAME)
//                .addQueryParam("query", "0")
//                .enableMock(true, 1000L, request -> {
//                    String segment = request.url().pathSegments().get(0);
//                    return Okio.buffer(Okio.source(mAssetManager.open(String.format("mock/%s.json", segment)))).readUtf8();
//                })
////              .enableAndroidStudio_v3_LogsHack(true) /* enable fix for logCat logging issues with pretty format */
////              .logger(new Logger() {
////                  @Override
////                  public void log(int level, String tag, String msg) {
////                      Log.w(tag, msg);
////                  }
////              })
////              .executor(Executors.newSingleThreadExecutor())
//                .build());

        mOkHttpClient = new OkHttpClient.Builder()
                //新增 EX: OkHttp: --> POST Log
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))//todo 保留一下當初舊式的打印方法(這邊先註解就好)
//                .addInterceptor(new LoggingInterceptor.Builder()
//                        .loggable(BuildConfig.DEBUG)
//                        .setLevel(Level.BASIC)
//                        .log(Platform.INFO)
//                        .request("Request")
//                        .response("Response")
//                        .addHeader("version", BuildConfig.VERSION_NAME)
//                        .addQueryParam("query", "0")
//                        .build())
//                .connectTimeout(5, TimeUnit.MINUTES)//todo 測試用調成 5 分鐘
//                .readTimeout(5, TimeUnit.MINUTES)
//                .writeTimeout(5, TimeUnit.MINUTES)
                .connectTimeout(15, TimeUnit.SECONDS)//todo 正式上線 三個設定參數要改回 網友建議值 15秒　(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                // TODO 拿掉authenticator 看看
//                .authenticator(new Authenticator() {
//                    @Override
//                    public Request authenticate(Route route, Response response) throws IOException {//401，认证
//                        String credential = Credentials.basic("qwe81301", "password1");
//                        return response.request().newBuilder().header("Authorization", credential).build();
//                    }
//                })
//                .cookieJar(new CookieJar()
//                {//这里可以做cookie传递，保存等操作
//                    @Override
//                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies)
//                    {//可以做保存cookies操作
//                        System.out.println("cookies url: " + url.toString());
//                        for (Cookie cookie : cookies)
//                        {
//                            System.out.println("cookies: " + cookie.toString());
//                        }
//                    }
//
//                    @Override
//                    public List<Cookie> loadForRequest(HttpUrl url)
//                    {//加载新的cookies
//                        System.out.println("host: " + url.host());
//                        ArrayList<Cookie> cookies = new ArrayList<>();
//                        Cookie cookie = new Cookie.Builder()
//                                .hostOnlyDomain(url.host())
//                                .name("SESSION").value("zyao89")
//                                .build();
//                        cookies.add(cookie);
//                        return cookies;
//                    }
//                })
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
        FormBody formBody = new FormBody.Builder()
//                .add(raw1, dataTransferToServerJson1)
//                .add(raw2, dataTransferToServerJson2)
                .build();

        Request request = new Request.Builder()
                .url(url)
//                .header("Accept", "application/json; q=0.5")//添加请求头，方式一
                //addHeader 放進cookie
//                .addHeader("Accept", "*")//添加请求头，方式二
//                .put(formBody)
//                .get(formBody)
                .build();

//        //開啟等待讀取提示圖
//        ToolsHelper.handlerSendMessage(ToolsHelper.SHOW_LOADING_PROGRESS);

//        singletonProgressDialog.show(mContext, mActivity);

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

//        Call call = mOkHttpClient.newCall(request);
////        return call.enqueue(getCallback());

        //關閉等待讀取提示圖
//        ToolsHelper.handlerSendMessage(ToolsHelper.DISMISS_LOADING_PROGRESS);

//        singletonProgressDialog.dismiss(mActivity);

        Log.v(TAG, "resStr: " + resStr);
        return resStr;
    }

}
