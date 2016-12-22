package sample;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.util.ArrayList;

import photopicker.utils.AndroidLifecycleUtils;
import sun.com.sundemo.R;


/**
 * Created by donglua on 15/5/31.
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

  private ArrayList<String> photoPaths = new ArrayList<String>();
  private LayoutInflater inflater;

  private Context mContext;


  public PhotoAdapter(Context mContext, ArrayList<String> photoPaths) {
    this.photoPaths = photoPaths;
    this.mContext = mContext;
    inflater = LayoutInflater.from(mContext);

  }


  @Override public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = inflater.inflate(R.layout.__picker_item_photo, parent, false);
    return new PhotoViewHolder(itemView);
  }


  @Override
  public void onBindViewHolder(final PhotoViewHolder holder, final int position) {

    boolean canLoadImage = AndroidLifecycleUtils.canLoadImage(holder.ivPhoto.getContext());

    if(canLoadImage) {
      Uri uri = Uri.fromFile(new File(photoPaths.get(position)));

      ImageRequest request = ImageRequestBuilder
              .newBuilderWithSource(uri)
              .setAutoRotateEnabled(true)
              .build();
      PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
              .setOldController(holder.ivPhoto.getController())
              .setImageRequest(request)
              .setAutoPlayAnimations(true)
              .build();
      holder.ivPhoto.setController(controller);
    }
  }


  @Override public int getItemCount() {
    return photoPaths.size();
  }


  public static class PhotoViewHolder extends RecyclerView.ViewHolder {
    private SimpleDraweeView ivPhoto;
    private View vSelected;
    public PhotoViewHolder(View itemView) {
      super(itemView);
      ivPhoto   = (SimpleDraweeView) itemView.findViewById(R.id.iv_photo);
      vSelected = itemView.findViewById(R.id.v_selected);
      vSelected.setVisibility(View.GONE);
    }
  }

}
