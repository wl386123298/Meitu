package com.meitu.android.utils;

import android.content.Context;

import com.lidroid.xutils.DbUtils;
import com.meitu.android.config.AppConfig;

/**
 * Created by wliang on 15/5/30.
 */
public class DBUtils {
    private static final String DBNAME = "meitu";

    protected static DbUtils getDBHelper(Context context , boolean allowTransaction) {
        DbUtils db = DbUtils.create(context, DBNAME, 1, new DbUtils.DbUpgradeListener() {

            @Override
            public void onUpgrade(DbUtils dbUtils, int oldVersion, int newVersion) {
                if (newVersion > oldVersion) {

                }
            }
        });

        db.configDebug(AppConfig.DEBUG);
        db.configAllowTransaction(allowTransaction);

        return db;
    }
}
