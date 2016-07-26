package com.meitu.android;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.meitu.android.model.TabModel;
import com.meitu.android.utils.HttpUtils;
import com.meitu.android.view.PhotoViewPager;
import com.umeng.onlineconfig.OnlineConfigAgent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends BaseActivity {

    private ViewPager viewPager;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private TabViewPagerAdapter adapter;
    //是否打开所有的tab，为了审核
    private boolean isForbiddenTab;

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        findView();
        OnlineConfigAgent.getInstance().updateOnlineConfig(this);
        setSupportActionBar(mToolbar);
        String onLineConfigValue = OnlineConfigAgent.getInstance().getConfigParams(this, "IS_FORBIDDEN_TAB");
        isForbiddenTab = "1".equals(onLineConfigValue) || TextUtils.isEmpty(onLineConfigValue);
        //ToastUtil.showShortToast(this ,  OnlineConfigAgent.getInstance().getConfigParams(this , "IS_FORBIDDEN_TAB"));
    }

    public void findView() {
        mToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        viewPager = (PhotoViewPager) findViewById(R.id.mainViewPager);
        mTabLayout = (TabLayout) findViewById(R.id.mainTabLayout);
        mTabLayout.setTabTextColors(Color.LTGRAY, Color.WHITE);//设置文本在选中和未选中时候的颜色
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        HttpUtils.getMenu(this, new HttpUtils.RequestResultListener<TabModel>() {
            @Override
            public void loadSuccess(List<TabModel> tabList) {
                adapter = new TabViewPagerAdapter(getSupportFragmentManager());
                if (isForbiddenTab) {
                    List<TabModel> noSexList = new ArrayList<>();
                    Iterator iterator = tabList.iterator();
                    List<String> forbiddenList = Arrays.asList("校花", "萌女", "气质", "明星", "非主流", "清纯");

                    while (iterator.hasNext()) {
                        TabModel model = (TabModel) iterator.next();
                        if (forbiddenList.contains(model.getName())) {
                            iterator.remove();
                            tabList.remove(model);
                        }
                    }

                    noSexList.addAll(tabList);
                    adapter.setTabList(noSexList);

                } else {
                    adapter.setTabList(tabList);
                }

                viewPager.setAdapter(adapter);
                mTabLayout.setupWithViewPager(viewPager);
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
            /*case R.id.menu_feedback://意见反馈
                new FeedbackAgent(MainActivity.this).startFeedbackActivity();
                break;*/
        }
        return super.onOptionsItemSelected(item);
    }
}
