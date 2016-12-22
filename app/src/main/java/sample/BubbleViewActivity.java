package sample;

import android.app.Activity;
import android.os.Bundle;
import bubbleimage.widget.BubbleImageView;
import sun.com.sundemo.R;

/**
 * Created by sunyunlong on 2016/12/22.
 */

public class BubbleViewActivity extends Activity {

    private BubbleImageView mBubbleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_bubbleview);
        mBubbleImageView = (BubbleImageView) findViewById(R.id.iv_bubbleview);
        init();
    }

    private void init() {
        mBubbleImageView.setImageURI("http://img04.tooopen.com/images/20130701/tooopen_10055061.jpg");
    }
}
