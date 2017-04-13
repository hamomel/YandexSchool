package com.hamom.yandexschool.data.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by hamom on 02.04.17.
 */
@Singleton
public class AppPreferencesManager {
  public static final String LAST_FROM_LANG_KEY = "LAST_FROM_LANG_KEY";
  public static final String LAST_TO_LANG_KEY = "LAST_TO_LANG_KEY";

  SharedPreferences mSharedPreferences;

  @Inject
  public AppPreferencesManager(Context context) {
    mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
  }

  public void saveLastLangs(String from, String to){
    SharedPreferences.Editor editor = mSharedPreferences.edit();
    editor.putString(LAST_FROM_LANG_KEY, from);
    editor.putString(LAST_TO_LANG_KEY, to);
    editor.apply();
  }

  public String[] getLastLangs(){
    String[] langs = new String[2];
    langs[0] = mSharedPreferences.getString(LAST_FROM_LANG_KEY, "");
    langs[1] = mSharedPreferences.getString(LAST_TO_LANG_KEY, "");
    return langs;
  }
}
