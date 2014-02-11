package com.BrotherOfLewis.SearchPartyPocket.DataAccessObjects;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by pye on 8/29/13.
 */
public class AssetDBHelper extends SQLiteOpenHelper {

    String dbName;
    Context context;

    File dbFile;

    public AssetDBHelper(Context context, String dbName, SQLiteDatabase.CursorFactory factory,
                    int version) {
        super(context, dbName, factory, version);
        this.context = context;
        this.dbName = dbName;
        dbFile = context.getDatabasePath(dbName);
    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {

        if(!dbFile.exists()){
            SQLiteDatabase db = super.getWritableDatabase();
            copyDataBase(db.getPath());
        }
        return super.getWritableDatabase();
    }

    @Override
    public synchronized SQLiteDatabase getReadableDatabase() {
        if(!dbFile.exists()){
            SQLiteDatabase db = super.getReadableDatabase();
            copyDataBase(db.getPath());
        }
        return super.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    private void copyDataBase(String dbPath){
        try{
            InputStream assetDB = context.getAssets().open(dbName);

            OutputStream appDB = new FileOutputStream(dbPath,false);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = assetDB.read(buffer)) > 0) {
                appDB.write(buffer, 0, length);
            }

            appDB.flush();
            appDB.close();
            assetDB.close();
        }catch(IOException e){
            e.printStackTrace();
        }

    }

}
