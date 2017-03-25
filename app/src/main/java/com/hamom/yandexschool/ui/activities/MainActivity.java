package com.hamom.yandexschool.ui.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;
import com.hamom.yandexschool.R;
import com.hamom.yandexschool.ui.fragments.translation.TranslationFragment;

public class MainActivity extends AppCompatActivity {





  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    FrameLayout mainFrame = ((FrameLayout) findViewById(R.id.main_frame));

    TranslationFragment fragment = new TranslationFragment();
    getSupportFragmentManager().beginTransaction().add(R.id.main_frame, fragment).commit();

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

}