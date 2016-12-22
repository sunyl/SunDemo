package sample;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import photodraweeview.OnPhotoTapListener;
import photodraweeview.OnViewTapListener;
import photodraweeview.PhotoDraweeView;
import sun.com.sundemo.R;

/**
 * Created by sunyunlong on 2016/12/20.
 */
public class PhotoDraweeviewActivity extends Activity {

    private PhotoDraweeView mPhotoDraweeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_photo_draweeview);
        mPhotoDraweeView = (PhotoDraweeView) findViewById(R.id.photo_drawee_view);
        mPhotoDraweeView.setPhotoUri(Uri.parse("res:///" + R.drawable.panda));
        mPhotoDraweeView.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                Toast.makeText(view.getContext(), "onPhotoTap :  x =  " + x + ";" + " y = " + y,
                        Toast.LENGTH_SHORT).show();
            }
        });
        mPhotoDraweeView.setOnViewTapListener(new OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                Toast.makeText(view.getContext(), "onViewTap", Toast.LENGTH_SHORT).show();
            }
        });

        mPhotoDraweeView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(v.getContext(), "onLongClick", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}