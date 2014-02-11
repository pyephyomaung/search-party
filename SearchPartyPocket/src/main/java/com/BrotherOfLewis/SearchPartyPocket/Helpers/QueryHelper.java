package com.BrotherOfLewis.SearchPartyPocket.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.BrotherOfLewis.SearchPartyPocket.DataAccessObjects.QueryDAO;
import com.BrotherOfLewis.SearchPartyPocket.MainActivity;
import com.BrotherOfLewis.SearchPartyPocket.Models.QueryQuestion;
import com.BrotherOfLewis.SearchPartyPocket.R;
import com.BrotherOfLewis.SearchPartyPocket.SearchPartyPocketApp;
import com.BrotherOfLewis.SearchPartyPocket.vos.QueryVO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Pye on 8/10/13.
 */
public class QueryHelper {
    public static String TAG = "QueryHelper";
    private static final int numberOfQueryQuestionsToCache = 10;
    private static final int threshold = 5;
    private static ArrayList<QueryVO> queries;
    private static ArrayList<QueryQuestion> queryQuestions = new ArrayList<QueryQuestion>();
    private static String[] availablePacks = new String[] { QueryDAO.DEFAULT_PACK };

    public static void setAvailablePacks(String[] array)
    {
        Log.v(TAG, "setAvailablePacks array.length = " + array.length );
        availablePacks = array;
    }

    public static ArrayList<QueryVO> getQueries()
    {
        if (queries == null || queries.size() == 0)
        {
            // this only happens at initialization and when queries run out
            // a separate refresh queries function is called when available pack setting changes
            try {
                Context context = SearchPartyPocketApp.getAppContext();
                QueryDAO queryDAO = new QueryDAO(context);
                Log.v(TAG, "getQueries 1");
                queries = queryDAO.getAllByPack(availablePacks);
                Log.v(TAG, "getQueries queries.length = " + queries.size());
            }
            catch (Exception e) {
                Log.v(TAG, "getQueries error:" + e.getMessage());
            }

        }
        return queries;
    }

    private static boolean ShouldRefreshQueriesWhenCachingDone = false;
    public static void tryRefreshQueries(String[] packs)
    {
        availablePacks = packs;
        if (isBusyCaching) {
            Log.v(TAG, "TryRefreshQueries scheduled because of isBusyCaching");
            ShouldRefreshQueriesWhenCachingDone = true;
        }
        else {
            refreshQueries();
        }
    }

    private static void refreshQueries()
    {
        Log.v(TAG, "RefreshQueries availablePacks count = " + availablePacks.length);
        Context context = SearchPartyPocketApp.getAppContext();
        QueryDAO queryDAO = new QueryDAO(context);
        queries = queryDAO.getAllByPack(availablePacks);
    }

    QueryQuestionListener queryQuestionListener;
    public void setQueryQuestionListener(QueryQuestionListener listener) {
        queryQuestionListener = listener;
    }

    private static boolean isBusyCaching = false;
    private static boolean FireOnQueryQuestionReturnWhenReady = false;
    public void getQueryQuestionAsync()
    {
        Log.v(TAG, "getQueryQuestionAsync isBusyCaching = " + isBusyCaching);
        if (queryQuestions.size() == 0)
        {
            FireOnQueryQuestionReturnWhenReady = true;
            if (!isBusyCaching) cacheQueryQuestions();
        }
        else
        {
            if (queryQuestionListener != null)
            {
                QueryQuestion first = queryQuestions.remove(0);
                queryQuestionListener.OnQueryQuestionReturn(first);

                if (queryQuestions.size() < threshold && !isBusyCaching)
                {
                    cacheQueryQuestions();
                }
            }
        }
    }

    private void FireEvent(Object sender, String s) {
        System.out.println(s);
    }

