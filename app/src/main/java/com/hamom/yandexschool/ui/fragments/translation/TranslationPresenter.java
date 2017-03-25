package com.hamom.yandexschool.ui.fragments.translation;

import android.util.Log;
import com.hamom.yandexschool.di.scopes.TranslationScope;
import com.hamom.yandexschool.mvp_contract.TranslationContract;
import com.hamom.yandexschool.utils.ConstantManager;
import javax.inject.Inject;

/**
 * Created by hamom on 25.03.17.
 */
@TranslationScope
public class TranslationPresenter implements
    TranslationContract.TranslationPresenter<TranslationContract.TranslationView> {
  private static String TAG = ConstantManager.TAG_PREFIX + "TransPresenter: ";

  private TranslationContract.TranslationView mView;

  @Inject
  public TranslationPresenter() {
  }

  public void takeView(TranslationContract.TranslationView view) {
    Log.d(TAG, "takeView: " + hashCode());
    mView = view;
  }

  public void dropView() {
    Log.d(TAG, "dropView: " + hashCode());
    mView = null;
  }

  public boolean hasView() {
    return mView != null;
  }

}
