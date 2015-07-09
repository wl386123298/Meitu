package com.meitu.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ToxicBakery.viewpager.transforms.ScaleInOutTransformer;
import com.ToxicBakery.viewpager.transforms.ZoomOutSlideTransformer;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.etsy.android.grid.util.DynamicHeightImageView;
import com.lidroid.xutils.util.LogUtils;
import com.meitu.android.model.ImageListByAlbumIdModel;
import com.meitu.android.utils.ImageLoaderHelper;
import com.meitu.android.utils.PaletteUtil;
import com.meitu.android.view.PhotoViewPager;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * @Author: wl
 * @Email : wangliang@daoxila.com
 * @Date : 2015/06/24 13:17
 * @Description: {所有预览图片类}
 * @Copyright(c) 2015 www.daoxila.com. All rights reserved.
 */
public class ImageGridActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private Toolbar mToolbar;
    private GridView mGridView;
    public static ArrayList<ImageListByAlbumIdModel.AlbumModel> images;
    private ImageGridAdapter adapter;
    private AlertDialog dialog;

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.image_grid_layout);
        findView();
        initData();
    }

    private void initData() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new ImageGridAdapter(this);
        mGridView.setAdapter(adapter);

        if (images != null) {
            adapter.notifyDataSetChanged();
            getSupportActionBar().setTitle(images.get(0).getTitle());

            ImageLoaderHelper.getBitmapFromUrlUseGlide(this, images.get(0).getMid_image(), new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    PaletteUtil.colorChange(ImageGridActivity.this, mToolbar, resource);
                }
            });

        }
    }

    private void findView() {
        mToolbar = (Toolbar) findViewById(R.id.includeToolbar);
        mGridView = (GridView) findViewById(R.id.imageGridview);

        mGridView.setOnItemClickListener(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        images = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        showImageDialog(position);
    }


    private void showImageDialog(int position) {
        PhotoViewPager photoViewPager  = new PhotoViewPager(this);
        photoViewPager.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        //photoViewPager.setPageTransformer(true , new ZoomOutSlideTransformer());
        PhotoViewAdapter adapter = new PhotoViewAdapter();
        photoViewPager.setAdapter(adapter);

        if (images != null){
            adapter.notifyDataSetChanged();
        }

        photoViewPager.setCurrentItem(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(ImageGridActivity.this, R.style.AppTheme_AlertDialog);
        dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width =  WindowManager.LayoutParams.MATCH_PARENT;
        params.height =  WindowManager.LayoutParams.MATCH_PARENT;

        Window window = dialog.getWindow();
        window.setAttributes(params);
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window.setContentView(photoViewPager);
    }

    /**
     * PreviewPictureAdapter
     */
    class PhotoViewAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return null != images ? images.size() : 0;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            container.addView(photoView, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            ImageLoaderHelper.displayImageForGlideNoScale(ImageGridActivity.this, images.get(position).getMid_image(), photoView);

            photoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                @Override
                public void onViewTap(View view, float v, float v1) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });

            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }


    /**
     * ImageGridAdapter
     */
    class ImageGridAdapter extends MyBaseAdapter {

        public ImageGridAdapter(Context context) {
            super(context);
        }

        @Override
        public int itemLayoutRes() {
            return R.layout.list_item_layout;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent, ViewHolder holder) {
            DynamicHeightImageView imageView = holder.obtainView(convertView, R.id.list_item_image);
            holder.obtainView(convertView, R.id.list_item_num).setVisibility(View.GONE);
            imageView.setHeightRatio(0.8);
            ImageLoaderHelper.displayImageForGlideNoScale(ImageGridActivity.this , images.get(position).getMid_image(), imageView);
            return convertView;
        }

        @Override
        public int getCount() {
            return images == null ? 0 : images.size();
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

}
