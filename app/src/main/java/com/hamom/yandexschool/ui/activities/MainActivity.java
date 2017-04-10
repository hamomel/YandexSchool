package com.hamom.yandexschool.ui.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.ButterKnife;
import com.hamom.yandexschool.R;
import com.hamom.yandexschool.ui.fragments.history.HistoryFragment;
import com.hamom.yandexschool.ui.fragments.translation.TranslationFragment;
import com.hamom.yandexschool.utils.AppConfig;
import com.hamom.yandexschool.utils.ConstantManager;
import com.hamom.yandexschool.utils.MenuItemHolder;
import java.util.List;

public class MainActivity extends AppCompatActivity {
  private static String TAG = ConstantManager.TAG_PREFIX + "MainActivity: ";
  private FragmentManager mFragmentManager;
  private List<MenuItemHolder> mMenuItems;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    mFragmentManager = getSupportFragmentManager();
    if (savedInstanceState == null){
      mFragmentManager.beginTransaction().add(R.id.main_frame, new TranslationFragment()).commit();
    }

    BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
    navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
  }

  public void setMenuItems(List<MenuItemHolder> items){
    if (AppConfig.DEBUG) Log.d(TAG, "setMenuItems: ");

    mMenuItems = items;
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    invalidateOptionsMenu();
    if (mMenuItems != null && !mMenuItems.isEmpty()){
      for (MenuItemHolder menuItem : mMenuItems) {
        MenuItem item = menu.add(menuItem.getItemTitle());
        item.setIcon(menuItem.getItemResId());
        item.setShowAsActionFlags(menuItem.getShowAsAction());
        item.setOnMenuItemClickListener(menuItem.getListener());
      }
    } else {
      menu.clear();
    }
    return super.onPrepareOptionsMenu(menu);
  }

  //region===================== bottom navigation ==========================
  private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =
      new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
          Fragment fragment = getCurrentFragment();
          switch (item.getItemId()) {
            case R.id.navigation_translate:
              if (AppConfig.DEBUG) Log.d(TAG, "onNavigationItemSelected: translate");

              if (!(fragment instanceof TranslationFragment)){
                fragment = new TranslationFragment();
              }
              break;
            case R.id.navigation_favorite:
              break;
            case R.id.navigation_history:
              if (AppConfig.DEBUG) Log.d(TAG, "onNavigationItemSelected: history");

              if (!(fragment instanceof HistoryFragment)){
                fragment = new HistoryFragment();
              }
              break;
          }
          setFragment(fragment, false);
          return true;
        }
      };
  //endregion

  private void setFragment(Fragment fragment, boolean addToBackStack){
    mFragmentManager.beginTransaction().replace(R.id.main_frame, fragment).commit();
  }

  private Fragment getCurrentFragment(){
    return mFragmentManager.findFragmentById(R.id.main_frame);
  }
  public Boolean isNetworkAvailable(){
    ConnectivityManager cm = (ConnectivityManager) getSystemService(
        Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
    return networkInfo != null && networkInfo.isConnectedOrConnecting();
  }
}
