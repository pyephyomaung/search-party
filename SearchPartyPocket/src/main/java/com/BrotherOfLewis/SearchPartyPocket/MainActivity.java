package com.BrotherOfLewis.SearchPartyPocket;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.BrotherOfLewis.SearchPartyPocket.Helpers.SoundEffectHelper;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Button BtnPlay;
    private Button BtnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // settings button
        BtnSettings = (Button)findViewById(R.id.btnSettings);
        final Intent settingsIntent = new Intent(this, SettingsActivity.class);
        BtnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundEffectHelper.getInstance().playSound(SoundEffectHelper.SOUND_SUCCESS);
                startActivity(settingsIntent);
            }
        });

        // mode selection button
        BtnPlay = (Button)findViewById(R.id.btnPlay);
        final Intent playIntent = new Intent(this, ModeSelectionActivity.class);
        BtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundEffectHelper.getInstance().playSound(SoundEffectHelper.SOUND_SUCCESS);
                startActivity(playIntent);
            }
        });

        // available packs

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
//            case R.id.action_settings:
//                final Intent settingsIntent = new Intent(this, SettingsActivity.class);
//                startActivity(settingsIntent);
//                break;
            default:
                break;
        }

        return true;
    }
}
