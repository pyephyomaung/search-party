package com.BrotherOfLewis.SearchPartyPocket;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.BrotherOfLewis.SearchPartyPocket.Helpers.GoogleSuggestionHelper;
import com.BrotherOfLewis.SearchPartyPocket.Helpers.QueryHelper;
import com.BrotherOfLewis.SearchPartyPocket.Helpers.QueryQuestionListener;
import com.BrotherOfLewis.SearchPartyPocket.Helpers.SoundEffectHelper;
import com.BrotherOfLewis.SearchPartyPocket.Models.QueryQuestion;

import java.util.List;

public class ModeSelectionActivity extends Activity {
    private static final String TAG = ModeSelectionActivity.class.getSimpleName();
    private Button BtnSoloMode;
    private Button BtnPartyMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_selection);
        // Show the Up button in the action bar.
        setupActionBar();

        // solo mode button
        BtnSoloMode = (Button)findViewById(R.id.btnSoloMode);
        final Intent soloModeIntent = new Intent(this, SoloModeActivity.class);
        BtnSoloMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundEffectHelper.getInstance().playSound(SoundEffectHelper.SOUND_SUCCESS);
                if (QueryHelper.hasQueryQuestionsRaady())
                {
                    startActivity(soloModeIntent);
                }
                else
                {
                    // test connection
                    GoogleSuggestionHelper gsh = new GoogleSuggestionHelper(){
                        @Override
                        protected void onPostExecute(List<QueryQuestion> queryQuestionList)
                        {
                            if (queryQuestionList != null && queryQuestionList.size() > 0)
                            {
                                QueryHelper queryHelper = new QueryHelper();
                                final ProgressDialog dialog = new ProgressDialog(ModeSelectionActivity.this);
                                queryHelper.setQueryQuestionListener(new QueryQuestionListener() {
                                    @Override
                                    public void OnQueryQuestionReturn(QueryQuestion queryQuestion) {
                                        return;
                                    }

                                    @Override
                                    public void OnCacheQueryQuestionCompleted() {
                                        dialog.dismiss();
                                        startActivity(soloModeIntent);
                                    }
                                });
                                dialog.setTitle("Loading");
                                dialog.setMessage("Please wait...");
                                dialog.setCancelable(false);
                                dialog.show();
                                queryHelper.cacheQueryQuestions();
                            }
                            else
                                Toast.makeText(SearchPartyPocketApp.getAppContext(), "Cannot load data. Check internet connection", Toast.LENGTH_LONG).show();
                        }
                    };
                    gsh.execute("seafood");
                }
            }
        });

        // party mode button
        BtnPartyMode = (Button)findViewById(R.id.btnPartyMode);
        final Intent partyModeIntent = new Intent(this, PartyModeActivity.class);
        BtnPartyMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundEffectHelper.getInstance().playSound(SoundEffectHelper.SOUND_SUCCESS);
                if (QueryHelper.hasQueryQuestionsRaady())
                {
                    startActivity(partyModeIntent);
                }
                else
                {
                    // test connection
                    GoogleSuggestionHelper gsh = new GoogleSuggestionHelper(){
                        @Override
                        protected void onPostExecute(List<QueryQuestion> queryQuestionList)
                        {
                            if (queryQuestionList != null && queryQuestionList.size() > 0)
                            {
                                QueryHelper queryHelper = new QueryHelper();
                                final ProgressDialog dialog = new ProgressDialog(ModeSelectionActivity.this);
                                queryHelper.setQueryQuestionListener(new QueryQuestionListener() {
                                    @Override
                                    public void OnQueryQuestionReturn(QueryQuestion queryQuestion) {
                                        return;
                                    }

                                    @Override
                                    public void OnCacheQueryQuestionCompleted() {
                                        dialog.dismiss();
                                        startActivity(partyModeIntent);
                                    }
                                });
                                dialog.setTitle("Loading");
                                dialog.setMessage("Please wait...");
                                dialog.setCancelable(false);
                                dialog.show();
                                queryHelper.cacheQueryQuestions();
                            }
                            else
                                Toast.makeText(SearchPartyPocketApp.getAppContext(), "Cannot load data. Check internet connection", Toast.LENGTH_LONG).show();
                        }
                    };
                    gsh.execute("seafood");
                }
            }
        });
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mode_selection, menu);
        return true;
    }
    

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
