package com.hamom.yandexschool.ui.fragments.history;

import android.util.Log;
import com.hamom.yandexschool.data.local.models.Translation;
import com.hamom.yandexschool.data.managers.DataManager;
import com.hamom.yandexschool.data.network.responce.LangsRes;
import com.hamom.yandexschool.di.scopes.HistoryScope;
import com.hamom.yandexschool.mvp_contract.AbstractPresenter;
import com.hamom.yandexschool.mvp_contract.HistoryContract;
import com.hamom.yandexschool.utils.AppConfig;
import com.hamom.yandexschool.utils.ConstantManager;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by hamom on 07.04.17.
 */
@HistoryScope
public class HistoryPresenter extends AbstractPresenter<HistoryContract.View>
    implements HistoryContract.Presenter {
  private static String TAG = ConstantManager.TAG_PREFIX + "HistoryPres: ";
  private Map<String, String> mLangs;
  public HistoryPresenter(DataManager dataManager) {
    super(dataManager);
  }

  @Override
  public void takeView(HistoryContract.View view) {
    super.takeView(view);
    if (AppConfig.DEBUG) Log.d(TAG, "takeView: ");
    getLangs();
  }

  @Override
  public void clickFavorite(Translation translation) {
    mDataManager.updateTranslation(translation);
  }

  @Override
  public void clickItem(Translation translation) {
    if (hasView()){
      if (getView().isInSelectionMode()){
        resolveSelection(translation);
      } else {
        mView.setTranslationFragment(translation);
      }
    }
  }

  private void resolveSelection(Translation translation) {
    if (getView().getSelectedItems().contains(translation)){
      getView().deselectItem(translation);
    } else {
      getView().addSelection(translation);
    }

    if (getView().getSelectedItems().size() < 1){
      getView().setNormalMode();
    } else {
      getView().updateToolbarCounter();
    }
  }

  @Override
  public void onLongItemClick(Translation translation) {
    if (hasView() && !getView().isInSelectionMode()){
      getView().addSelection(translation);
      getView().setSelectionMode();
    }
  }


  @Override
  public void deleteMenuClick() {
    if (hasView()) {
      deleteSelectedItemsFromDb(getView().getSelectedItems());
      getView().deleteSelectedItems();
    }
  }

  private void deleteSelectedItemsFromDb(List<Translation> selectedItems) {
    if (AppConfig.DEBUG) Log.d(TAG, "deleteSelectedItemsFromDb: " + selectedItems.size());

    for (Translation selectedItem : selectedItems) {
      mDataManager.deleteTranslation(selectedItem);
    }
  }

  @Override
  public void cleanHistory() {
    mDataManager.deleteAllHistory();
    getHistory();
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

  private void getHistory(){
    mDataManager.getAllHistory(getHistoryCallback());
  }

  private DataManager.ReqCallback<List<Translation>> getHistoryCallback(){
    return new DataManager.ReqCallback<List<Translation>>() {
      @Override
      public void onSuccess(List<Translation> res) {
        if (hasView()) getView().initView(res, mLangs);
      }

      @Override
      public void onFailure(Throwable e) {

      }
    };
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
        getHistory();
      }

      @Override
      public void onFailure(Throwable e) {
        if (hasView()) {
          getView().showMessage(e.getMessage());
        }
      }
    };
  }
}
