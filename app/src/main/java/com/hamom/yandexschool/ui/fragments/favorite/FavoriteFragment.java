package com.hamom.yandexschool.ui.fragments.favorite;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.hamom.yandexschool.R;
import com.hamom.yandexschool.data.local.models.Translation;
import com.hamom.yandexschool.di.modules.FavoriteModule;
import com.hamom.yandexschool.mvp_contract.FavoriteContract;
import com.hamom.yandexschool.ui.activities.MainActivity;
import com.hamom.yandexschool.ui.fragments.translation.TranslationFragment;
import com.hamom.yandexschool.utils.App;
import com.hamom.yandexschool.utils.AppConfig;
import com.hamom.yandexschool.utils.ConstantManager;
import com.hamom.yandexschool.utils.MenuItemHolder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

/**
 * Created by hamom on 15.04.17.
 */

public class FavoriteFragment extends Fragment implements FavoriteContract.View{
  private static String TAG = ConstantManager.TAG_PREFIX + "FavoriteFrag: ";
  @Inject
  FavoriteContract.Presenter mPresenter;

  @BindView(R.id.toolbar)
  Toolbar toolbar;

  @BindView(R.id.history_recycler)
  RecyclerView historyRecycler;

  private List<MenuItemHolder> mMenuItems;
  private FavoriteAdapter mAdapter;

  @OnClick(R.id.yandex_tv)
  void onYandexClick(){
    ((MainActivity) getActivity()).openYandexTranslate();
  }
  //region===================== LifeCycle ==========================
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    App.getAppComponent().getFavoriteComponent(new FavoriteModule()).inject(this);
    initMenuItems();
  }

  @Override
  public android.view.View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    android.view.View v = inflater.inflate(R.layout.fragment_history, container, false);
    ButterKnife.bind(this, v);
    initToolbar();
    initRecycler();
    mPresenter.takeView(this);
    return v;
  }

  @Override
  public void onDestroyView() {
    clearAppbarMenu();
    super.onDestroyView();
    mPresenter.dropView();
  }
  //endregion

  //region===================== Toolbar ==========================
  private void initToolbar() {
    MainActivity activity = ((MainActivity) getActivity());
    activity.setSupportActionBar(toolbar);
    activity.getSupportActionBar().setTitle(activity.getString(R.string.favorite));
    activity.setMenuItems(mMenuItems);
  }

  private void initMenuItems() {
    mMenuItems = new ArrayList<>();
  }

  private void clearAppbarMenu() {
    ((MainActivity) getActivity()).setMenuItems(null);
  }
  //endregion

  private void initRecycler() {
    mAdapter = new FavoriteAdapter(getOnClickListener());
    LinearLayoutManager manager = new LinearLayoutManager(getContext());
    historyRecycler.setLayoutManager(manager);
    historyRecycler.setAdapter(mAdapter);
  }

  private FavoriteAdapter.FavoriteClickListener getOnClickListener() {
    return new FavoriteAdapter.FavoriteClickListener() {
      @Override
      public void onClick(android.view.View v, Translation translation) {
        switch (v.getId()){
          case R.id.history_item:
            clickItem(translation);
            break;
          case R.id.favorite_iv:
            clickFavorite(translation);
            break;
        }
      }

      @Override
      public void onLongClick(android.view.View v, Translation translation) {
        Toast.makeText(getContext(), "LongClick " + translation.getId(), Toast.LENGTH_SHORT).show();
      }
    };
  }

  private void clickFavorite(Translation translation) {
    mPresenter.clickFavorite(translation);
  }

  private void clickItem(Translation translation) {
    mPresenter.clickItem(translation);
  }

  @Override
  public boolean onBackPressed() {
    ((MainActivity) getActivity()).selectTranslationNavigation();
    return true;
  }

  @Override
  public void initView(List<Translation> history, Map<String, String> langs) {
    if (AppConfig.DEBUG) Log.d(TAG, "initView: " + history);

    mAdapter.init(history, langs);
  }

  @Override
  public void setTranslationFragment(Translation translation) {
    MainActivity activity = ((MainActivity) getActivity());
    TranslationFragment fragment = TranslationFragment.newInstance(translation);
    activity.selectTranslationNavigation();
    activity.setFragment(fragment, false);
  }

  @Override
  public void showNoNetworkMessage() {
    Toast.makeText(getContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
  }

  @Override
  public boolean isNetworkAvailable() {
    return ((MainActivity) getActivity()).isNetworkAvailable();
  }

  @Override
  public void showMessage(String message) {
    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
  }
}
