package com.meitu.android;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.etsy.android.grid.util.DynamicHeightImageView;
import com.meitu.android.model.ImageListModel;
import com.meitu.android.utils.ImageLoaderHelper;

import java.util.List;

/**
 * @Author: wl
 * @Email : wangliang@daoxila.com
 * @Date : 2015/06/18 17:29
 * @Description: {ImageListAdapter}
 * @Copyright(c) 2015 www.daoxila.com. All rights reserved.
 */
public class ImageListAdapter extends MyBaseAdapter {
    private List<ImageListModel> imageListModelList;
    private Context context;

    public ImageListAdapter(Context context) {
        super(context);
        this.context = context;
    }

    public void setImageListModelList(List<ImageListModel> imageListModelList) {
        this.imageListModelList = imageListModelList;
    }

    @Override
    public int itemLayoutRes() {
        return R.layout.list_item_layout;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent, ViewHolder holder) {
        DynamicHeightImageView imageView = holder.obtainView(convertView, R.id.list_item_image);
        TextView num = holder.obtainView(convertView, R.id.list_item_num);

        ImageLoaderHelper.displayImage(imageListModelList.get(position).getSmall_image(), imageView);
        num.setText(imageListModelList.get(position).getNum());

        return convertView;
    }

    @Override
    public int getCount() {
        return imageListModelList == null ? 0 : imageListModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
