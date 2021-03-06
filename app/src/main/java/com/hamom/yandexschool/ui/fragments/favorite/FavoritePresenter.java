package com.hamom.yandexschool.ui.fragments.favorite;

import android.util.Log;
import com.hamom.yandexschool.data.local.models.Translation;
import com.hamom.yandexschool.data.managers.DataManager;
import com.hamom.yandexschool.data.network.responce.LangsRes;
import com.hamom.yandexschool.di.scopes.FavoriteScope;
import com.hamom.yandexschool.mvp_contract.AbstractPresenter;
import com.hamom.yandexschool.mvp_contract.FavoriteContract;
import com.hamom.yandexschool.utils.AppConfig;
import com.hamom.yandexschool.utils.ConstantManager;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by hamom on 15.04.17.
 */
@FavoriteScope
public class FavoritePresenter extends AbstractPresenter<FavoriteContract.View>
    implements FavoriteContract.Presenter{
  private static String TAG = ConstantManager.TAG_PREFIX + "FavPresenter: ";
  private Map<String, String> mLangs;

  public FavoritePresenter(DataManager dataManager) {
    super(dataManager);
  }

  @Override
  public void takeView(FavoriteContract.View view) {
    super.takeView(view);
    getLangs();
  }

  @Override
  public void clickFavorite(Translation translation) {
    mDataManager.updateTranslation(translation);
  }

  @Override
  public void clickItem(Translation translation) {
    mView.setTranslationFragment(translation);
  }

  private void getLangs() {
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

  /**
   * make callback to receive map of languages
   */
  private DataManager.ReqCallback<Map<String, String>> getLangsCallback() {
    if (AppConfig.DEBUG) Log.d(TAG, "getLangsCallback: ");

    return new DataManager.ReqCallback<Map<String, String>>() {
      @Override
      public void onSuccess(Map<String, String> res) {
        mLangs = res;
        getFavoriteHistory();
      }

      @Override
      public void onFailure(Throwable e) {
        if (hasView()) {
          getView().showMessage(e.getMessage());
        }
      }
    };
  }

  private void getFavoriteHistory(){
    mDataManager.getFavoriteHistory(getHistoryCallback());
  }

  private DataManager.ReqCallback<List<Translation>> getHistoryCallback(){
    return new DataManager.ReqCallback<List<Translation>>() {
      @Override
      public void onSuccess(List<Translation> res) {
        if (AppConfig.DEBUG) Log.d(TAG, "onSuccess: " + res);

        if (hasView()) getView().initView(res, mLangs);
      }

      @Override
      public void onFailure(Throwable e) {

      }
    };
  }
}
