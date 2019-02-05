package android.test.shimorouterdemo;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import chuxin.shimo.shimowendang.smrouter.annotation.Router;
import chuxin.shimo.shimowendang.smrouter.core.Routers;

@Router(value = RouterTable.TEST5,interceptors = {TestInterceptor4.class,TestInterceptor2.class})
public class Test5Activity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView viewById = findViewById(R.id.txt);
        viewById.setText("test5");
        final TextView txt2 = findViewById(R.id.txt2);
        final TextView txt3 = findViewById(R.id.txt3);
        txt2.setText("jump");
        txt3.setText("");
        txt2.setOnClickListener(v -> Routers.getInstance().build(RouterTable.TEST2));
    }
}
