package com.meitu.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.etsy.android.grid.StaggeredGridView;
import com.lidroid.xutils.util.LogUtils;
import com.meitu.android.model.ImageListModel;
import com.meitu.android.utils.HttpUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * author:wl
 * time: 下午 3:04
 */
public class ImageListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, AbsListView.OnScrollListener {
    private String category_id;
    private SwipeRefreshLayout mLoadingLayout;
    private int page = 1;
    private ImageListAdapter adapter;
    private StaggeredGridView mGridView;
    private List<ImageListModel> images = new ArrayList<>();
    private boolean hasMore;//是否有更多

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        category_id = getArguments().getString("category_id");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.image_list_fragment, null);
        mLoadingLayout = (SwipeRefreshLayout) view.findViewById(R.id.imageListLoadingLayout);
        mLoadingLayout.setColorSchemeColors(R.color.primary);
        mGridView = (StaggeredGridView) view.findViewById(R.id.imageListGridView);

        adapter = new ImageListAdapter(getActivity());
        mGridView.setAdapter(adapter);

        loadData(category_id);
        initListener();

        return view;
    }


    private void initListener() {
        mLoadingLayout.setOnRefreshListener(this);
        mGridView.setOnItemClickListener(this);
        mGridView.setOnScrollListener(this);
    }

    /**
     * load date from category_id
     *
     * @param category_id
     */
    private void loadData(String category_id) {
        HttpUtils.getImageList(getActivity(), page, category_id, new HttpUtils.RequestResultListener<ImageListModel>() {
            @Override
            public void loadSuccess(List<ImageListModel> imageListModelList) {
                mLoadingLayout.setRefreshing(false);
                if (imageListModelList != null) {
                    if (page == 1) {
                        images = imageListModelList;
                    }else {
                        images.addAll(imageListModelList);
                        hasMore = false;
                    }
                }

                if (images != null){
                    adapter.setImageListModelList(images);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void loadFail(String message) {
                mLoadingLayout.setRefreshing(false);
            }
        });
    }


    @Override
    public void onRefresh() {
        page = 1;
        loadData(category_id);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), PreviewPictureActivity.class);
        intent.putExtra(PreviewPictureActivity.PREVIEW_IMAGES_ID_KEY, images.get(position).getId());
        intent.putExtra(PreviewPictureActivity.PREVIEW_IMAGES_CATEGORY_KEY, category_id);
        startActivity(intent);

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (!hasMore) {
            int lastInScreen = firstVisibleItem + visibleItemCount;
            if (lastInScreen >= totalItemCount) {
                hasMore = true;
                page ++;
                loadData(category_id);
            }
        }
    }
}
