package chuxin.shimo.shimowendang.smrouter.thread;
import android.content.Context;
import android.net.Uri;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import chuxin.shimo.shimowendang.smrouter.core.Path;
import chuxin.shimo.shimowendang.smrouter.core.RealInterceptorChain;
import chuxin.shimo.shimowendang.smrouter.core.RealRouters;
import chuxin.shimo.shimowendang.smrouter.exceptions.NotFoundRouteException;
import chuxin.shimo.shimowendang.smrouter.exceptions.RouteException;
import chuxin.shimo.shimowendang.smrouter.interfaces.Call;
import chuxin.shimo.shimowendang.smrouter.interfaces.Interceptor;
import chuxin.shimo.shimowendang.smrouter.interfaces.RouteStatus;
import chuxin.shimo.shimowendang.smrouter.interfaces.RouterCallback;
import chuxin.shimo.shimowendang.smrouter.request.RouteRequest;
import chuxin.shimo.shimowendang.smrouter.response.RouteResponse;

public class RealCall implements Call {
    private static final String TAG = "RealCall";
    private WeakReference<Context> mReference;
    final RealRouters mRouters;
    final RouteRequest mOriginalRequest;
    // Guarded by this.
    private boolean executed;

    public RealCall(Context context, RealRouters routers, RouteRequest routeRequest) {
        mReference = new WeakReference<>(context);
        mRouters = routers;
        mOriginalRequest = routeRequest;
    }

    @Override
    public RouteRequest request() {
        return mOriginalRequest;
    }

    @Override
    public RouteResponse execute(RouterCallback callback) throws RuntimeException {
        synchronized (this) {
            if (executed) {
                throw new RouteException("Already Executed");
            }
            executed = true;
        }
        try {
            mRouters.dispatcher().executed(this);
            RouteResponse response = getResponseWithInterceptorChain(null);
            if (response == null) {
                final RouteException canceled = new RouteException("Canceled,execute RouteResponse = null");
                canceled.printStackTrace();
                if (callback != null) {
                    callback.onFailure(mOriginalRequest, canceled);
                }
                throw canceled;
            }
            mRouters.startActivity(mReference.get(), response);
            if (callback != null) {
                callback.onArrival(mOriginalRequest);
            }
            return response;
        } finally {
            mRouters.dispatcher().finished(this);
        }
    }

    @Override
    public void enqueue(RouterCallback responseCallback) throws RuntimeException {
        synchronized (this) {
            if (executed) {
                throw new RouteException("Already Executed");
            }
            executed = true;
        }
        final AsyncCall asyncCall = new AsyncCall(responseCallback);
        mRouters.dispatcher().enqueue(asyncCall);
    }

    @Override
    public void cancel() {

    }

    @Override
    public boolean isExecuted() {
        return false;
    }

    @Override
    public boolean isCanceled() {
        return false;
    }

    public final class AsyncCall extends NamedRunnable {
        private final RouterCallback responseCallback;

        AsyncCall(RouterCallback responseCallback) {
            super("Router %s", mOriginalRequest.getPath());
            this.responseCallback = responseCallback;
        }

        public RouteRequest request() {
            return mOriginalRequest;
        }

        public RealCall get() {
            return RealCall.this;
        }

        public Path url() {
            if (mOriginalRequest == null) {
                return Path.create(Uri.parse(""));
            }
            return Path.create(Uri.parse(mOriginalRequest.getPath()));
        }

        @Override
        protected void execute() {
            boolean signalledCallback = false;
            try {
                RouteResponse response = getResponseWithInterceptorChain(responseCallback);
                signalledCallback = true;
                if (response.isRedirect()) {
                    mRouters.build(response.redirectUrl()).with(response.getDatas()).navigation(responseCallback);
                } else {
                    final RouteStatus status = response.getStatus();
                    if (status.isSuccessful()) {

                        mRouters.startActivity(mReference.get(), response);
                        if (responseCallback != null) {
                            responseCallback.onArrival(mOriginalRequest);
                            responseCallback.onResponse(response);
                        }
                    } else {
                        final RouteException cancel = new RouteException("Cancel");
                        if (responseCallback != null) {
                            responseCallback.onFailure(mOriginalRequest, cancel);
                        }
                        cancel.printStackTrace();
                    }
                }
            } catch (NotFoundRouteException e) {
                if (signalledCallback) {
                    // Do not signal the callback twice!
                } else {
                    if (responseCallback != null) {
                        responseCallback.onFailure(mOriginalRequest, e);
                    }
                }
                e.printStackTrace();
            } finally {
                mRouters.dispatcher().finished(this);
            }
        }


    }

    private RouteResponse getResponseWithInterceptorChain(
        RouterCallback responseCallback) throws NotFoundRouteException {
        // Build a full stack of interceptors.
        List<Interceptor> interceptors = new ArrayList<>();
        final List<Interceptor> interceptorList = mRouters.interceptors(mOriginalRequest.getPath());
        if (interceptorList != null && !interceptorList.isEmpty()) {
            interceptors.addAll(interceptorList);
        }
        Interceptor.Chain chain = new RealInterceptorChain(mReference.get(), interceptors, 0, mOriginalRequest,
            responseCallback);
        return chain.proceed(mOriginalRequest);
    }

}
