package com.hamom.yandexschool.ui.fragments.history;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
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
import javax.inject.Inject;

public class HistoryFragment extends Fragment implements HistoryContract.HistoryView {
  private static String TAG = ConstantManager.TAG_PREFIX + "HistoryFrag: ";
  @Inject
  HistoryContract.HistoryPresenter mPresenter;

  @BindView(R.id.toolbar)
  Toolbar toolbar;

  @BindView(R.id.history_recycler)
  RecyclerView historyRecycler;

  private List<MenuItemHolder> mMenuItems;
  private HistoryAdapter mAdapter;

  public HistoryFragment() {
    // Required empty public constructor
  }

  //region===================== LifeCycle ==========================
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    App.getAppComponent().getHistoryComponent(new HistoryModule()).inject(this);
    initMenuItems();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_history, container, false);
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
    activity.getSupportActionBar().setTitle(activity.getString(R.string.history));
    activity.setMenuItems(mMenuItems);
  }

  private void initMenuItems() {
    mMenuItems = new ArrayList<>();

    MenuItemHolder itemClear = new MenuItemHolder();
    itemClear.setItemTitle(getString(R.string.clear_hostory));
    itemClear.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
    itemClear.setListener(new MenuItem.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem item) {
        clearHistory();
        return true;
      }
    });
    mMenuItems.add(itemClear);
  }

  private void clearAppbarMenu() {
    ((MainActivity) getActivity()).setMenuItems(null);
  }
  //endregion

  private void clearHistory() {
    mPresenter.cleanHistory();
  }

  private void initRecycler() {
    mAdapter = new HistoryAdapter(getOnClickListener());
    LinearLayoutManager manager = new LinearLayoutManager(getActivity());
    historyRecycler.setLayoutManager(manager);
    historyRecycler.setAdapter(mAdapter);
  }

  private HistoryAdapter.HistoryClickListener getOnClickListener() {
    return new HistoryAdapter.HistoryClickListener() {
      @Override
      public void onClick(View v, Translation translation) {
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
      public void onLongClick(View v, Translation translation) {
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
  public void initView(List<Translation> history) {
    if (AppConfig.DEBUG) Log.d(TAG, "initView: " + history);

    mAdapter.init(history);
  }

  @Override
  public void setTranslationFragment(Translation translation) {
    MainActivity activity = ((MainActivity) getActivity());
    TranslationFragment fragment = TranslationFragment.newInstance(translation);
    activity.selectTranslationNavigation();
    activity.setFragment(fragment, false);
  }
}
