package photopicker.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import photopicker.entity.PhotoDirectory;
import sun.com.sundemo.R;


/**
 * Created by donglua on 15/6/28.
 */
public class PopupDirectoryListAdapter extends RecyclerView.Adapter<PopupDirectoryListAdapter.ViewHolder> {

    private List<PhotoDirectory> directories = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;

    public PopupDirectoryListAdapter(List<PhotoDirectory> directories) {
        this.directories = directories;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.__picker_item_directory, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(position);
            }
        });
        holder.bindData(directories.get(position));
    }

    @Override
    public int getItemCount() {
        return directories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public SimpleDraweeView ivCover;
        public TextView tvName;
        public TextView tvCount;

        public ViewHolder(View itemView) {
            super(itemView);
            ivCover = (SimpleDraweeView) itemView.findViewById(R.id.iv_dir_cover);
            tvName = (TextView) itemView.findViewById(R.id.tv_dir_name);
            tvCount = (TextView) itemView.findViewById(R.id.tv_dir_count);
        }

        public void bindData(PhotoDirectory directory) {
            Uri uri = Uri.fromFile(new File(directory.getCoverPath()));
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithSource(uri)
                    .setResizeOptions(new ResizeOptions(180, 180))
                    .setAutoRotateEnabled(true)
                    .build();
            PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                    .setOldController(ivCover.getController())
                    .setImageRequest(request)
                    .setAutoPlayAnimations(true)
                    .build();
            ivCover.setController(controller);


            tvName.setText(directory.getName());
            tvCount.setText(tvCount.getContext().getString(R.string.__picker_image_count, directory.getPhotos().size()));
        }
    }

}
