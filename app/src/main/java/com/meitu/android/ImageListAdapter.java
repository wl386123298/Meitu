package com.meitu.android;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.util.LogUtils;
import com.meitu.android.model.ImageListModel;
import com.meitu.android.utils.ImageLoaderHelper;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

/**
 * @Author: wl
 * @Date : 2015/06/18 17:29
 * @Description: {ImageListAdapter}
 */
public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> implements View.OnClickListener {
    private List<ImageListModel> imageListModelList;
    private OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener;

    public void setImageListModelList(List<ImageListModel> imageListModelList) {
        this.imageListModelList = imageListModelList;
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        mOnRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    @Override
    public ImageListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImageListAdapter.ViewHolder holder, int position) {
        String imageUrl = imageListModelList.get(position).getMid_image();

        //set tag
        holder.itemView.setTag(imageListModelList.get(position));
        if (holder.mImageView.getTag() == null || !holder.mImageView.getTag().toString().equals(imageUrl)) {
            holder.mImageView.setTag(imageUrl);
            ImageLoaderHelper.displayImage(imageUrl, holder.mImageView, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    //LogUtils.e(loadedImage.getHeight() + "***" + loadedImage.getWidth());
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.mImageView.getLayoutParams();
                    if (loadedImage.getHeight() > 350) {
                        params.height = loadedImage.getHeight() - 50;
                    }else if (loadedImage.getHeight()  < 200) {
                        params.height = (int) (loadedImage.getHeight() * 0.6 + loadedImage.getHeight());
                        LogUtils.e(params.height +"");
                    }else {
                        params.height = loadedImage.getHeight();
                    }

                    holder.mImageView.setLayoutParams(params);
                }
            });
        }

        holder.num.setText(imageListModelList.get(position).getNum());
    }

    @Override
    public int getItemCount() {
        return imageListModelList == null ? 0 : imageListModelList.size();
    }

    @Override
    public void onClick(View view) {
        if (mOnRecyclerViewItemClickListener != null) {
            mOnRecyclerViewItemClickListener.onItemClick(view, (ImageListModel) view.getTag());
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        private TextView num;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.list_item_image);
            num = (TextView) itemView.findViewById(R.id.list_item_num);
        }
    }


    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, ImageListModel model);
    }
}
