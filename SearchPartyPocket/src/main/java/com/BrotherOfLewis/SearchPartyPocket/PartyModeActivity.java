
package com.BrotherOfLewis.SearchPartyPocket;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.BrotherOfLewis.SearchPartyPocket.DataAccessObjects.QueryDAO;
import com.BrotherOfLewis.SearchPartyPocket.Helpers.GoogleSuggestionHelper;
import com.BrotherOfLewis.SearchPartyPocket.Helpers.QueryHelper;
import com.BrotherOfLewis.SearchPartyPocket.Helpers.QueryQuestionListener;
import com.BrotherOfLewis.SearchPartyPocket.Helpers.SoundEffectHelper;
import com.BrotherOfLewis.SearchPartyPocket.Models.Player;
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

public class PartyModeActivity extends Activity {
    public static final String TAG = PartyModeActivity.class.getSimpleName();
    private TextView TxtQuestion;
    private List<Button> BtnSuggestions;
    private ImageButton ImgBtnNext;
    private ViewGroup ViewGroup;
    private TextView TxtMsg;
    private ShareActionProvider mShareActionProvider;

    private final int playerCount = 2; // default play
    private final int questionCount = 5;
    private int currentPlayerIndex = -1;
    private Player[] players;
    private ProgressBar[] playerProgressBars;
    private TextView[] playerTitles;
    private boolean alreadyAnswered = false;
    private boolean winnerDeclared = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_mode);
        // Show the Up button in the action bar.
        setupActionBar();

        TxtQuestion = (TextView) findViewById(R.id.txtQuestion_party);
        TxtMsg = (TextView) findViewById(R.id.txtMsg_party);
        BtnSuggestions = new ArrayList<Button>();
        BtnSuggestions.add((Button) findViewById(R.id.btnSuggestion1_party));
        BtnSuggestions.add((Button) findViewById(R.id.btnSuggestion2_party));
        BtnSuggestions.add((Button) findViewById(R.id.btnSuggestion3_party));
        ViewGroup = (ViewGroup) findViewById(R.id.party_mode_layout);

        // click event for next button
        ImgBtnNext = (ImageButton) findViewById(R.id.imgBtnNext_party);
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
                                final ProgressDialog dialog = new ProgressDialog(PartyModeActivity.this);
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

        // players
        players = new Player[playerCount];
        playerProgressBars = new ProgressBar[playerCount];
        playerTitles = new TextView[playerCount];
        for (int id = 0; id < playerCount; id++)
        {
            players[id] = new Player(id);
            switch (id)
            {
                case 0:
                    players[id].setDescription("Red Team");
                    players[id].setBackgroundId(R.drawable.background_red);
                    playerProgressBars[id] = (ProgressBar) findViewById(R.id.progressBarRed);
                    playerTitles[id] = (TextView) findViewById(R.id.txtRedTeam);
                    break;
                case 1:
                    players[id].setDescription("Blue Team");
                    players[id].setBackgroundId(R.drawable.background_blue);
                    playerProgressBars[id] = (ProgressBar) findViewById(R.id.progressBarBlue);
                    playerTitles[id] = (TextView) findViewById(R.id.txtBlueTeam);
                    break;
                default:
                    players[id].setBackgroundId(R.drawable.background_yellow);
                    break;
            }
        }

        LoadQueryQuestionAsync();
    }

    private void nextPlayer()
    {
        currentPlayerIndex = (currentPlayerIndex + 1) % playerCount;
        ViewGroup.setBackgroundResource(players[currentPlayerIndex].getBackgroundId());

        for (int i = 0; i < playerTitles.length; i++)
        {
            if (i == currentPlayerIndex)
            {
                playerTitles[i].setShadowLayer(8.0f, 0.0f, 0.0f, Color.WHITE);
                playerTitles[i].setTypeface(Typeface.DEFAULT_BOLD);
            }
            else
            {
                playerTitles[i].setShadowLayer(0.0f, 0.0f, 0.0f, Color.WHITE);
                playerTitles[i].setTypeface(Typeface.DEFAULT);
            }
        }
    }

    private void LoadQueryQuestionAsync() {
        Log.v(TAG, "LoadQueryQuestionAsync");
        if (winnerDeclared) return;
        nextPlayer();

        // reset query and suggestions
        alreadyAnswered = false;
        TxtQuestion.setText("");

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
                Suggestion[] suggestions = qq.getSuggestions();
                Log.v(TAG, "LoadQueryQuestionAsync 1");
                if (suggestions.length < BtnSuggestions.size())
                {
                    qHelper.getQueryQuestionAsync();
                    return;
                }
                Log.v(TAG, "LoadQueryQuestionAsync 2");

                TxtQuestion.setText(qq.getQuery());
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
                                    incrementCorrectCountForCurrentPlayer();
                                }
                                else {
                                    SoundEffectHelper.getInstance().playSound(SoundEffectHelper.SOUND_FAIL);
                                    BtnSuggestions.get(0).setBackgroundColor(getResources().getColor(R.color.myGreen));
                                    tmpBtn.setBackgroundColor(Color.RED);
                                }
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

    private void incrementCorrectCountForCurrentPlayer() {
        players[currentPlayerIndex].incrementCorrectCount(1);

        if (playerProgressBars.length >= currentPlayerIndex + 1
                && playerProgressBars[currentPlayerIndex] != null)
        {
            int progressPercentage = (int) ((players[currentPlayerIndex].getCorrectCount() / (double) questionCount) * 100);
            playerProgressBars[currentPlayerIndex].setProgress(progressPercentage);
        }

        if (players[currentPlayerIndex].getCorrectCount() >= questionCount)
        {
            declareWinner();
        }
    }

    private void declareWinner() {
        winnerDeclared = true;

        TxtMsg.setText(players[currentPlayerIndex].getDescription() + " wins !!!");
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
        getMenuInflater().inflate(R.menu.party_mode, menu);
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
            case R.id.action_share:
                try
                {
                    Log.v(TAG, "someone is sharing");
                    // create bitmap screen capture
                    RelativeLayout layout = (RelativeLayout) findViewById(R.id.party_mode_layout);
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
