package com.meitu.android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.WallpaperManager;
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
import android.view.animation.OvershootInterpolator;
import android.widget.RelativeLayout;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
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
import java.io.IOException;
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
    private Toolbar mToolbar;
    private int current_position;//当前viewPager的position
    private View mReloadLayout;
    private MenuItem num;
    private FloatingActionMenu mFloatMenu;
    private FloatingActionButton mFabPreviewAll;
    private FloatingActionButton mFabSave;
    private FloatingActionButton mFabSetPic;
    private FloatingActionButton mFabShare;

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.preview_picture_layout);
        findView();
        initData();
    }

    public void findView() {
        mToolbar = (Toolbar) findViewById(R.id.previewToolbar);
        viewPager = (PhotoViewPager) findViewById(R.id.previewPictureViewPager);
        mReloadLayout = findViewById(R.id.previewPictureNoNetLayout);
        mFloatMenu = (FloatingActionMenu) findViewById(R.id.previewPictureActionMenu);
        mFloatMenu.setClosedOnTouchOutside(true);
        mFabPreviewAll = (FloatingActionButton)findViewById(R.id.fab_preview_all);
        mFabSave = (FloatingActionButton)findViewById(R.id.fab_save);
        mFabSetPic = (FloatingActionButton)findViewById(R.id.fab_set_pic);
        mFabShare  = (FloatingActionButton)findViewById(R.id.fab_share);
        setViewsClick(this, mFabPreviewAll , mFabSave , mFabSetPic , mFabShare);
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
        createCustomAnimation();
        loadData();
    }

    private void createCustomAnimation() {
        AnimatorSet set = new AnimatorSet();

        ObjectAnimator scaleOutX = ObjectAnimator.ofFloat(mFloatMenu.getMenuIconView(), "scaleX", 1.0f, 0.2f);
        ObjectAnimator scaleOutY = ObjectAnimator.ofFloat(mFloatMenu.getMenuIconView(), "scaleY", 1.0f, 0.2f);

        ObjectAnimator scaleInX = ObjectAnimator.ofFloat(mFloatMenu.getMenuIconView(), "scaleX", 0.2f, 1.0f);
        ObjectAnimator scaleInY = ObjectAnimator.ofFloat(mFloatMenu.getMenuIconView(), "scaleY", 0.2f, 1.0f);

        scaleOutX.setDuration(50);
        scaleOutY.setDuration(50);

        scaleInX.setDuration(150);
        scaleInY.setDuration(150);

        scaleInX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mFloatMenu.getMenuIconView().setImageResource(mFloatMenu.isOpened()
                        ? R.mipmap.ic_close : R.mipmap.icon_dot);
            }
        });

        set.play(scaleOutX).with(scaleOutY);
        set.play(scaleInX).with(scaleInY).after(scaleOutX);
        set.setInterpolator(new OvershootInterpolator(2));

        mFloatMenu.setIconToggleAnimatorSet(set);
    }

    private void setViewsClick(View.OnClickListener clickListener, View... views) {
        for (View view : views) {
            view.setOnClickListener(clickListener);
        }
    }


    /**
     * load the data
     */
    private void loadData() {
        com.meitu.android.utils.HttpUtils.getImageListByAlbumId(this, id, category, new com.meitu.android.utils.HttpUtils.RequestResultListener<ImageListByAlbumIdModel>() {
            @Override
            public void loadSuccess(List<ImageListByAlbumIdModel> tabList) {
                if (!tabList.isEmpty()) {
                    mReloadLayout.setVisibility(View.GONE);
                    images = (ArrayList<ImageListByAlbumIdModel.AlbumModel>) tabList.get(0).getAlbumModelList();
                    mToolbar.setTitle(images.get(0).getTitle());
                    num.setTitle("1/" + images.size());
                    findViewById(R.id.fab_preview_all).setVisibility(images.size() != 1 ? View.VISIBLE : View.GONE);

                    ImageLoaderHelper.getBitmapFromUrlUseGlide(PreviewPictureActivity.this, images.get(0).getMid_image(), new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            PaletteUtil.colorChange(PreviewPictureActivity.this, mToolbar, resource);
                            PaletteUtil.getColor(mFloatMenu , resource,  mFabPreviewAll , mFabSave , mFabSetPic , mFabShare);
                        }
                    });

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void loadFail(String message) {
                mReloadLayout.setVisibility(View.VISIBLE);
                PaletteUtil.colorChange(PreviewPictureActivity.this, mToolbar, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_reload));
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.previewPictureNoNetLayout:
                loadData();
                break;
            //保存
            case R.id.fab_save:
                MobclickAgent.onEvent(PreviewPictureActivity.this, "menu_save");
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

            //分享
            case R.id.fab_share:
                MobclickAgent.onEvent(PreviewPictureActivity.this, "menu_share");
                File cacheImagePath = ImageLoaderHelper.getImageLoader().getDiskCache().get(images.get(current_position).getMid_image());
                if (cacheImagePath != null) {
                    ShareUtils.shareMsg(PreviewPictureActivity.this, "分享", "", "#魅图#", cacheImagePath.getPath());
                }

                break;

            //浏览全部
            case R.id.fab_preview_all:
                MobclickAgent.onEvent(PreviewPictureActivity.this, "menu_grid");
                Intent intent = new Intent(PreviewPictureActivity.this, ImageGridActivity.class);
                ImageGridActivity.images = images;
                startActivity(intent);
                break;
            //设置壁纸
            case R.id.fab_set_pic:
                ImageLoaderHelper.getImageLoader().loadImage(images.get(current_position).getBig_image(), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        try {
                            WallpaperManager.getInstance(PreviewPictureActivity.this).setBitmap(loadedImage);
                            ToastUtil.showShortToast(getApplicationContext(), "壁纸设置成功");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });

                break;
        }

        mFloatMenu.toggle(true);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        num.setTitle((position + 1) + "/" + images.size());
        current_position = position;

        ImageLoaderHelper.getBitmapFromUrlUseGlide(PreviewPictureActivity.this, images.get(position).getMid_image(), new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                PaletteUtil.colorChange(PreviewPictureActivity.this, mToolbar, resource);
                PaletteUtil.getColor(mFloatMenu , resource,  mFabPreviewAll , mFabSave , mFabSetPic , mFabShare);
            }
        });
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_preview_pic, menu);
        num = menu.findItem(R.id.menu_num);

        return super.onCreateOptionsMenu(menu);
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
