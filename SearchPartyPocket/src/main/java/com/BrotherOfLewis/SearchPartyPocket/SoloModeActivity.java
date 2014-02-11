package com.BrotherOfLewis.SearchPartyPocket;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.BrotherOfLewis.SearchPartyPocket.DataAccessObjects.QueryDAO;
import com.BrotherOfLewis.SearchPartyPocket.Helpers.GoogleSuggestionHelper;
import com.BrotherOfLewis.SearchPartyPocket.Helpers.QueryHelper;
import com.BrotherOfLewis.SearchPartyPocket.Helpers.QueryQuestionListener;
import com.BrotherOfLewis.SearchPartyPocket.Helpers.SoundEffectHelper;
import com.BrotherOfLewis.SearchPartyPocket.Models.QueryQuestion;
import com.BrotherOfLewis.SearchPartyPocket.Models.Suggestion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SoloModeActivity extends Activity {
    private static final String TAG = SoloModeActivity.class.getSimpleName();
    private TextView TxtQuestion;
    private List<Button> BtnSuggestions;
    private ImageButton ImgBtnNext;
    private ShareActionProvider mShareActionProvider;
    private TextView TxtCurrentStreak;
    private TextView TxtBestStreak;
    private TextView TxtCorrectPercentage;

    private int currentStreak = 0;
    private int correctCount = 0;
    private int incorrectCount = 0;
    private boolean alreadyAnswered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solo_mode);
        // Show the Up button in the action bar.
        setupActionBar();

        TxtQuestion = (TextView) findViewById(R.id.txtQuestion);
        BtnSuggestions = new ArrayList<Button>();
        BtnSuggestions.add((Button) findViewById(R.id.btnSuggestion1));
        BtnSuggestions.add((Button) findViewById(R.id.btnSuggestion2));
        BtnSuggestions.add((Button) findViewById(R.id.btnSuggestion3));
        ImgBtnNext = (ImageButton) findViewById(R.id.imgBtnNext);
        TxtCurrentStreak = (TextView) findViewById(R.id.txtCurrentStreak);
        TxtBestStreak = (TextView) findViewById(R.id.txtBestStreak);
        TxtCorrectPercentage = (TextView) findViewById(R.id.txtCorrectPercentage);

        // click event for next button
        ImgBtnNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (QueryHelper.hasQueryQuestionsRaady())
                {
                    LoadQueryQuestionAsync();
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
                                final ProgressDialog dialog = new ProgressDialog(SoloModeActivity.this);
                                queryHelper.setQueryQuestionListener(new QueryQuestionListener() {
                                    @Override
                                    public void OnQueryQuestionReturn(QueryQuestion queryQuestion) {
                                        return;
                                    }

                                    @Override
                                    public void OnCacheQueryQuestionCompleted() {
                                        dialog.dismiss();
                                        LoadQueryQuestionAsync();
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

        LoadQueryQuestionAsync();
    }

    private void LoadQueryQuestionAsync() {
        Log.v(TAG, "LoadQueryQuestionAsync");

        // reset query and suggestions
        alreadyAnswered = false;
        TxtQuestion.setText("");
        TxtCurrentStreak.setText(String.valueOf(currentStreak));
        refreshBestStreak();

        for (Button btn : BtnSuggestions)
        {
            btn.setText("");
            btn.setBackgroundColor(Color.WHITE);
            if (Build.VERSION.SDK_INT >= 11)
                btn.setAlpha(0.8f);
        }
        Collections.shuffle(BtnSuggestions);

        // get query and suggestions
        final QueryHelper qHelper = new QueryHelper();
        qHelper.setQueryQuestionListener(new QueryQuestionListener() {
            @Override
            public void OnQueryQuestionReturn(QueryQuestion qq) {
                // not enough choice, then skip the current question
                Log.v(TAG, "OnQueryQuestionReturn");
                if (qq.getSuggestions().length < BtnSuggestions.size())
                {
                    qHelper.getQueryQuestionAsync();
                    return;
                }

                TxtQuestion.setText(qq.getQuery());

                Suggestion[] suggestions = qq.getSuggestions();
                for (int i = 0; i < BtnSuggestions.size(); i++) {
                    final Button tmpBtn = BtnSuggestions.get(i);
                    tmpBtn.setOnClickListener(null);

                    if (i < suggestions.length)
                    {
                        tmpBtn.setText(suggestions[i].getTheSuggestion());
                        final int finalI = i;
                        tmpBtn.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                if (alreadyAnswered) return;

                                alreadyAnswered = true;
                                ImgBtnNext.setEnabled(true);
                                ImgBtnNext.setBackgroundResource(R.drawable.next_search);

                                if (finalI == 0) {
                                    SoundEffectHelper.getInstance().playSound(SoundEffectHelper.SOUND_SUCCESS);
                                    tmpBtn.setBackgroundColor(getResources().getColor(R.color.myGreen));
                                    correctCount++;
                                    currentStreak++;
                                    if (currentStreak > QueryHelper.getBestStreak())
                                    {
                                        QueryHelper.setBestStreak(currentStreak);
                                        refreshBestStreak();
                                    }
                                }
                                else {
                                    SoundEffectHelper.getInstance().playSound(SoundEffectHelper.SOUND_FAIL);
                                    BtnSuggestions.get(0).setBackgroundColor(getResources().getColor(R.color.myGreen));
                                    tmpBtn.setBackgroundColor(Color.RED);
                                    incorrectCount++;
                                    currentStreak = 0;
                                }
                                refreshCorrectPercentage();
                            }
                        });
                    }
                }
            }

            @Override
            public void OnCacheQueryQuestionCompleted() {
                return;
            }
        });
        qHelper.getQueryQuestionAsync();
        ImgBtnNext.setEnabled(false);
        ImgBtnNext.setBackgroundResource(R.drawable.next_search_off);
    }

    private void refreshCorrectPercentage() {
        // load correct percentage
        int totalCount = (correctCount + incorrectCount);
        if (totalCount > 0)
        {
            int correctPercentage = (int)((correctCount / (double) totalCount) * 100);
            TxtCorrectPercentage.setText(String.valueOf(correctPercentage) + "%");
        }
    }

    private void refreshBestStreak() {
        final int bestStreak = QueryHelper.getBestStreak();
        TxtBestStreak.setText(String.valueOf(bestStreak));
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

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.solo_mode, menu);

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
//            case R.id.action_settings:
//                final Intent settingsIntent = new Intent(this, SettingsActivity.class);
//                startActivity(settingsIntent);
//                break;
            case R.id.action_share:
                try
                {
                    Log.v(TAG, "someone is sharing");
                    // create bitmap screen capture
                    RelativeLayout layout = (RelativeLayout) findViewById(R.id.solo_mode_layout);
                    View layoutView = layout.getRootView();
                    layoutView.setDrawingCacheEnabled(true);
                    Bitmap bitmap = Bitmap.createBitmap(layoutView.getDrawingCache());
                    layoutView.setDrawingCacheEnabled(false);

                    // image naming and path  to include sd card  appending name you choose for file
                    String mPath = Environment.getExternalStorageDirectory().toString() + "/searchparty_share.jpg";
                    Log.v(TAG, "file will be saved to " + mPath);

                    // create bitmap screen capture
                    OutputStream fout = null;
                    File imageFile = new File(mPath);

                    try {
                        fout = new FileOutputStream(imageFile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fout);
                        fout.flush();
                        fout.close();

                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Log.v(TAG, e.getMessage());
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Log.v(TAG, e.getMessage());
                    }

                    // create share intent
                    Log.v(TAG, "create share intent 1");
                    final Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                    Uri tmpUri = Uri.fromFile(new File(mPath));
                    Log.v(TAG, "create share intent 2");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, tmpUri);
                    shareIntent.setType("image/jpeg");
                    startActivity(Intent.createChooser(shareIntent, "Share"));
                }
                catch (Exception e)
                {
                    Log.v(TAG, e.getMessage());
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
