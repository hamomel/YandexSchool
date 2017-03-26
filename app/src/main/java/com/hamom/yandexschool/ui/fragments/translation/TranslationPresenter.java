package com.hamom.yandexschool.ui.fragments.translation;

import android.net.Network;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import com.hamom.yandexschool.data.managers.DataManager;
import com.hamom.yandexschool.data.network.responce.TranslateRes;
import com.hamom.yandexschool.di.scopes.TranslationScope;
import com.hamom.yandexschool.mvp_contract.TranslationContract;
import com.hamom.yandexschool.utils.AppConfig;
import com.hamom.yandexschool.utils.ConstantManager;
import com.hamom.yandexschool.utils.NetworkStatusChecker;
import javax.inject.Inject;

/**
 * Created by hamom on 25.03.17.
 */
@TranslationScope
public class TranslationPresenter implements
    TranslationContract.TranslationPresenter<TranslationContract.TranslationView> {
  private static String TAG = ConstantManager.TAG_PREFIX + "TransPresenter: ";

  private TranslationContract.TranslationView mView;
  private DataManager mDataManager;
  private String mToLang;

  public TranslationPresenter(DataManager dataManager) {
    mDataManager = dataManager;
  }

  public void takeView(TranslationContract.TranslationView view) {
    Log.d(TAG, "takeView: " + hashCode());
    mView = view;
  }

  public void dropView() {
    Log.d(TAG, "dropView: " + hashCode());
    mView = null;
  }

  @Nullable
  @Override
  public TranslationContract.TranslationView getView() {
    return mView;
  }

  public boolean hasView() {
    return mView != null;
  }

  @Override
  public void translate(String text) {
    if (AppConfig.DEBUG) Log.d(TAG, "translate: " + text);

    if (!NetworkStatusChecker.isNetworkAvailable()){
      if (hasView()){
        getView().showNoNetworkMessage();
      }
      return;
    }

    // TODO: 27.03.17 remove this
    mToLang = "ru";

    mDataManager.translate(text, mToLang, new DataManager.ReqCallback() {
      @Override
      public void onSuccess(Object res) {
        if (AppConfig.DEBUG) Log.d(TAG, "onSuccess: ");

        if (hasView()){
          getView().updateTranslation(((TranslateRes) res).getText());
        }
      }

      @Override
      public void onFailure(Throwable e) {
        if (AppConfig.DEBUG) Log.d(TAG, "onFailure: ");
        if (hasView()){
          getView().showMessage(e.getMessage());
        }
      }
    });
  }
}
