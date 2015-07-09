package com.meitu.android;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;

import com.meitu.android.model.TabModel;
import com.meitu.android.utils.HttpUtils;
import com.meitu.android.view.PhotoViewPager;
import com.umeng.fb.FeedbackAgent;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity {

    private ViewPager viewPager;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private TabViewPagerAdapter adapter;

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        findView();
        setSupportActionBar(mToolbar);
        FeedbackAgent agent = new FeedbackAgent(MainActivity.this);
        agent.sync();
        agent.closeAudioFeedback();
    }

    public void findView() {
        mToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        viewPager = (PhotoViewPager) findViewById(R.id.mainViewPager);
        mTabLayout = (TabLayout) findViewById(R.id.mainTabLayout);
        mTabLayout.setTabTextColors( Color.LTGRAY , Color.WHITE);//设置文本在选中和未选中时候的颜色
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);


        HttpUtils.getMenu(this, new HttpUtils.RequestResultListener<TabModel>() {
            @Override
            public void loadSuccess(List<TabModel> tabList) {
                adapter = new TabViewPagerAdapter(getSupportFragmentManager());
                adapter.setTabList(tabList);
                viewPager.setAdapter(adapter);

                mTabLayout.setupWithViewPager(viewPager);
                // mTabLayout.setTabsFromPagerAdapter(adapter);
            }

            @Override
            public void loadFail(String message) {

            }
        });
    }


    /**
     * TabViewPagerAdapter
     */
    class TabViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments = new ArrayList<>();
        private List<TabModel> tablist;

        public TabViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setTabList(List<TabModel> tabList) {
            this.tablist = tabList;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return tablist == null ? "" : tablist.get(position).getName();
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putString("category_id", tablist.get(position).getId());

            ImageListFragment fragment = (ImageListFragment) Fragment.instantiate(MainActivity.this, ImageListFragment.class.getName(), bundle);

            return fragment;
        }

        @Override
        public int getCount() {
            return tablist.size();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_about://关于
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                Dialog dialog = builder.create();
                builder.setTitle("关于")
                        .setCancelable(true)
                        .setMessage(getResources().getString(R.string.about_text))
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (dialog != null)
                                    dialog.dismiss();
                            }
                        })
                        .show();
                break;
            case R.id.menu_feedback://意见反馈
                new FeedbackAgent(MainActivity.this).startFeedbackActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
