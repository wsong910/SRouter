package android.test.shimorouterdemo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import chuxin.shimo.shimowendang.smrouter.core.RouterResponseNext;
import chuxin.shimo.shimowendang.smrouter.interfaces.Interceptor;
import chuxin.shimo.shimowendang.smrouter.interfaces.RouteStatus;
import chuxin.shimo.shimowendang.smrouter.request.RouteRequest;
import chuxin.shimo.shimowendang.smrouter.response.RouteResponse;
import io.reactivex.Observable;

public class TestInterceptor3 implements Interceptor {

    @Override
    public Observable<RouteResponse> intercept(@Nullable Context context, Chain chain) throws RuntimeException {
        System.out.println("TestInterceptor3.intercept");
        //如果没有注册:
        return new RouterResponseNext(RouterTable.TEST1, null, chain) {
            @Override
            public RouteResponse onFailed(RouteResponse currentResponse) {
                currentResponse.setStatus(RouteStatus.FAILED);
                return currentResponse;
            }

            @Override
            public RouteResponse onSuccess(@Nullable Intent data, Chain chain, RouteResponse currentResponse,
                RouteRequest request) {
                if (data != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("test",data.getStringExtra("test"));
                    currentResponse.addData(bundle);
                }
//                data.getExtras();
                return chain.proceed(request);
            }
        }.cover();
        //如果已经注册:
        //方式一：
        /*return new RouterResponseNext(chain) {
            @Override
            public RouteResponse onFailed(RouteResponse currentResponse) {
                return null;
            }

            @Override
            public RouteResponse onSuccess(Intent data, Chain chain, RouteResponse currentResponse,
                RouteRequest request) {
                // data = null;
                currentResponse.addData(new Bundle());
                final RouteResponse response = chain.proceed(request);
                response.setStatus(RouteStatus.SUCCEED);
                return response;
            }
        }.cover();*/

        //方式二：
        /*
        currentResponse.addData(new Bundle());
        final RouteResponse response = chain.proceed(chain.getCurrentRequest());
        response.setStatus(RouteStatus.SUCCEED);
        return Observable.just(response);*/
    }
}
