package photopicker.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import photodraweeview.OnPhotoTapListener;
import photodraweeview.PhotoDraweeView;
import photopicker.event.OnItemCheckListener;
import photopicker.fragment.PhotoPickerFragment;
import photopicker.utils.AndroidLifecycleUtils;
import sun.com.sundemo.R;

public class PhotoPagerAdapter extends PagerAdapter {

    private List<String> paths = new ArrayList<>();
    private FragmentManager mFragmentManager;
    private OnItemCheckListener onItemCheckListener;

    public PhotoPagerAdapter(FragmentManager fragmentManager, List<String> paths) {
        this.paths = paths;
        mFragmentManager = fragmentManager;
    }

    public void setOnItemCheckListener(OnItemCheckListener onItemCheckListener) {
        this.onItemCheckListener = onItemCheckListener;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final Context context = container.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.__picker_picker_item_pager, container, false);
        final PhotoDraweeView imageView = (PhotoDraweeView) itemView.findViewById(R.id.iv_pager);
        final CheckBox mIvCheck = (CheckBox) itemView.findViewById(R.id.iv_checkbox);
        final String path = paths.get(position);
        boolean isChecked = ((PhotoPickerFragment) mFragmentManager.findFragmentByTag(PhotoPickerFragment.PHOTO_PICKER_TAG)).checkSelected(path);
        Log.i("syl", "----------->instantiateItem isChecked = " + isChecked);
        mIvCheck.setSelected(isChecked);
        final Uri uri;
        if (path.startsWith("http")) {
            uri = Uri.parse(path);
        } else {
            uri = Uri.fromFile(new File(path));
        }
        mIvCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isEnable = true;

                /*if (onItemCheckListener != null) {
                    isEnable = onItemCheckListener.OnItemCheck(pos, photo, isChecked, getSelectedPhotos().size());
                }
                if (isEnable) {
                    toggleSelection(photo);
                    notifyItemChanged(pos);
                }*/
            }
        });


        boolean canLoadImage = AndroidLifecycleUtils.canLoadImage(context);
        if (canLoadImage) {
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithSource(uri)
                    .setResizeOptions(new ResizeOptions(1000, 1000))
                    .setAutoRotateEnabled(true)
                    .build();
            PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(imageView.getController())
                    .setAutoPlayAnimations(true)
                    .setControllerListener(new BaseControllerListener<ImageInfo>() {
                        @Override
                        public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                            super.onFinalImageSet(id, imageInfo, animatable);
                            if (imageInfo == null) {
                                return;
                            }
                            imageView.update(imageInfo.getWidth(), imageInfo.getHeight());
                        }
                    });

            imageView.setController(controller.build());
        }

        imageView.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                if (context instanceof Activity) {
                    if (!((Activity) context).isFinishing()) {
                        ((Activity) context).onBackPressed();
                    }
                }
            }
        });
        container.addView(itemView);
        return itemView;
    }


    @Override
    public int getCount() {
        return paths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
