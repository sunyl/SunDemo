package photopicker.fragment;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import photopicker.adapter.PhotoPagerAdapter;
import photopicker.widget.MultiTouchViewPager;
import sun.com.sundemo.R;

/**
 * Created by donglua on 15/6/21.
 */
public class ImagePagerFragment extends Fragment {

    public final static String SELECTED_CURRENT_PATHS = "selected_paths";
    public final static String CURRENT_PATH = "current_path";

    private MultiTouchViewPager mViewPager;
    private PhotoPagerAdapter mPagerAdapter;

    public final static long ANIM_DURATION = 200L;

    public final static String ARG_THUMBNAIL_TOP = "THUMBNAIL_TOP";
    public final static String ARG_THUMBNAIL_LEFT = "THUMBNAIL_LEFT";
    public final static String ARG_THUMBNAIL_WIDTH = "THUMBNAIL_WIDTH";
    public final static String ARG_THUMBNAIL_HEIGHT = "THUMBNAIL_HEIGHT";
    public final static String ARG_HAS_ANIM = "HAS_ANIM";

    private int thumbnailTop = 0;
    private int thumbnailLeft = 0;
    private int thumbnailWidth = 0;
    private int thumbnailHeight = 0;

    private boolean hasAnim = false;

    private final ColorMatrix colorizerMatrix = new ColorMatrix();

    private int mCurrentItemIndex = 0;
    private ArrayList<String> mSelectPaths;
    private String mCurrentPath;

    public static ImagePagerFragment newInstance(List<String> paths, String currentPath) {
        ImagePagerFragment f = new ImagePagerFragment();
        Bundle args = new Bundle();
        args.putStringArray(SELECTED_CURRENT_PATHS, paths.toArray(new String[paths.size()]));
        args.putString(CURRENT_PATH, currentPath);
        args.putBoolean(ARG_HAS_ANIM, false);
        f.setArguments(args);
        return f;
    }

    public static ImagePagerFragment newInstance(List<String> paths, String currentPath, int[] screenLocation, int thumbnailWidth, int thumbnailHeight) {
        ImagePagerFragment f = newInstance(paths, currentPath);
        f.getArguments().putInt(ARG_THUMBNAIL_LEFT, screenLocation[0]);
        f.getArguments().putInt(ARG_THUMBNAIL_TOP, screenLocation[1]);
        f.getArguments().putInt(ARG_THUMBNAIL_WIDTH, thumbnailWidth);
        f.getArguments().putInt(ARG_THUMBNAIL_HEIGHT, thumbnailHeight);
        f.getArguments().putBoolean(ARG_HAS_ANIM, true);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mSelectPaths = new ArrayList<>();
        if (bundle != null) {
            mSelectPaths.clear();
            String[] pathArr = bundle.getStringArray(SELECTED_CURRENT_PATHS);
            hasAnim = bundle.getBoolean(ARG_HAS_ANIM);
            mCurrentPath = bundle.getString(CURRENT_PATH);
            thumbnailTop = bundle.getInt(ARG_THUMBNAIL_TOP);
            thumbnailLeft = bundle.getInt(ARG_THUMBNAIL_LEFT);
            thumbnailWidth = bundle.getInt(ARG_THUMBNAIL_WIDTH);
            thumbnailHeight = bundle.getInt(ARG_THUMBNAIL_HEIGHT);
            if (pathArr != null) {
                mSelectPaths = new ArrayList<>(Arrays.asList(pathArr));
            }
            int index = mSelectPaths.indexOf(mCurrentPath);
            if (index == -1) {
                mSelectPaths.add(0, mCurrentPath);
                mCurrentItemIndex = 0;
            } else {
                mCurrentItemIndex = index;
            }
        }
        mPagerAdapter = new PhotoPagerAdapter(getActivity().getSupportFragmentManager(), mSelectPaths);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.__picker_picker_fragment_image_pager, container, false);
        mViewPager = (MultiTouchViewPager) rootView.findViewById(R.id.vp_photos);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(mCurrentItemIndex);
        mViewPager.setOffscreenPageLimit(1);

