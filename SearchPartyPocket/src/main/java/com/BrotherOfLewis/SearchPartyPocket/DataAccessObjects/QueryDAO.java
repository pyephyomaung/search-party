package com.BrotherOfLewis.SearchPartyPocket.DataAccessObjects;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.BrotherOfLewis.SearchPartyPocket.vos.QueryVO;

import java.util.ArrayList;

/**
 * Created by pye on 7/8/13.
 */
public class QueryDAO {
    public static final String TAG = "QueryDAO";
    private static final String DB_FileName = "firstQueries.sqlite3";
    private static final String TABLE_FIRST_QUERIES = "queries";
    protected static final String QUERY = "query";
    protected static final String PACK = "pack";

    public static final String DEFAULT_PACK = "default";
    public static final String POP_PACK = "pop";
    public static final String CELEB_PACK = "celeb";

    private final AssetDBHelper assetDBHelper;

    public QueryDAO(Context context)
    {
        assetDBHelper = new AssetDBHelper(context, DB_FileName, null, 1);
    }

    public ArrayList<QueryVO> getAllByPack(String[] packs) {
        ArrayList<QueryVO> list = new ArrayList<QueryVO>();

        // filter packs
        String packSqlArray = "(";
        for (int i = 0; i < packs.length; i++)
        {
            if (i > 0) packSqlArray += ",";
            packSqlArray += "'" + packs[i] + "'";
        }
        packSqlArray += ")";

        String selectQuery = "SELECT * FROM " + TABLE_FIRST_QUERIES + " WHERE pack IN " + packSqlArray;
        SQLiteDatabase db = assetDBHelper.getReadableDatabase();
        Log.v(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        int count = cursor.getCount();

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                QueryVO queryVO = new QueryVO();
                queryVO.setQuery(cursor.getString(cursor.getColumnIndex(QUERY)));
                queryVO.setPack(cursor.getString(cursor.getColumnIndex(PACK)));
                list.add(queryVO);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }
}
