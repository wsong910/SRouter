package chuxin.shimo.shimowendang.smrouter.core;
import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.app.Instrumentation.ActivityResult;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import chuxin.shimo.shimowendang.smrouter.exceptions.NotFoundRouteException;
import chuxin.shimo.shimowendang.smrouter.exceptions.NotInitException;
import chuxin.shimo.shimowendang.smrouter.interfaces.Interceptor;
import chuxin.shimo.shimowendang.smrouter.interfaces.RouteStatus;
import chuxin.shimo.shimowendang.smrouter.interfaces.RouterCallback;
import chuxin.shimo.shimowendang.smrouter.request.RouteRequest;
import chuxin.shimo.shimowendang.smrouter.response.RouteResponse;
import chuxin.shimo.shimowendang.smrouter.thread.Dispatcher;
import chuxin.shimo.shimowendang.smrouter.thread.RealCall;
import chuxin.shimo.shimowendang.smrouter.utils.ActivityLauncher;
import chuxin.shimo.shimowendang.smrouter.utils.Logger;
import chuxin.shimo.shimowendang.smrouter.utils.Util;
import io.reactivex.Observable;

import static chuxin.shimo.shimowendang.smrouter.core.Routers.KEY_RAW_URL;

public class RealRouters {
    private static final String TAG = "RealRouters";

    private static List<Mapping> mMappings = new ArrayList<>();
    private static Logger mLogger = new Logger();
    private static Handler mHandler;

    private static Application mApplicationContext;
    private static Dispatcher mDispatcher;
    private static ActivityLauncher mActivityLauncher;


    static void map(String format, Class<? extends Activity> activity, ExtraTypes extraTypes,
        List<Interceptor> interceptors) {
        mMappings.add(new Mapping(format, activity, extraTypes, interceptors));
    }

    RealRouters() {

    }

    public static boolean init(Application application) {
        mApplicationContext = application;
        mDispatcher = new Dispatcher();
        mHandler = new Handler(Looper.getMainLooper());
        RouterInit.init();
        sort();
        mMappings = Util.immutableList(mMappings);
        return true;
    }


    private static void sort() {
        //优先适配
        //user/collection is top over user/:userId
        //so scheme://user/collection will match user/collection not user/:userId
        Collections.sort(mMappings, new Comparator<Mapping>() {
            @Override
            public int compare(Mapping lhs, Mapping rhs) {
                return lhs.getPathSrc().compareTo(rhs.getPathSrc()) * -1;
            }
        });
    }

    static Context getApplicationContext() {
        return mApplicationContext;
    }

    static Mapping getMapByPath(Uri url) {
        Path path = Path.create(url);
        for (Mapping mapping : mMappings) {
            if (mapping.match(path)) {
                return mapping;
            }
        }
        return null;
    }

    public Postcard build(String url) {
        final Uri uri = Uri.parse(url);
        Path path = Path.create(uri);
        for (Mapping mapping : mMappings) {
            if (mapping.match(path)) {
                return new Postcard(url, mapping);
            }
        }
        return new Postcard(url, new Mapping(url, null, null));
    }

    boolean open(Context context, Uri uri, int requestCode, RouterCallback callback) {
        if (mMappings.isEmpty()) {
            if (callback != null) {
                final Postcard failPostcard = new Postcard(uri.getPath(), new Mapping(uri.getPath(), null, null));
                final RouteRequest failRequest = failPostcard.convertRouteRequest();
                failRequest.setRequestCode(requestCode);
                callback.onFailure(failRequest, new NotInitException("没有初始化 Router.init(...)"));
            }
            mLogger.error(TAG, "没有初始化 Router.init(...)");
            return false;
        }
        final Mapping mapByPath = getMapByPath(uri);
        if (mapByPath == null || mapByPath.getActivity() == null) {
            if (callback != null) {
                final Postcard failPostcard = new Postcard(uri.getPath(), new Mapping(uri.getPath(), null, null));
                final RouteRequest failRequest = failPostcard.convertRouteRequest();
                failRequest.setRequestCode(requestCode);
                callback.onFailure(failRequest, new NotInitException("uri 没有路由匹配"));
            }
            mLogger.error(TAG, "uri 没有路由匹配");
            return false;
        }
        final Postcard postcard = new Postcard(uri.getPath(), mapByPath);
        final RouteRequest request = postcard.convertRouteRequest();
        request.setRequestCode(requestCode);
        if (callback != null) {
            callback.onFound(request);
        }
        boolean success;
        try {
            success = doOpen(context, postcard, request, callback);
        } catch (Throwable e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onFailure(request, new NotInitException("路由失败，msg=" + e.getMessage()));
            }
            mLogger.error(TAG, "路由失败，msg=" + e.getMessage());
            return false;
        }

