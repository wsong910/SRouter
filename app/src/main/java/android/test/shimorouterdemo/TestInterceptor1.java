package android.test.shimorouterdemo;
import android.content.Context;
import android.support.annotation.Nullable;

import chuxin.shimo.shimowendang.smrouter.interfaces.Interceptor;
import chuxin.shimo.shimowendang.smrouter.interfaces.RouteStatus;
import chuxin.shimo.shimowendang.smrouter.response.RouteResponse;
import io.reactivex.Observable;

public class TestInterceptor1 implements Interceptor {

    @Override
    public Observable<RouteResponse> intercept(@Nullable Context context,Chain chain) throws RuntimeException {
        System.out.println("TestInterceptor1.intercept");
        final RouteResponse response = chain.proceed(chain.getCurrentRequest());
        response.setStatus(RouteStatus.SUCCEED);
        return Observable.just(response);
    }
}
