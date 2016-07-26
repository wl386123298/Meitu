package com.meitu.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.meitu.android.model.ImageListModel;
import com.meitu.android.utils.HttpUtils;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * author:wl
 * time: 下午 3:04
 */
public class ImageListFragment extends Fragment implements PullLoadMoreRecyclerView.PullLoadMoreListener, ImageListAdapter.OnRecyclerViewItemClickListener {
    private String category_id;
    private int page = 1;
    private ImageListAdapter adapter;
    private PullLoadMoreRecyclerView mGridView;
    private List<ImageListModel> images = new ArrayList<>();
    private boolean hasMore;//是否有更多
    private AlertDialog dialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        category_id = getArguments().getString("category_id");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_list_fragment, container, false);
        mGridView = (PullLoadMoreRecyclerView) view.findViewById(R.id.pullLoadMoreRecyclerView);

        initPullRecycleView();
        adapter = new ImageListAdapter();
        mGridView.setAdapter(adapter);
        loadData(category_id);

        adapter.setOnRecyclerViewItemClickListener(this);
        mGridView.setOnPullLoadMoreListener(this);

        return view;
    }

    private void initPullRecycleView() {
        mGridView.setStaggeredGridLayout(2);
        mGridView.setColorSchemeResources(R.color.primary);
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
                mGridView.setPullLoadMoreCompleted();
                if (page == 1) images.clear();

                if (imageListModelList != null) {
                    images.addAll(imageListModelList);
                    hasMore = false;
                }

                if (images != null) {
                    adapter.setImageListModelList(images);
                    adapter.notifyItemInserted(images.size());
                }
            }

            @Override
            public void loadFail(String message) {
                mGridView.setPullLoadMoreCompleted();
            }
        });
    }


    @Override
    public void onRefresh() {
        page = 1;
        loadData(category_id);
    }

    @Override
    public void onLoadMore() {
        if (!hasMore) {
            hasMore = true;
            page++;
            loadData(category_id);
        }
    }

    @Override
    public void onItemClick(View view, ImageListModel model) {
        Intent intent = new Intent(getActivity(), PreviewPictureActivity.class);
        intent.putExtra(PreviewPictureActivity.PREVIEW_IMAGES_ID_KEY, model.getId());
        intent.putExtra(PreviewPictureActivity.PREVIEW_IMAGES_CATEGORY_KEY, category_id);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), view, getString(R.string.imageTransitionName));
        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
    }
}
