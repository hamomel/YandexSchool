package com.hamom.yandexschool.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.hamom.yandexschool.R;
import com.hamom.yandexschool.mvp_contract.IView;
import com.hamom.yandexschool.ui.fragments.favorite.FavoriteFragment;
import com.hamom.yandexschool.ui.fragments.history.HistoryFragment;
import com.hamom.yandexschool.ui.fragments.translation.TranslationFragment;
import com.hamom.yandexschool.utils.App;
import com.hamom.yandexschool.utils.AppConfig;
import com.hamom.yandexschool.utils.ConstantManager;
import com.hamom.yandexschool.utils.MenuItemHolder;
import java.util.List;

public class MainActivity extends AppCompatActivity {
  public static final int APP_CLOSE_INTERVAL = 2000;
  private static String TAG = ConstantManager.TAG_PREFIX + "MainActivity: ";
  private FragmentManager mFragmentManager;
  private List<MenuItemHolder> mMenuItems;

  @BindView(R.id.navigation)
  BottomNavigationView navigation;
  private long mBackPressed;

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

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home){
      onBackPressed();
    }
    return super.onOptionsItemSelected(item);
  }

  @SuppressWarnings("all")
  @Override
  public void onBackPressed() {
    if (!((IView) getCurrentFragment()).onBackPressed()){
      if ((System.currentTimeMillis() - mBackPressed) < APP_CLOSE_INTERVAL){
        super.onBackPressed();
      } else {
        Toast.makeText(this, getString(R.string.press_one_more_time_to_exit), APP_CLOSE_INTERVAL).show();
        mBackPressed = System.currentTimeMillis();
      }
    }
  }

  //region===================== bottom navigation ==========================
  private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =
      new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
          Fragment fragment = getCurrentFragment();
          switch (item.getItemId()) {
            case R.id.navigation_translate:
              fragment = new TranslationFragment();
              break;
            case R.id.navigation_favorite:
              fragment = new FavoriteFragment();
              break;
            case R.id.navigation_history:
              fragment = new HistoryFragment();
              break;
          }
          if (getCurrentFragment().getClass() != fragment.getClass()){
            setFragment(fragment, false);
            return true;
          }
          return false;
        }
      };

  public void selectTranslationNavigation(){
    navigation.setSelectedItemId(R.id.navigation_translate);
  }
  //endregion

  public void setFragment(Fragment fragment, boolean addToBackStack){
    Fragment oldFragment = getSupportFragmentManager().findFragmentByTag(fragment.getClass().getName());
    if (oldFragment != null) {
      Bundle args = fragment.getArguments();
      oldFragment.setArguments(args);
      fragment = oldFragment;
    }
    FragmentTransaction transaction = mFragmentManager.beginTransaction();
    transaction.replace(R.id.main_frame, fragment, fragment.getClass().getName());
    if (addToBackStack) transaction.addToBackStack(null);
    transaction.commit();
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

  /**
   * open Yandex official site
   */
  public void openYandexTranslate(){
    String uri = getString(R.string.yandex_url);
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setData(Uri.parse(uri));
    Intent newIntent = Intent.createChooser(intent, "");
    startActivity(newIntent);
  }
}
