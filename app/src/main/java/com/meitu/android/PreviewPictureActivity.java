package com.meitu.android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.meitu.android.model.ImageListByAlbumIdModel;
import com.meitu.android.utils.FileUtils;
import com.meitu.android.utils.ImageLoaderHelper;
import com.meitu.android.utils.PaletteUtil;
import com.meitu.android.utils.ShareUtils;
import com.meitu.android.utils.ToastUtil;
import com.meitu.android.view.PhotoViewPager;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;

/**
 * author:wl
 * time: 下午 5:22
 */
public class PreviewPictureActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private PhotoViewPager viewPager;
    private PreviewPictureAdapter adapter;
    public static final String PREVIEW_IMAGES_ID_KEY = "id";
    public static final String PREVIEW_IMAGES_CATEGORY_KEY = "category";
    private String id;
    private String category;
    private ArrayList<ImageListByAlbumIdModel.AlbumModel> images;
    private TextView title;
    private Toolbar mToolbar;
    private TextView mPicNumText;
    private MenuItem gridMenu;
    private int current_position;//当前viewPager的position
    private View mReloadLayout;
    private MenuItem saveMenu;
    private MenuItem shareMenu;

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.preview_picture_layout);
        findView();
        initData();
    }

    public void findView() {
        mToolbar = (Toolbar) findViewById(R.id.previewToolbar);
        viewPager = (PhotoViewPager) findViewById(R.id.previewPictureViewPager);
        title = (TextView) findViewById(R.id.previewTitle);
        mPicNumText = (TextView) findViewById(R.id.previewPictureNum);
        mReloadLayout = findViewById(R.id.previewPictureNoNetLayout);

        mReloadLayout.setOnClickListener(this);
        viewPager.addOnPageChangeListener(this);
    }

    /**
     * init the data
     */
    private void initData() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        id = getIntent().getStringExtra(PREVIEW_IMAGES_CATEGORY_KEY);
        category = getIntent().getStringExtra(PREVIEW_IMAGES_ID_KEY);
        adapter = new PreviewPictureAdapter();
        viewPager.setAdapter(adapter);

        loadData();
    }

    /**
     * load the data
     */
    private void loadData() {
        com.meitu.android.utils.HttpUtils.getImageListByAlbumId(this, id, category, new com.meitu.android.utils.HttpUtils.RequestResultListener<ImageListByAlbumIdModel>() {
            @Override
            public void loadSuccess(List<ImageListByAlbumIdModel> tabList) {
                if (!tabList.isEmpty()) {
                    saveMenu.setVisible(true);
                    shareMenu.setVisible(true);
                    mReloadLayout.setVisibility(View.GONE);
                    images = (ArrayList<ImageListByAlbumIdModel.AlbumModel>) tabList.get(0).getAlbumModelList();
                    title.setText(images.get(0).getTitle());
                    mPicNumText.setText("1/" + images.size());
                    if (images.size() == 1) {
                        gridMenu.setVisible(false);
                    } else {
                        gridMenu.setVisible(true);
                    }

                    ImageLoaderHelper.getBitmapFromUrlUseGlide(PreviewPictureActivity.this, images.get(0).getMid_image(), new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            PaletteUtil.colorChange(PreviewPictureActivity.this, mToolbar, resource);
                        }
                    });

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void loadFail(String message) {
                mReloadLayout.setVisibility(View.VISIBLE);
                PaletteUtil.colorChange(PreviewPictureActivity.this , mToolbar, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_reload));
            }
        });
    }


    @Override
    public void onClick(View v) {
        loadData();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mPicNumText.setText((position + 1) + "/" + images.size());
        current_position = position;

        ImageLoaderHelper.getBitmapFromUrlUseGlide(PreviewPictureActivity.this, images.get(position).getMid_image(), new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                PaletteUtil.colorChange(PreviewPictureActivity.this, mToolbar, resource);
            }
        });
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_preview_pic, menu);
        gridMenu = menu.findItem(R.id.menu_grid);
        saveMenu = menu.findItem(R.id.menu_save);
        shareMenu = menu.findItem(R.id.menu_share);

        saveMenu.setVisible(false);
        shareMenu.setVisible(false);
        gridMenu.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_grid:
                MobclickAgent.onEvent(PreviewPictureActivity.this , "menu_grid");
                Intent intent = new Intent(PreviewPictureActivity.this, ImageGridActivity.class);
                ImageGridActivity.images = images;
                startActivity(intent);
                break;
            case R.id.menu_save://save
                MobclickAgent.onEvent(PreviewPictureActivity.this , "menu_save");
                ImageLoaderHelper.getImageLoader().loadImage(images.get(current_position).getBig_image(), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        String savePath = FileUtils.saveTempBitmap(PreviewPictureActivity.this, loadedImage);
                        if (savePath != null) {
                            ToastUtil.showLongToast(PreviewPictureActivity.this, "成功保存到相册!");
                            /*这个广播的目的就是更新图库，发了这个广播进入相册就可以找到图片***/
                            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            Uri uri = Uri.fromFile(new File(savePath));
                            intent.setData(uri);
                            sendBroadcast(intent);
                        }
                    }
                });


                /*ImageLoaderHelper.getBitmapFromUrlUseGlide(PreviewPictureActivity.this, images.get(current_position).getBig_image(), new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        String savePath = FileUtils.saveTempBitmap(PreviewPictureActivity.this, resource);
                        if (savePath != null){
                            ToastUtil.showLongToast(PreviewPictureActivity.this , "成功保存到相册!");
                            *//*这个广播的目的就是更新图库，发了这个广播进入相册就可以找到图片*//*
                            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            Uri uri = Uri.fromFile(new File(savePath));
                            intent.setData(uri);
                            sendBroadcast(intent);
                        }
                    }
                });

            */
                break;
            case R.id.menu_share://share
                MobclickAgent.onEvent(PreviewPictureActivity.this , "menu_share");
                File cacheImagePath = ImageLoaderHelper.getImageLoader().getDiskCache().get(images.get(current_position).getMid_image());
                if (cacheImagePath != null) {
                    ShareUtils.shareMsg(PreviewPictureActivity.this, "分享", "", "#魅图#", cacheImagePath.getPath());
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * PreviewPictureAdapter
     */
    class PreviewPictureAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return null != images ? images.size() : 0;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            container.addView(photoView, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            ImageLoaderHelper.displayImage(images.get(position).getMid_image(), photoView);
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
}
