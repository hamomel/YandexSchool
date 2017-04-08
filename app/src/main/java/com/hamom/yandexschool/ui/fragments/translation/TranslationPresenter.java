package com.hamom.yandexschool.ui.fragments.translation;

import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import com.hamom.yandexschool.data.managers.DataManager;
import com.hamom.yandexschool.data.local.models.Translation;
import com.hamom.yandexschool.data.network.responce.LangsRes;
import com.hamom.yandexschool.di.scopes.TranslationScope;
import com.hamom.yandexschool.mvp_contract.AbstractPresenter;
import com.hamom.yandexschool.mvp_contract.TranslationContract;
import com.hamom.yandexschool.utils.AppConfig;
import com.hamom.yandexschool.utils.ConstantManager;
import com.hamom.yandexschool.utils.NetworkStatusChecker;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by hamom on 25.03.17.
 */
@TranslationScope
public class TranslationPresenter extends AbstractPresenter<TranslationContract.TranslationView>
    implements TranslationContract.TranslationPresenter {
  private static String TAG = ConstantManager.TAG_PREFIX + "TransPres: ";

  private Map<String, String> mLangs;

  public TranslationPresenter(DataManager dataManager) {
    super(dataManager);
    mLangs = new HashMap<>();
    fillLangs();
  }

  public void takeView(TranslationContract.TranslationView view) {
    super.takeView(view);
    if (!mView.hasLangs()) {
      if (!mLangs.isEmpty()) {
        setViewLangs();
      }
    }
  }

  public void dropView() {
    super.dropView();
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
  public void translate(String text, String from, String to) {
    if (checkNetwork()) return;

    String direction = mLangs.get(from) + "-" + mLangs.get(to);
    if (AppConfig.DEBUG) Log.d(TAG, "translate: " + direction + " " + text);

    mDataManager.translate(text.trim(), direction, getTranslateCallback());
  }

  @Override
  public List<String> getLangs() {
    List<String> list = new ArrayList<>();
    if (mLangs != null) {
      list.addAll(mLangs.keySet());
    }
    sortAlphabeticaly(list);
    return list;
  }

  @Override
  public void saveLastLangs(String langFrom, String langTo) {
    mDataManager.saveLastLangs(langFrom, langTo);
  }

  private void sortAlphabeticaly(List<String> list) {
    Collections.sort(list, new Comparator<String>() {
      @Override
      public int compare(String text1, String text2) {
        return text1.compareToIgnoreCase(text2);
      }
    });
  }

  private void fillLangs() {
    if (checkNetwork()) return;
    String sysLang = Locale.getDefault().getDisplayLanguage();
    String ui = sysLang.equals("русский") ? "ru" : "en";
    mDataManager.getLangs(ui, getLangsCallback());
  }

  private boolean checkNetwork() {
    if (hasView()) {
      if (!getView().isNetworkAvailable()) {
        getView().showNoNetworkMessage();
        return true;
      }
    }
    return false;
  }

  private void setViewLangs() {
    if (hasView()) {
      getView().setLangs(getLangs());
      getView().setLastLangs(mDataManager.getLastlangs());
    }
  }

  /**
   * make callback to receive map of languages
   */
  private DataManager.ReqCallback<LangsRes> getLangsCallback() {
    return new DataManager.ReqCallback<LangsRes>() {
      @Override
      public void onSuccess(LangsRes res) {
        if (AppConfig.DEBUG) Log.d(TAG, "onSuccess: " + res.getLangs());
        Set<Map.Entry<String, String>> set = res.getLangs().entrySet();
        for (Map.Entry<String, String> entry : set) {
          mLangs.put(entry.getValue(), entry.getKey());
        }

        setViewLangs();
      }

      @Override
      public void onFailure(Throwable e) {
        if (hasView()) {
          getView().showMessage(e.getMessage());
        }
      }
    };
  }

  /**
   * make callback to receive translation and update view
   */
  private DataManager.ReqCallback<Translation> getTranslateCallback() {
    return new DataManager.ReqCallback<Translation>() {
      @Override
      public void onSuccess(Translation translation) {
        if (AppConfig.DEBUG) Log.d(TAG, "onSuccess: ");

        if (hasView()) {
          getView().updateTranslation(translation.getTranslations());
        }
      }

      @Override
      public void onFailure(Throwable e) {
        if (AppConfig.DEBUG) Log.d(TAG, "onFailure: ");
        if (hasView()) {
          getView().showMessage(e.getMessage());
        }
      }
    };
  }
}
