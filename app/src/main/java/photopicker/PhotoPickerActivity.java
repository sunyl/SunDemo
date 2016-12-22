package photopicker;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

import java.util.ArrayList;
import java.util.List;

import photopicker.entity.Photo;
import photopicker.event.OnItemCheckListener;
import photopicker.fragment.ImagePagerFragment;
import photopicker.fragment.PhotoPickerFragment;
import sun.com.sundemo.R;

import static android.widget.Toast.LENGTH_LONG;

public class PhotoPickerActivity extends AppCompatActivity {
    private PhotoPickerFragment pickerFragment;
    private ImagePagerFragment imagePagerFragment;
    private MenuItem menuDoneItem;

    private int maxCount = PhotoPicker.DEFAULT_MAX_COUNT;

    /**
     * to prevent multiple calls to inflate menu
     */
    private boolean menuIsInflated = false;

    private boolean showGif = false;
    private int columnNumber = PhotoPicker.DEFAULT_COLUMN_NUMBER;
    private ArrayList<String> originalPhotos = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Fresco.hasBeenInitialized()) {
            ImagePipelineConfig config = ImagePipelineConfig.newBuilder(getApplicationContext())
                    .setDownsampleEnabled(true)
                    .build();
            Fresco.initialize(getApplicationContext(), config);
        }

        boolean showCamera = getIntent().getBooleanExtra(PhotoPicker.EXTRA_SHOW_CAMERA, true);
        boolean showGif = getIntent().getBooleanExtra(PhotoPicker.EXTRA_SHOW_GIF, false);
        boolean previewEnabled = getIntent().getBooleanExtra(PhotoPicker.EXTRA_PREVIEW_ENABLED, true);

        setShowGif(showGif);

        setContentView(R.layout.__picker_activity_photo_picker);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        setTitle(R.string.__picker_title);

        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            actionBar.setElevation(25);
        }

        maxCount = getIntent().getIntExtra(PhotoPicker.EXTRA_MAX_COUNT, PhotoPicker.DEFAULT_MAX_COUNT);
        columnNumber = getIntent().getIntExtra(PhotoPicker.EXTRA_GRID_COLUMN, PhotoPicker.DEFAULT_COLUMN_NUMBER);
        originalPhotos = getIntent().getStringArrayListExtra(PhotoPicker.EXTRA_ORIGINAL_PHOTOS);

        pickerFragment = (PhotoPickerFragment) getSupportFragmentManager().findFragmentByTag(PhotoPickerFragment.PHOTO_PICKER_TAG);
        if (pickerFragment == null) {
            pickerFragment = PhotoPickerFragment
                    .newInstance(showCamera, showGif, previewEnabled, columnNumber, maxCount, originalPhotos);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, pickerFragment, PhotoPickerFragment.PHOTO_PICKER_TAG)
                    .commit();
            getSupportFragmentManager().executePendingTransactions();
        }

        pickerFragment.getPhotoGridAdapter().setOnItemCheckListener(new OnItemCheckListener() {
            @Override
            public boolean OnItemCheck(int position, Photo photo, final boolean isCheck, int selectedItemCount) {

                int total = selectedItemCount + (isCheck ? -1 : 1);

                menuDoneItem.setEnabled(total > 0);

                if (maxCount <= 1) {
                    List<String> photos = pickerFragment.getPhotoGridAdapter().getSelectedPhotos();
                    if (!photos.contains(photo.getPath())) {
                        photos.clear();
                        pickerFragment.getPhotoGridAdapter().notifyDataSetChanged();
                    }
                    return true;
                }

                if (total > maxCount) {
                    Toast.makeText(getActivity(), getString(R.string.__picker_over_max_count_tips, maxCount),
                            LENGTH_LONG).show();
                    return false;
                }
                menuDoneItem.setTitle(getString(R.string.__picker_done_with_count, total, maxCount));
                return true;
            }
        });

    }

    public void addImagePagerFragment(ImagePagerFragment imagePagerFragment) {
        this.imagePagerFragment = imagePagerFragment;
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, this.imagePagerFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!menuIsInflated) {
            getMenuInflater().inflate(R.menu.__picker_menu_picker, menu);
            menuDoneItem = menu.findItem(R.id.done);
            if (originalPhotos != null && originalPhotos.size() > 0) {
                menuDoneItem.setEnabled(true);
                menuDoneItem.setTitle(
                        getString(R.string.__picker_done_with_count, originalPhotos.size(), maxCount));
            } else {
                menuDoneItem.setEnabled(false);
            }
            menuIsInflated = true;
            return true;
        }
        return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        if (item.getItemId() == R.id.done) {
            Intent intent = new Intent();
            ArrayList<String> selectedPhotos = pickerFragment.getPhotoGridAdapter().getSelectedPhotoPaths();
            intent.putStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS, selectedPhotos);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (pickerFragment != null && pickerFragment.onKeyDown(keyCode, event)) {
                return true;
            } else if (imagePagerFragment != null && imagePagerFragment.isVisible()) {
                imagePagerFragment.runExitAnimation(new Runnable() {
                    public void run() {
                        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                            getSupportFragmentManager().popBackStack();
                        }
                    }
                });
                return true;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public PhotoPickerActivity getActivity() {
        return this;
    }

    public boolean isShowGif() {
        return showGif;
    }

    public void setShowGif(boolean showGif) {
        this.showGif = showGif;
    }

}
