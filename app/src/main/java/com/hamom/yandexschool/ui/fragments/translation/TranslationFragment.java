package com.hamom.yandexschool.ui.fragments.translation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.hamom.yandexschool.R;
import com.hamom.yandexschool.di.modules.TranslationModule;
import com.hamom.yandexschool.mvp_contract.TranslationContract;
import com.hamom.yandexschool.utils.App;
import com.hamom.yandexschool.utils.AppConfig;
import com.hamom.yandexschool.utils.ConstantManager;
import javax.inject.Inject;

/**
 * Created by hamom on 25.03.17.
 */

public class TranslationFragment extends Fragment implements TranslationContract.TranslationView {
  private static String TAG = ConstantManager.TAG_PREFIX + "TransFragment: ";
  @Inject
  TranslationContract.TranslationPresenter mPresenter;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (AppConfig.DEBUG) Log.d(TAG, "onCreate: ");

    App.getAppComponent().getTranslationComponent(new TranslationModule()).inject(this);
  }

  @SuppressWarnings("unchecked")
  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    Log.d(TAG, "onCreateView: ");
    View v = inflater.inflate(R.layout.fragment_translation, container, false);
    mPresenter.takeView(this);
    return v;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (AppConfig.DEBUG) Log.d(TAG, "onDestroyView: ");

    mPresenter.dropView();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (AppConfig.DEBUG) Log.d(TAG, "onDestroy: ");
  }

  @Override
  public void onBackPressed() {

  }
}
