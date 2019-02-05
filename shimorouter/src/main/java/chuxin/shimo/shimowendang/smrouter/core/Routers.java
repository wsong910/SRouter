package chuxin.shimo.shimowendang.smrouter.core;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation.ActivityResult;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.List;

import chuxin.shimo.shimowendang.smrouter.interfaces.Interceptor;
import chuxin.shimo.shimowendang.smrouter.interfaces.RouterCallback;
import chuxin.shimo.shimowendang.smrouter.response.RouteResponse;
import chuxin.shimo.shimowendang.smrouter.utils.ActivityLauncher;
import io.reactivex.Observable;

public class Routers {
    public static String KEY_RAW_URL = "chuxin.shimo.shimowendang.smrouter.KeyRawUrl";
    private static Routers mInstance;
    private static boolean mHasInit;
    private RealRouters mRealRouters;


    private Routers() {
        mRealRouters = new RealRouters();
    }

    //编译时赋值
    static void map(String format, Class<? extends Activity> activity, ExtraTypes extraTypes,
        List<Interceptor> interceptors) {
        RealRouters.map(format, activity, extraTypes, interceptors);
    }

    public static void init(Application application) {
        if (mHasInit) {
            return;
        }
        mHasInit = RealRouters.init(application);
    }

    public static Routers getInstance() {
        if (!mHasInit) {
            throw new IllegalArgumentException("start Routers init(context) first!");
        }
        if (mInstance == null) {
            synchronized (Routers.class) {
                if (mInstance == null) {
                    mInstance = new Routers();
                }
            }
        }
        return mInstance;
    }

    public static void showLog(boolean flag) {
        mInstance.mRealRouters.showLog(flag);
    }

    public Postcard build(String url) {
        return mRealRouters.build(url);
    }

    //<editor-fold desc="直接使用路由的方法集,忽略拦截器">

    public boolean open(Context context, String url) {
        return open(context, Uri.parse(url));
    }

    public boolean open(Context context, String url, RouterCallback callback) {
        return open(context, Uri.parse(url), callback);
    }

    public boolean open(Context context, Uri uri) {
        return open(context, uri, null);
    }

    public boolean open(Context context, Uri uri, RouterCallback callback) {
        return open(context, uri, -1, callback);
    }

    public boolean openForResult(Activity activity, String url, int requestCode) {
        return openForResult(activity, Uri.parse(url), requestCode);
    }

    public boolean openForResult(Activity activity, String url, int requestCode, RouterCallback callback) {
        return openForResult(activity, Uri.parse(url), requestCode, callback);
    }

    public boolean openForResult(Activity activity, Uri uri, int requestCode) {
        return openForResult(activity, uri, requestCode, null);
    }

    public boolean openForResult(Activity activity, Uri uri, int requestCode, RouterCallback callback) {
        return open(activity, uri, requestCode, callback);
    }

    private boolean open(Context context, Uri uri, int requestCode, RouterCallback callback) {
        return mRealRouters.open(context, uri, requestCode, callback);
    }
    //</editor-fold>

    @Nullable
    static Mapping getMapByPath(Uri url) {
        return RealRouters.getMapByPath(url);
    }

    static Context getApplicationContext() {
        return RealRouters.getApplicationContext();
    }

    void navigation(Context context, Postcard postcard, RouterCallback callback) {
        mRealRouters._navigation(context, postcard, callback);
    }


    RouteResponse navigationSync(Context context, Postcard postcard, RouterCallback callback) {
        return mRealRouters._navigationSync(context, postcard, callback);
    }

    Observable<ActivityResult> navigationSyncByActvityLauncher(Bundle data, Postcard postcard) {
        if (data != null) {
            postcard.with(data);
        }
        return mRealRouters._navigationSyncByActivityLauncher(postcard);
    }

    ActivityLauncher getActivityLauncher() {
        return mRealRouters.getActivityLauncher();
    }
}
