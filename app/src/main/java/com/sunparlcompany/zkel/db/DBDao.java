package com.sunparlcompany.zkel.db;

import android.content.Context;
import android.util.Log;

import org.xutils.DbManager;
import org.xutils.db.table.TableEntity;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/12 0012.
 */
public class DBDao {
    private Context context;
    private DbManager db;

    public DBDao(Context context) {
        this.context = context;
        /**
         * 初始化DaoConfig配置
         */
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                //设置数据库名，默认xutils.db
                .setDbName("zkdevice.db")
                //设置数据库的版本号
                .setDbVersion(1)
                //设置数据库打开的监听
                .setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {
                        //开启数据库支持多线程操作，提升性能，对写入加速提升巨大
                        db.getDatabase().enableWriteAheadLogging();
                    }
                })
                //设置数据库更新的监听
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                    }
                })
                //设置表创建的监听
                .setTableCreateListener(new DbManager.TableCreateListener() {
                    @Override
                    public void onTableCreated(DbManager db, TableEntity<?> table){
                        Log.i("DBDao", "onTableCreated：" + table.getName());
                    }
                });
        //设置是否允许事务，默认true
        //.setAllowTransaction(true)

         db = x.getDb(daoConfig);
    }

    public DbManager getDBUtils() {
        return db;
    }

    public List<DbDeviceEntity> getShowMenu() {
        List<DbDeviceEntity> dbDeviceEntities = null;
        try {
            dbDeviceEntities = db.selector(DbDeviceEntity.class).where("isShow", "=", 1).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (dbDeviceEntities == null)
            dbDeviceEntities = new ArrayList<>();
        return dbDeviceEntities;
    }
    public List<DbDeviceEntity> getAllDeviceInfo() {
        List<DbDeviceEntity> deviceEntities = null;
        try {
            deviceEntities = db.findAll(DbDeviceEntity.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (deviceEntities == null)
            deviceEntities = new ArrayList<>();
        return deviceEntities;
    }


}
