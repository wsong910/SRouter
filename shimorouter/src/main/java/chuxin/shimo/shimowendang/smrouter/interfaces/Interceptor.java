package chuxin.shimo.shimowendang.smrouter.interfaces;

import android.content.Context;
import android.support.annotation.Nullable;

import chuxin.shimo.shimowendang.smrouter.request.RouteRequest;
import chuxin.shimo.shimowendang.smrouter.response.RouteResponse;
import io.reactivex.Observable;

/**
 * Interceptor before route.
 */
public interface Interceptor {
    /**
     * 拦截核心逻辑，其中chain可以拿到request
     * 处理完之后 需要对response进行值的改写{@link RouteStatus}
     *
     * @param chain
     * @return
     * @throws RuntimeException
     */
    Observable<RouteResponse> intercept(@Nullable Context context, Chain chain) throws RuntimeException;

    /**
     * Interceptor chain processor.
     */
    interface Chain {
        RouteRequest getCurrentRequest();

        /**
         * 本次正在处理的response
         * @return
         */
        RouteResponse getCurrentResponse();

        Context getContext();

        /**
         * Continue to process this route request.
         */
        RouteResponse proceed(RouteRequest request) throws RuntimeException;
    }
}
