package chuxin.shimo.shimowendang.smrouter.core;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import chuxin.shimo.shimowendang.smrouter.interfaces.Interceptor;
import chuxin.shimo.shimowendang.smrouter.interfaces.RouteStatus;
import chuxin.shimo.shimowendang.smrouter.interfaces.RouterCallback;
import chuxin.shimo.shimowendang.smrouter.request.RouteRequest;
import chuxin.shimo.shimowendang.smrouter.response.RouteResponse;
import io.reactivex.functions.Consumer;

public final class RealInterceptorChain implements Interceptor.Chain {
    private final Context mContext;
    RouteRequest mRouteRequest;
    private final List<Interceptor> interceptors;
    private int index;
    private final RouterCallback mRouterCallback;
    private int calls;
    private RouteResponse mResponse;

    public RealInterceptorChain(@Nullable Context context, List<Interceptor> interceptors, int index,
        @NonNull RouteRequest routeRequest, RouterCallback routerCallback) {
        mContext = context;
        mRouteRequest = routeRequest;
        this.interceptors = interceptors;
        this.index = index;
        mRouterCallback = routerCallback;
        RouteResponse.Builder builder = new RouteResponse.Builder();
        builder.request(mRouteRequest).path(mRouteRequest.getPath()).requestCode(mRouteRequest.getRequestCode());
        mResponse = builder.build();
    }

    @Nullable
    @Override
    public Context getContext() {
        return mContext;
    }

    @NonNull
    @Override
    public RouteRequest getCurrentRequest() {
        return mRouteRequest;
    }

    @Override
    public RouteResponse getCurrentResponse() {
        return mResponse;
    }

    @NonNull
    @Override
    public RouteResponse proceed(RouteRequest request) throws RuntimeException {
        if (index >= interceptors.size()) {
            mResponse.setStatus(RouteStatus.SUCCEED);
            return mResponse;
        }
        calls++;
        // Call the next interceptor in the chain.
        RealInterceptorChain next;
        Interceptor interceptor = interceptors.get(index);
        if (index + 1 >= interceptors.size()) {
            next = this;
            index += 1;
        } else {
            next = new RealInterceptorChain(mContext, interceptors, index + 1, request, mRouterCallback);
        }
        RouteResponse temp = interceptor.intercept(mContext, next)
            .doOnError(new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    mResponse.setMessage(throwable.getMessage());
                    mResponse.setStatus(RouteStatus.FAILED);
                    if (mRouterCallback != null) {
                        mRouterCallback.onFailure(mRouteRequest, new RuntimeException(throwable));
                    }
                }
            })
            .blockingFirst();
        if (temp == null || !temp.getStatus().isSuccessful()) {
            interceptors.clear();
            mResponse.setStatus(RouteStatus.FAILED);
            mResponse.setMessage(interceptor.getClass().getName().concat(" interceptor and return failed"));
            return mResponse;
        }
        mResponse = temp;
        if (mResponse.isRedirect()) {
            interceptors.clear();
            if (mRouterCallback != null) {
                mRouterCallback.onRedirect(mResponse.redirectUrl());
            }
            return mResponse;
        }
        // Confirm that the next interceptor made its required call to chain.proceed().
        if (index + 1 < interceptors.size() && next.calls != 1) {
            throw new IllegalStateException("interceptor " + interceptor
                + " must call proceed() exactly once");
        }
        return mResponse;
    }

/*    private RouteResponse process(Interceptor interceptor, RealInterceptorChain next) {
        interceptor.configWithContext(mContext);
        RouteStatus status = interceptor.intercept(next);
        if (status.isPass()) {
            mResponse.setStatus(RouteStatus.SUCCEED);
        } else if (status.isInterrupt()) {
            mResponse.setStatus(RouteStatus.INTERCEPTED);
        } else if (status.isBlock()) {
            while (!mIdleBlock) {
                try {
                    Thread.sleep(30);
                    System.out.println("RealInterceptorChain.process");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    mResponse.setStatus(RouteStatus.INTERCEPTED);
                }
            }
        } else {
            throw new RouteException("interceptor:" + interceptor.getClass().getSimpleName() +
                ".intercept() can only return INTERCEPTOR_XX status");
        }
        return mResponse;
    }*/

}
