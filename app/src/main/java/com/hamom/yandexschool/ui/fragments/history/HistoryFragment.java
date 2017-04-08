package com.hamom.yandexschool.ui.fragments.history;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.hamom.yandexschool.R;
import com.hamom.yandexschool.di.modules.HistoryModule;
import com.hamom.yandexschool.mvp_contract.HistoryContract;
import com.hamom.yandexschool.utils.App;
import javax.inject.Inject;
import okhttp3.ResponseBody;

public class HistoryFragment extends Fragment implements HistoryContract.HistoryView {

  @Inject
  HistoryContract.HistoryPresenter mPresenter;
  @BindView(R.id.text_view_history)
  TextView textViewHistory;

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
    mPresenter.takeView(this);
    return v;
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

}
