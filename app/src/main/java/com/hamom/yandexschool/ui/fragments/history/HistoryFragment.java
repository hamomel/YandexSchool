package com.hamom.yandexschool.ui.fragments.history;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.hamom.yandexschool.R;
import com.hamom.yandexschool.data.local.models.Translation;
import com.hamom.yandexschool.di.modules.HistoryModule;
import com.hamom.yandexschool.mvp_contract.HistoryContract;
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

public class HistoryFragment extends Fragment implements HistoryContract.View {
  private static String TAG = ConstantManager.TAG_PREFIX + "HistoryFrag: ";
  @Inject
  HistoryContract.Presenter mPresenter;

  @BindView(R.id.toolbar)
  Toolbar toolbar;

  @BindView(R.id.history_recycler)
  RecyclerView historyRecycler;

  private HistoryAdapter mAdapter;
  private boolean mIsSelectionMode;

  @OnClick(R.id.yandex_tv)
  void onYandexClick(){
    getMainActivity().openYandexTranslate();
  }

  public HistoryFragment() {
    // Required empty public constructor
  }

  //region===================== LifeCycle ==========================
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
    if (mPresenter == null){
      App.getAppComponent().getHistoryComponent(new HistoryModule()).inject(this);
    }
  }

  @Override
  public android.view.View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    android.view.View v = inflater.inflate(R.layout.fragment_history, container, false);
    ButterKnife.bind(this, v);
    initRecycler();
    mPresenter.takeView(this);
    if (mIsSelectionMode){
      setSelectionModeToolbar();
    } else {
      setNormalToolbar();
    }
    return v;
  }

  @Override
  public void onDestroyView() {
    clearAppbarMenu();
    super.onDestroyView();
    mPresenter.dropView();
  }
  //endregion

  private void initRecycler() {
    if (mAdapter == null){
      mAdapter = new HistoryAdapter(getOnClickListener());
    }
    LinearLayoutManager manager = new LinearLayoutManager(getActivity());
    historyRecycler.setLayoutManager(manager);
    historyRecycler.setAdapter(mAdapter);
    mAdapter.notifyDataSetChanged();
  }

  private HistoryAdapter.HistoryClickListener getOnClickListener() {
    return new HistoryAdapter.HistoryClickListener() {
      @Override
      public void onClick(android.view.View v, Translation translation) {
        switch (v.getId()){
          case R.id.history_item:
            mPresenter.clickItem(translation);
            break;
          case R.id.favorite_iv:
            mPresenter.clickFavorite(translation);
            break;
        }
      }

      @Override
      public void onLongClick(android.view.View v, Translation translation) {
          mPresenter.onLongItemClick(translation);
      }
    };
  }

  //region===================== Toolbar ==========================
  @Override
  public void setNormalToolbar() {
    MainActivity activity = (getMainActivity());
    activity.setSupportActionBar(toolbar);
    ActionBar actionBar = activity.getSupportActionBar();
    if (actionBar != null) {
      actionBar.setTitle(activity.getString(R.string.history));
      actionBar.setDisplayHomeAsUpEnabled(false);
    }
    activity.setMenuItems(getNormalModeMenu());
    setNormalScrollFlags();
  }

  private void setNormalScrollFlags() {
    AppBarLayout.LayoutParams params = ((AppBarLayout.LayoutParams) toolbar.getLayoutParams());
    params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL |
        AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS |
    AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
  }

  private List<MenuItemHolder> getNormalModeMenu() {
    List<MenuItemHolder> items = new ArrayList<>();

    MenuItemHolder itemClear = new MenuItemHolder();
    itemClear.setItemTitle(getString(R.string.clear_history));
    itemClear.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
    itemClear.setListener(new MenuItem.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem item) {
        clearHistory();
        return true;
      }
    });
    items.add(itemClear);
    return items;
  }

  @Override
  public void setSelectionModeToolbar() {
    MainActivity activity = (getMainActivity());
    activity.setSupportActionBar(toolbar);
    ActionBar actionBar = activity.getSupportActionBar();
    if (actionBar != null){
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setTitle(String.valueOf(getSelectedItems().size()));
    }
    activity.setMenuItems(getSelectedModeMenu());
    setSelectionModeScrollFlags();
  }

  private void setSelectionModeScrollFlags() {
    AppBarLayout.LayoutParams params = ((AppBarLayout.LayoutParams) toolbar.getLayoutParams());
    params.setScrollFlags(0);
  }

  private List<MenuItemHolder> getSelectedModeMenu() {
    List<MenuItemHolder> items = new ArrayList<>();
    MenuItemHolder itemDelete = new MenuItemHolder();
    itemDelete.setItemTitle(getString(R.string.delete));
    itemDelete.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    itemDelete.setItemResId(R.drawable.ic_delete_black_24dp);
    itemDelete.setListener(new MenuItem.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem item) {
        deleteMenuClick();
        return true;
      }
    });
    items.add(itemDelete);
    return items;
  }

  @Override
  public void updateToolbarCounter() {
    MainActivity activity = getMainActivity();
    ActionBar actionBar = activity.getSupportActionBar();
    if (actionBar != null) {
      actionBar.setTitle(String.valueOf(getSelectedItems().size()));
    }
  }

  private void clearAppbarMenu() {
    getMainActivity().setMenuItems(null);
  }
  //endregion

  private void deleteMenuClick() {
    if (AppConfig.DEBUG) Log.d(TAG, "deleteMenuClick: ");

    mPresenter.deleteMenuClick();
  }

  private void clearHistory() {
    mPresenter.cleanHistory();
  }

  //region===================== Selection ==========================
  @Override
  public void setSelectionMode() {
    mIsSelectionMode = true;
    setSelectionModeToolbar();
  }

  @Override
  public void setNormalMode() {
    mIsSelectionMode = false;
    mAdapter.setNormalMode();
    setNormalToolbar();
  }

  @Override
  public boolean isInSelectionMode() {
    return mIsSelectionMode;
  }

  @Override
  public List<Translation> getSelectedItems(){
    return mAdapter.getSelectedItems();
  }

  @Override
  public void addSelection(Translation translation){
    mAdapter.addSelection(translation);
  }

  @Override
  public void deselectItem(Translation translation) {
    mAdapter.deSelectItem(translation);
  }

  @Override
  public void deleteSelectedItems() {
    mAdapter.deleteSelectedItems();
    setNormalMode();
  }

  //endregion

  @Override
  public boolean onBackPressed() {
    if (mIsSelectionMode){
      setNormalMode();
    } else {
      getMainActivity().selectTranslationNavigation();
    }
    return true;
  }

  @Override
  public void initView(List<Translation> history, Map<String, String> langs) {
    mAdapter.init(history, langs);
  }

  @Override
  public void setTranslationFragment(Translation translation) {
    MainActivity activity = getMainActivity();
    TranslationFragment fragment = TranslationFragment.newInstance(translation);
    activity.selectTranslationNavigation();
    activity.setFragment(fragment, false);
  }

  @Override
  public void showNoNetworkMessage() {
    Toast.makeText(getContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
  }

  @Override
  public void showMessage(String message) {
    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
  }

  @Override
  public boolean isNetworkAvailable() {
    return getMainActivity().isNetworkAvailable();
  }

  private MainActivity getMainActivity() {
    return (MainActivity) getActivity();
  }
}
