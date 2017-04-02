package com.hamom.yandexschool.ui.fragments.translation;

import android.support.annotation.Nullable;
import android.util.Log;
import com.hamom.yandexschool.data.managers.DataManager;
import com.hamom.yandexschool.data.local.models.Translation;
import com.hamom.yandexschool.data.network.responce.LangsRes;
import com.hamom.yandexschool.di.scopes.TranslationScope;
import com.hamom.yandexschool.mvp_contract.TranslationContract;
import com.hamom.yandexschool.utils.AppConfig;
import com.hamom.yandexschool.utils.ConstantManager;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
  private Map<String, String> mLangs;

  public TranslationPresenter(DataManager dataManager) {
    // TODO: 27.03.17 remove this
    mToLang = "ru";
    mDataManager = dataManager;
    fillLangs();
  }

  public void takeView(TranslationContract.TranslationView view) {
    mView = view;
  }

  public void dropView() {
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

  /**
   * get translation for given word from local or network
   * @param text text to translate
   */
  @Override
  public void translate(String text) {

    if (checkNetwork()) return;

    mDataManager.translate(text.trim(), mToLang, getTranslateCallback());
  }

  private void fillLangs(){
    String sysLang = Locale.getDefault().getDisplayLanguage();
    String ui = sysLang.equals("русский") ? "ru" : "en";
    mDataManager.getLangs(ui, getLangsCallback());
  }



  private boolean checkNetwork() {
    if (!getView().isNetworkAvailable()){
      if (hasView()){
        getView().showNoNetworkMessage();
      }
      return true;
    }
    return false;
  }

  /**
   * make callback to receive map of languages
   * @return
   */
  private DataManager.ReqCallback<LangsRes> getLangsCallback() {
    return new DataManager.ReqCallback<LangsRes>() {
      @Override
      public void onSuccess(LangsRes res) {
        if (AppConfig.DEBUG) Log.d(TAG, "onSuccess: " + res.getLangs());

        mLangs = res.getLangs();
      }

      @Override
      public void onFailure(Throwable e) {
        if (hasView()){
          getView().showMessage(e.getMessage());
        }
      }
    };
  }

  /**
   * make callback to receive translation and update view
   * @return
   */
  private DataManager.ReqCallback<Translation> getTranslateCallback(){
    return new DataManager.ReqCallback<Translation>() {
      @Override
      public void onSuccess(Translation translation) {
        if (AppConfig.DEBUG) Log.d(TAG, "onSuccess: ");

        if (hasView()){
          getView().updateTranslation(translation.getTranslations());
        }
      }

      @Override
      public void onFailure(Throwable e) {
        if (AppConfig.DEBUG) Log.d(TAG, "onFailure: ");
        if (hasView()){
          getView().showMessage(e.getMessage());
        }
      }
    };
  }
}
