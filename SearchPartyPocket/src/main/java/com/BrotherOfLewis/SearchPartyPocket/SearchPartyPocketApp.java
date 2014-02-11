package com.BrotherOfLewis.SearchPartyPocket;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.BrotherOfLewis.SearchPartyPocket.DataAccessObjects.QueryDAO;
import com.BrotherOfLewis.SearchPartyPocket.Helpers.GoogleSuggestionHelper;
import com.BrotherOfLewis.SearchPartyPocket.Helpers.QueryHelper;
import com.BrotherOfLewis.SearchPartyPocket.Helpers.SoundEffectHelper;

import java.util.ArrayList;

/**
 * Created by Pye on 8/10/13.
 */
public class SearchPartyPocketApp extends Application {

    private static Context context;
    public static String TAG = "SearchPartyPocketApp";

    public void onCreate(){
        super.onCreate();
        SearchPartyPocketApp.context = getApplicationContext();

        // initialize available packs
        try
        {
            Log.v(TAG, "getAvailablePacks");
            String prefKeyPacksDefault =  context.getResources().getString(R.string.pref_key_packs_default);
            String prefKeyPacksPop = context.getResources().getString(R.string.pref_key_packs_pop);
            String prefKeyPacksCeleb = context.getResources().getString(R.string.pref_key_packs_celeb);
            String[] packsPrefKeys = new String[] { prefKeyPacksDefault,prefKeyPacksPop,prefKeyPacksCeleb };

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(SearchPartyPocketApp.getAppContext());
            ArrayList<String> tmpAvailablePacks = new ArrayList<String>();
            boolean includeDefault = sharedPref.getBoolean(prefKeyPacksDefault,true);
            boolean includePop = sharedPref.getBoolean(prefKeyPacksPop,false);
            boolean includeCeleb = sharedPref.getBoolean(prefKeyPacksCeleb,false);

            if (includeDefault) tmpAvailablePacks.add(QueryDAO.DEFAULT_PACK);
            if (includePop) tmpAvailablePacks.add(QueryDAO.POP_PACK);
            if (includeCeleb) tmpAvailablePacks.add(QueryDAO.CELEB_PACK);
            QueryHelper.setAvailablePacks(tmpAvailablePacks.toArray(new String[tmpAvailablePacks.size()]));
        }
        catch (Exception e)
        {
            Log.v(TAG, "getAvailablePacks error:" + e.getMessage());
        }

        // start query caching
        Log.v(TAG, "start query caching");
        QueryHelper qHelper = new QueryHelper();
        qHelper.cacheQueryQuestions();

        // initialize sound effects
        Log.v(TAG, "initialize sound effects");
        SoundEffectHelper.getInstance().init(context);
    }

    public static Context getAppContext() {
        Toast.makeText(context, "test Toast", Toast.LENGTH_LONG);
        return SearchPartyPocketApp.context;
    }
}