        if (savedInstanceState == null && hasAnim) {
            ViewTreeObserver observer = mViewPager.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mViewPager.getViewTreeObserver().removeOnPreDrawListener(this);
                    int[] screenLocation = new int[2];
                    mViewPager.getLocationOnScreen(screenLocation);
                    thumbnailLeft = thumbnailLeft - screenLocation[0];
                    thumbnailTop = thumbnailTop - screenLocation[1];
                    runEnterAnimation();
                    return true;
                }
            });
        }

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                hasAnim = mCurrentItemIndex == position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return rootView;
    }

    /**
     * The enter animation scales the picture in from its previous thumbnail
     * size/location, colorizing it in parallel. In parallel, the background of the
     * activity is fading in. When the pictue is in place, the text description
     * drops down.
     */
    private void runEnterAnimation() {
        final long duration = ANIM_DURATION;

        // Set starting values for properties we're going to animate. These
        // values scale and position the full size version down to the thumbnail
        // size/location, from which we'll animate it back up
        ViewHelper.setPivotX(mViewPager, 0);
        ViewHelper.setPivotY(mViewPager, 0);
        ViewHelper.setScaleX(mViewPager, (float) thumbnailWidth / mViewPager.getWidth());
        ViewHelper.setScaleY(mViewPager, (float) thumbnailHeight / mViewPager.getHeight());
        ViewHelper.setTranslationX(mViewPager, thumbnailLeft);
        ViewHelper.setTranslationY(mViewPager, thumbnailTop);

        // Animate scale and translation to go from thumbnail to full size
        ViewPropertyAnimator.animate(mViewPager)
                .setDuration(duration)
                .scaleX(1)
                .scaleY(1)
                .translationX(0)
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator());

        // Fade in the black background
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(mViewPager.getBackground(), "alpha", 0, 255);
        bgAnim.setDuration(duration);
        bgAnim.start();

        // Animate a color filter to take the image from grayscale to full color.
        // This happens in parallel with the image scaling and moving into place.
        ObjectAnimator colorizer = ObjectAnimator.ofFloat(ImagePagerFragment.this,
                "saturation", 0, 1);
        colorizer.setDuration(duration);
        colorizer.start();

    }


    /**
     * The exit animation is basically a reverse of the enter animation, except that if
     * the orientation has changed we simply scale the picture back into the center of
     * the screen.
     *
     * @param endAction This action gets run after the animation completes (this is
     *                  when we actually switch activities)
     */
    public void runExitAnimation(final Runnable endAction) {

        if (!getArguments().getBoolean(ARG_HAS_ANIM, false) || !hasAnim) {
            endAction.run();
            return;
        }

        final long duration = ANIM_DURATION;

        // Animate image back to thumbnail size/location
        ViewPropertyAnimator.animate(mViewPager)
                .setDuration(duration)
                .setInterpolator(new AccelerateInterpolator())
                .scaleX((float) thumbnailWidth / mViewPager.getWidth())
                .scaleY((float) thumbnailHeight / mViewPager.getHeight())
                .translationX(thumbnailLeft)
                .translationY(thumbnailTop)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        endAction.run();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });

        // Fade out background
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(mViewPager.getBackground(), "alpha", 0);
        bgAnim.setDuration(duration);
        bgAnim.start();

        // Animate a color filter to take the image back to grayscale,
        // in parallel with the image scaling and moving into place.
        ObjectAnimator colorizer =
                ObjectAnimator.ofFloat(ImagePagerFragment.this, "saturation", 1, 0);
        colorizer.setDuration(duration);
        colorizer.start();
    }


    /**
     * This is called by the colorizing animator. It sets a saturation factor that is then
     * passed onto a filter on the picture's drawable.
     *
     * @param value saturation
     */
    public void setSaturation(float value) {
        colorizerMatrix.setSaturation(value);
        ColorMatrixColorFilter colorizerFilter = new ColorMatrixColorFilter(colorizerMatrix);
        mViewPager.getBackground().setColorFilter(colorizerFilter);
    }


    public ViewPager getViewPager() {
        return mViewPager;
    }


    public ArrayList<String> getPaths() {
        return mSelectPaths;
    }


    public int getCurrentItem() {
        return mViewPager.getCurrentItem();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSelectPaths.clear();
        mSelectPaths = null;
        if (mViewPager != null) {
            mViewPager.setAdapter(null);
        }
    }
}
