package sample;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import photopicker.PhotoPicker;
import photopicker.PhotoPreview;
import sun.com.sundemo.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private PhotoAdapter photoAdapter;

    private ArrayList<String> selectedPhotos = new ArrayList<>();

    private int currentClickId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        photoAdapter = new PhotoAdapter(this, selectedPhotos);

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL));
        recyclerView.setAdapter(photoAdapter);


        findViewById(R.id.button).setOnClickListener(this);
        findViewById(R.id.button_no_camera).setOnClickListener(this);
        findViewById(R.id.button_one_photo).setOnClickListener(this);
        findViewById(R.id.button_photo_gif).setOnClickListener(this);
        findViewById(R.id.button_multiple_picked).setOnClickListener(this);
        findViewById(R.id.button_photodraweeview).setOnClickListener(this);
       /* recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                PhotoPreview.builder()
                        .setPhotos(selectedPhotos)
                        .setCurrentItem(position)
                        .start(MainActivity.this);
            }
        }));*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK &&
                (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {

            List<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }
            selectedPhotos.clear();

            if (photos != null) {

                selectedPhotos.addAll(photos);
            }
            photoAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //if (currentClickId != -1) onClick(currentClickId);
        } else {
            // permission denied, boo! Disable the
            // functionality that depends on this permission.
            Toast.makeText(this, "No read storage permission! Cannot perform the action.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
        // No need to explain to user as it is obvious
        return false;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.button: {
                PhotoPicker.builder()
                        .setPhotoCount(9)
                        .setGridColumnCount(4)
                        .start(this);
                break;
            }

            case R.id.button_no_camera: {
                PhotoPicker.builder()
                        .setPhotoCount(7)
                        .setShowCamera(false)
                        .setPreviewEnabled(false)
                        .start(this);
                break;
            }

            case R.id.button_one_photo: {
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .start(this);
                break;
            }

            case R.id.button_photo_gif: {
                PhotoPicker.builder()
                        .setPhotoCount(9)
                        .setGridColumnCount(4)
                        .setShowCamera(true)
                        .setShowGif(true)
                        .start(this);
                break;
            }

            case R.id.button_multiple_picked: {
                PhotoPicker.builder()
                        .setPhotoCount(9)
                        .setShowCamera(true)
                        .setGridColumnCount(4)
                        .setSelected(selectedPhotos)
                        .start(this);
                break;
            }
            case R.id.button_photodraweeview:
                Intent intent = new Intent(MainActivity.this, PhotoDraweeviewActivity.class);
                startActivity(intent);
                break;
        }

        currentClickId = viewId;
    }
}
