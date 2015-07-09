package com.meitu.android.model;

import com.lidroid.xutils.db.annotation.Table;

/**
 * Tab model
 * Created by wliang on 15/5/31.
 */
public class TabModel {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
