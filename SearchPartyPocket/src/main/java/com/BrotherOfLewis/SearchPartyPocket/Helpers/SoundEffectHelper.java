package com.BrotherOfLewis.SearchPartyPocket.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.preference.PreferenceManager;

import com.BrotherOfLewis.SearchPartyPocket.R;
import com.BrotherOfLewis.SearchPartyPocket.SearchPartyPocketApp;
import com.BrotherOfLewis.SearchPartyPocket.SettingsActivity;

import java.util.HashMap;

/**
 * Created by pye on 9/2/13.
 */
public class SoundEffectHelper {
    private static final String TAG = SoundEffectHelper.class.toString();

    private static final SoundEffectHelper INSTANCE = new SoundEffectHelper();

    // Sound ID can't be 0 (zero)
    public static final int SOUND_SUCCESS = 1;
    public static final int SOUND_FAIL = 2;

    private SoundEffectHelper() {

    }

    public static SoundEffectHelper getInstance() {
        return INSTANCE;
    }

    private SoundPool soundPool;
    private HashMap<Integer, Integer> soundPoolMap;
    int priority = 1;
    int no_loop = 0;
    private int volume;
    float normal_playback_rate = 1f;

    private Context context;

    public void init(Context context) {
        this.context = context;
        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        soundPoolMap = new HashMap<Integer, Integer>();
        soundPoolMap.put(SOUND_SUCCESS, soundPool.load(context, R.raw.phaser_down_3, 1));
        soundPoolMap.put(SOUND_FAIL, soundPool.load(context, R.raw.phaser_up_2, 1));
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        volume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
    }

    public void playSound(int soundId) {
        if (getIsSoundEffectEnabled())
            soundPool.play(soundId, volume, volume, priority, no_loop, normal_playback_rate);
    }

    public boolean getIsSoundEffectEnabled() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(SearchPartyPocketApp.getAppContext());
        boolean rtn = sharedPref.getBoolean(SettingsActivity.KEY_PREF_GENERAL_SOUND_EFFECTS, true);
        return rtn;
    }
}