    public void cacheQueryQuestions() {
        isBusyCaching = true;
        Log.v(TAG, "cacheQueryQuestions");
        cacheQueryQuestions(numberOfQueryQuestionsToCache);

//        // cache initally one first if empty
//        if (queryQuestions.size() == 0)
//        {
//            int currentQueriesCount = getQueries().size();
//            int rand = new Random().nextInt(currentQueriesCount);
//            QueryVO currentQueryVO = getQueries().get(rand);
//            getQueries().remove(rand);
//            GoogleSuggestionHelper gsh = new GoogleSuggestionHelper(){
//                @Override
//                protected void onPostExecute(List<QueryQuestion> queryQuestionList)
//                {
//                    queryQuestions.addAll(queryQuestionList);
//                }
//            };
//            gsh.execute(currentQueryVO.getQuery());
//        }
//
//
//        List<String> queryVOsToCache = new ArrayList<String>();
//        for (int i = 0; i < numberOfQueryQuestionsToCache; i++)
//        {
//            int currentQueriesCount = getQueries().size();
//            if (currentQueriesCount > 0)
//            {
//                int rand = new Random().nextInt(currentQueriesCount);
//                QueryVO currentQueryVO = getQueries().remove(rand);
//                queryVOsToCache.add(currentQueryVO.getQuery());
//            }
//        }
//
//        GoogleSuggestionHelper gsh = new GoogleSuggestionHelper(){
//            @Override
//            protected void onPostExecute(List<QueryQuestion> queryQuestionList)
//            {
//                queryQuestions.addAll(queryQuestionList);
//                isBusyCaching = false;
//            }
//        };
//        String[] strArray = queryVOsToCache.toArray(new String[queryVOsToCache.size()]);
//        gsh.execute(strArray);
    }

    public void cacheQueryQuestions(final int n) {
        if (n == 0) {
            onCacheQueryQuestionsCompleted();   // terminate cache place 1
            return;
        }
        Log.v(TAG, "cacheQueryQuestions n = " + n);
        int currentQueriesCount = getQueries().size();
        int rand = new Random().nextInt(currentQueriesCount);
        QueryVO currentQueryVO = getQueries().get(rand);
        getQueries().remove(rand);
        GoogleSuggestionHelper gsh = new GoogleSuggestionHelper(){
            @Override
            protected void onPostExecute(List<QueryQuestion> queryQuestionList)
            {
                if (queryQuestionList != null && queryQuestionList.size() > 0)
                {
                    if (FireOnQueryQuestionReturnWhenReady)
                    {
                        if (queryQuestionListener != null) queryQuestionListener.OnQueryQuestionReturn(queryQuestionList.get(0));
                        FireOnQueryQuestionReturnWhenReady = false;
                    }
                    else
                        queryQuestions.addAll(queryQuestionList);
                    cacheQueryQuestions(n - 1);
                }
                else
                {
                    Log.v(TAG, "onPostExecute: No internet connection");
                    onCacheQueryQuestionsCompleted();   // terminate cache place 2
                    //Toast.makeText(SearchPartyPocketApp.getAppContext(),"Cannot load data. Check internet connection",Toast.LENGTH_LONG).show();
                }
            }
        };
        gsh.execute(currentQueryVO.getQuery());
    }

    private void onCacheQueryQuestionsCompleted() {
        Log.v(TAG, "onCacheQueryQuestionsCompleted");
        if (ShouldRefreshQueriesWhenCachingDone) refreshQueries();
        isBusyCaching = false;
        if (queryQuestionListener != null) queryQuestionListener.OnCacheQueryQuestionCompleted();
    }

    public static int getBestStreak()
    {
        Context context = SearchPartyPocketApp.getAppContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                String.valueOf(R.string.SAVED_SHARE_PREFERENCE_KEY), Context.MODE_PRIVATE);
        return sharedPref.getInt(String.valueOf(R.string.SAVED_INT_BEST_STREAK_KEY), 0);
    }

    public static void setBestStreak(int streak)
    {
        Context context = SearchPartyPocketApp.getAppContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                String.valueOf(R.string.SAVED_SHARE_PREFERENCE_KEY), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(String.valueOf(R.string.SAVED_INT_BEST_STREAK_KEY), streak);
        editor.commit();
    }

    public static boolean hasQueryQuestionsRaady()
    {
        return (queryQuestions != null && queryQuestions.size() > 0);
    }
}

