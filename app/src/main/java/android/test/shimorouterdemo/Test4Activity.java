package android.test.shimorouterdemo;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Set;

import chuxin.shimo.shimowendang.smrouter.annotation.Router;

@Router({RouterTable.TEST4_INFO, RouterTable.TEST4_INFO_WITH_ID})
public class Test4Activity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView viewById = findViewById(R.id.txt);
        final TextView txt2 = findViewById(R.id.txt2);
        final TextView txt3 = findViewById(R.id.txt3);
        viewById.setText("");
        txt2.setText("");
        txt3.setText("test4");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Set<String> keys = extras.keySet();
            for (String key : keys) {
                viewById.append(key + "=>");
                Object v = extras.get(key);
                if (v != null) {
                    viewById.append(v + "=>" + v.getClass().getSimpleName());
                } else {
                    viewById.append("null");
                }
                viewById.append("\n\n");
            }
        }
    }
}
