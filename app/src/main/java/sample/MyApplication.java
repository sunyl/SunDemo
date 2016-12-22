package sample;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by sunyunlong on 2016/12/21.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
