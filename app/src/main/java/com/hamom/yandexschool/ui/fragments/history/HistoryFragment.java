package com.hamom.yandexschool.ui.fragments.history;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.hamom.yandexschool.R;
import com.hamom.yandexschool.data.local.models.Translation;
import com.hamom.yandexschool.di.modules.HistoryModule;
import com.hamom.yandexschool.mvp_contract.HistoryContract;
import com.hamom.yandexschool.utils.App;
import com.hamom.yandexschool.utils.AppConfig;
import com.hamom.yandexschool.utils.ConstantManager;
import java.util.List;
import javax.inject.Inject;
import okhttp3.ResponseBody;

public class HistoryFragment extends Fragment implements HistoryContract.HistoryView {
  private static String TAG = ConstantManager.TAG_PREFIX + "HistoryFrag: ";
  @Inject
  HistoryContract.HistoryPresenter mPresenter;

  @BindView(R.id.history_recycler)
  RecyclerView historyRecycler;

  HistoryAdapter mAdapter;

  public HistoryFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    App.getAppComponent().getHistoryComponent(new HistoryModule()).inject(this);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_history, container, false);
    ButterKnife.bind(this, v);
    initRecycler();
    mPresenter.takeView(this);
    return v;
  }

  private void initRecycler() {
    mAdapter = new HistoryAdapter();
    LinearLayoutManager manager = new LinearLayoutManager(getActivity());
    historyRecycler.setLayoutManager(manager);
    historyRecycler.setAdapter(mAdapter);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mPresenter.dropView();
  }

  @Override
  public boolean onBackPressed() {
    return false;
  }

  @Override
  public void initView(List<Translation> history) {
    if (AppConfig.DEBUG) Log.d(TAG, "initView: " + history);

    mAdapter.init(history);
  }
}
