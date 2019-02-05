package android.test.shimorouterdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import chuxin.shimo.shimowendang.smrouter.annotation.Router;
import chuxin.shimo.shimowendang.smrouter.core.Routers;
import chuxin.shimo.shimowendang.smrouter.interfaces.RouterCallback;
import chuxin.shimo.shimowendang.smrouter.request.RouteRequest;
import chuxin.shimo.shimowendang.smrouter.response.RouteResponse;


@Router(RouterTable.MAIN)
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView txt = findViewById(R.id.txt);
        final TextView txt2 = findViewById(R.id.txt2);
        final TextView txt3 = findViewById(R.id.txt3);
        txt.setOnClickListener(v -> Routers.getInstance().build(RouterTable.TEST1).navigation());
        //        txt2.setOnClickListener(v -> Routers.getInstance().build(RouterTable.TEST2_ERR).navigation());
                txt2.setOnClickListener(v -> Routers.getInstance().build("/test2/info/123456").navigation());
//        txt2.setOnClickListener(v -> Routers.getInstance().build(RouterTable.TEST5).navigation());
        txt3.setOnClickListener(
            v -> Routers.getInstance().build(RouterTable.TEST3).navigation(this, new RouterCallback() {
                @Override
                public void onFailure(@Nullable RouteRequest request, RuntimeException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(RouteResponse response) {

                }

                @Override
                public void onFound(RouteRequest request) {

                }

                @Override
                public void onArrival(RouteRequest request) {

                }

                @Override
                public void onRedirect(String redirectUrl) {

                }
            }));
    }
}
