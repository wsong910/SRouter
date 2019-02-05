package chuxin.shimo.shimowendang.smrouter.core;
import android.app.Activity;
import android.app.Instrumentation.ActivityResult;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import chuxin.shimo.shimowendang.smrouter.interfaces.Interceptor.Chain;
import chuxin.shimo.shimowendang.smrouter.interfaces.RouteStatus;
import chuxin.shimo.shimowendang.smrouter.request.RouteRequest;
import chuxin.shimo.shimowendang.smrouter.response.RouteResponse;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

public abstract class RouterResponseNext {
    private final Observable<RouteResponse> mResponseObservable;

    public RouterResponseNext(@NonNull Chain chain) {
        this(null, null, chain);
    }

    /**
     *
     * @param path 路由
     * @param data  传递数据
     * @param chain 拦截链
     */
    public RouterResponseNext(@Nullable String path, Bundle data, final @NonNull Chain chain) {
        final RouteResponse currentResponse = chain.getCurrentResponse();
        if (!TextUtils.isEmpty(path)) {
            final Observable<ActivityResult> resultObservable = Routers.getInstance()
                .build(path)
                .navigationSyncByActvityLauncher(data);
            mResponseObservable = resultObservable.map(new Function<ActivityResult, RouteResponse>() {
                @Override
                public RouteResponse apply(ActivityResult activityResult) throws Exception {
                    if (activityResult.getResultCode() == Activity.RESULT_OK) {
                        currentResponse.setStatus(RouteStatus.SUCCEED);
                        return RouterResponseNext.this.onSuccess(activityResult.getResultData(), chain, currentResponse,
                            chain.getCurrentRequest());
                    } else {
                        currentResponse.setStatus(RouteStatus.FAILED);
                        return RouterResponseNext.this.onFailed(currentResponse);
                    }
                }
            });
        } else {
            mResponseObservable = Observable.just(
                onSuccess(null, chain, currentResponse, chain.getCurrentRequest()));
        }
    }

    public abstract RouteResponse onFailed(RouteResponse currentResponse);

    public abstract RouteResponse onSuccess(@Nullable Intent data, Chain chain, RouteResponse currentResponse,
        RouteRequest routeRequest);

    public Observable<RouteResponse> cover() {
        return mResponseObservable;
    }
}