        if (callback != null) {
            if (success) {
                RouteResponse response = getResponse(requestCode, request);
                response.setStatus(RouteStatus.SUCCEED);
                response.setMessage("路由成功");
                callback.onResponse(response);
            } else {
                callback.onFailure(request, new NotFoundRouteException("路由" + uri.getPath() + "不存在"));
                mLogger.error(TAG, "路由" + uri.getPath() + "不存在");
            }
        }
        return success;
    }

    private boolean doOpen(final Context context, final Postcard postcard, final RouteRequest request,
        final RouterCallback callback) {
        final Bundle bundle = request.getData();
        final int flag = request.getFlag();
        final String pathUrl = request.getPath();
        final Context currentContext = null == context ? mApplicationContext : context;
        final Intent intent = new Intent(currentContext, postcard.getActivity());
        intent.putExtras(bundle);
        intent.putExtra(KEY_RAW_URL, pathUrl);
        if (flag > -1) {
            intent.addFlags(flag);
        }
        runInMainThread(new Runnable() {
            @Override
            public void run() {
                if (request.getRequestCode() >= 0) {  // Need start for result
                    if (currentContext instanceof Activity) {
                        ActivityCompat.startActivityForResult((Activity) currentContext, intent,
                            request.getRequestCode(), null);
                    } else {
                        mLogger.error(TAG, "Must use [navigation(activity, ...)] to support [startActivityForResult]");
                    }
                } else {
                    ActivityCompat.startActivity(currentContext, intent, null);
                }
                if (callback != null) {
                    callback.onArrival(request);
                }
            }
        });
        return true;
    }

    private static RouteResponse getResponse(int requestCode, RouteRequest request) {
        RouteResponse.Builder builder = new RouteResponse.Builder();
        builder.request(request).path(request.getPath()).requestCode(requestCode);
        return builder.build();
    }


    private static void runInMainThread(Runnable runnable) {
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            mHandler.post(runnable);
        } else {
            runnable.run();
        }
    }

    public List<Interceptor> interceptors(String url) {
        Path path = Path.create(Uri.parse(url));
        for (Mapping mapping : mMappings) {
            if (mapping.match(path)) {
                return mapping.interceptors();
            }
        }
        return null;
    }

    public Dispatcher dispatcher() {
        return mDispatcher;
    }

    ActivityLauncher getActivityLauncher() {
        if (mActivityLauncher == null) {
            mActivityLauncher = new ActivityLauncher();
        }
        return mActivityLauncher;
    }

    void _navigation(Context context, Postcard postcard, RouterCallback callback) {
        RouteRequest request;
        try {
            check(postcard);
            request = postcard.convertRouteRequest();
            if (callback != null) {
                callback.onFound(request);
            }
            new RealCall(context, this, request).enqueue(callback);
        } catch (RuntimeException e) {
            mLogger.error(TAG, e.getMessage());
            e.printStackTrace();
            if (callback != null) {
                if (postcard == null) {
                    callback.onFailure(null, e);
                } else {
                    request = postcard.convertRouteRequest();
                    callback.onFailure(request, e);
                }
            }
        }
    }


    RouteResponse _navigationSync(Context context, Postcard postcard, RouterCallback callback) {
        try {
            check(postcard);
        } catch (NotFoundRouteException e) {
            e.printStackTrace();
            return null;
        }
        RouteResponse response = null;
        final RouteRequest request = postcard.convertRouteRequest();
        try {
            if (callback != null) {
                callback.onFound(request);
            }
            response = new RealCall(context, this, request).execute(callback);
        } catch (RuntimeException e) {
            mLogger.error(TAG, e.getMessage());
            e.printStackTrace();
            if (callback != null) {
                callback.onFailure(request, e);
            }
        }
        return response;
    }

    Observable<ActivityResult> _navigationSyncByActivityLauncher(final Postcard postcard) {
        //检测目的路由是否存在
        Class<? extends Activity> activity = postcard.getActivity();
        if (activity == null) {
            return Observable.just(new ActivityResult(Activity.RESULT_CANCELED, null));
        }
        //代理跳转准备数据
        final Context applicationContext = Routers.getApplicationContext();

        final Intent intent = new Intent(applicationContext, RouterActivity.class);
        //最终的目标Activity
        final String name = activity.getName();
        intent.putExtra(RouterActivity.JUMP_ACTIVITY_PACKAGE_NAME, applicationContext.getPackageName());
        intent.putExtra(RouterActivity.JUMP_ACTIVITY_NAME, name);
        postcard.configIntent(intent);
        ((Application) applicationContext).registerActivityLifecycleCallbacks(
            new ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

                }

                @Override
                public void onActivityStarted(Activity activity) {

                }

                @Override
                public void onActivityResumed(Activity activity) {
                    if (postcard.getActivityResultObservable() == null && activity instanceof RouterActivity) {
                        postcard.setActivityResultObservable(((RouterActivity) activity).getObservableActivityResult());
                    }
                }

                @Override
                public void onActivityPaused(Activity activity) {

                }

                @Override
                public void onActivityStopped(Activity activity) {

                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

                }

                @Override
                public void onActivityDestroyed(Activity activity) {
                    if (activity instanceof RouterActivity) {
                        //释放
                        ((Application) applicationContext).unregisterActivityLifecycleCallbacks(this);
                        if (postcard.getActivityResultObservable() == null) {
                            postcard.setActivityResultObservable(Observable.just(
                                new ActivityResult(Activity.RESULT_CANCELED, null)));
                        }
                    }

                }
            });
        runInMainThread(new Runnable() {
            @Override
            public void run() {
                //跳转到代理Activity:RouterActivity
                applicationContext.startActivity(intent);
            }
        });
        while (postcard.getActivityResultObservable() == null) {
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return Observable.just(new ActivityResult(Activity.RESULT_CANCELED, null));
            }
        }
        return postcard.getActivityResultObservable();
    }

    private synchronized void check(Postcard postcard) {
        if (postcard == null) {
            mLogger.error(TAG, "No postcard!");
            throw new NotFoundRouteException(TAG + "No postcard!");
        }
        if (postcard.getActivity() == null) {
            mLogger.error(TAG, "There is no Activity match the path [" + postcard.getPathSrc() + "]");
            throw new NotFoundRouteException(
                TAG + "There is no Activity match the path [" + postcard.getPathSrc() + "]");
        }
    }

    /**
     *
     * @param context
     * @param response 有些数据Bundle可以直接放入response内，做传递参数
     */
    public void startActivity(@Nullable final Context context, @NonNull final RouteResponse response) {
        if (!response.getStatus().isSuccessful()) {
            return;
        }
        runInMainThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Context currentContext = context == null ? getApplicationContext() : context;
                    RouteRequest originalRequest = response.getOriginalRequest();
                    final String path = originalRequest.getPath();
                    if (TextUtils.isEmpty(path)) {
                        throw new RuntimeException("Path is null in Router when startActivity");
                    }
                    final Mapping mapByPath = Routers.getMapByPath(Uri.parse(path));
                    if (mapByPath == null) {
                        throw new RuntimeException("it is not in RouterMapping(" + path + ")");
                    }
                    Class<? extends Activity> activity = mapByPath.getActivity();
                    if (activity == null) {
                        throw new RuntimeException("Activity not register in Router(" + path + ")");
                    }
                    final Intent intent = new Intent(currentContext, activity);
                    intent.putExtras(originalRequest.getData());
                    intent.putExtra(KEY_RAW_URL, originalRequest.getPath());
                    int flags = originalRequest.getFlag();
                    if (-1 != flags) {
                        intent.setFlags(flags);
                    }
                    int requestCode = originalRequest.getRequestCode();
                    if (requestCode >= 0) {  // Need start for result
                        if (currentContext instanceof Activity) {
                            ActivityCompat.startActivityForResult((Activity) currentContext, intent, requestCode,
                                originalRequest.getOptionsCompat());
                        } else {
                            mLogger.warning(TAG,
                                "Must use [navigation(activity, ...)] to support [startActivityForResult]");
                        }
                    } else {
                        ActivityCompat.startActivity(currentContext, intent, originalRequest.getOptionsCompat());
                    }
                    if (context instanceof Activity) {
                        if ((-1 != originalRequest.getEnterAnim() && -1 != originalRequest.getExitAnim())) {
                            ((Activity) currentContext).overridePendingTransition(originalRequest.getEnterAnim(),
                                originalRequest.getExitAnim());
                        }
                    }
                } catch (RuntimeException e) {
                    mLogger.error(TAG, e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    void showLog(boolean flag) {
        mLogger.showLog(flag);
    }
}
