package android.test.shimorouterdemo;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import chuxin.shimo.shimowendang.smrouter.annotation.Router;

@Router(value = RouterTable.TEST2/*TEST2_ERR*/, interceptors = {TestInterceptor2.class})
public class Test2Activity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView viewById = findViewById(R.id.txt);
        viewById.setText("test2");
        final TextView txt2 = findViewById(R.id.txt2);
        final TextView txt3 = findViewById(R.id.txt3);
        txt2.setText("");
        txt3.setText("");
    }
}
