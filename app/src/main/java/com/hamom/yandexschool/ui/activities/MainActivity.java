package com.hamom.yandexschool.ui.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.hamom.yandexschool.R;
import com.hamom.yandexschool.ui.fragments.translation.TranslationFragment;
import com.hamom.yandexschool.utils.App;
import com.hamom.yandexschool.utils.AppConfig;
import com.hamom.yandexschool.utils.ConstantManager;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
  private static String TAG = ConstantManager.TAG_PREFIX + "MainActivity: ";
  private FragmentManager mFragmentManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    //LayoutInflater inflater = ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE));
    //View view = inflater.inflate(R.layout.toolbar_main_view, toolbar);
    ////toolbar.addView(view);

    if (AppConfig.DEBUG) Log.d(TAG, "onCreate: " + Locale.getDefault().getDisplayLanguage());


    mFragmentManager = getSupportFragmentManager();
    if (savedInstanceState == null){
      mFragmentManager.beginTransaction().add(R.id.main_frame, new TranslationFragment()).commit();
    }

    BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
    navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
  }

  //region===================== bottom navigation ==========================
  private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =
      new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
          switch (item.getItemId()) {
            case R.id.navigation_translate:

              return true;
            case R.id.navigation_favorite:
              return true;
            case R.id.navigation_history:
              return true;
          }
          return false;
        }
      };
  //endregion

  public Boolean isNetworkAvailable(){
    ConnectivityManager cm = (ConnectivityManager) getSystemService(
        Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
    return networkInfo != null && networkInfo.isConnectedOrConnecting();
  }
}
