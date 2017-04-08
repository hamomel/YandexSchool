package com.hamom.yandexschool.ui.fragments.history;

import android.util.Log;
import com.hamom.yandexschool.data.local.models.Translation;
import com.hamom.yandexschool.data.managers.DataManager;
import com.hamom.yandexschool.di.scopes.HistoryScope;
import com.hamom.yandexschool.mvp_contract.AbstractPresenter;
import com.hamom.yandexschool.mvp_contract.HistoryContract;
import com.hamom.yandexschool.utils.AppConfig;
import com.hamom.yandexschool.utils.ConstantManager;
import java.util.List;

/**
 * Created by hamom on 07.04.17.
 */
@HistoryScope
public class HistoryPresenterImpl extends AbstractPresenter<HistoryContract.HistoryView>
    implements HistoryContract.HistoryPresenter {
  private static String TAG = ConstantManager.TAG_PREFIX + "HistoryPres: ";

  public HistoryPresenterImpl(DataManager dataManager) {
    super(dataManager);
  }

  @Override
  public void takeView(HistoryContract.HistoryView view) {
    super.takeView(view);
    if (AppConfig.DEBUG) Log.d(TAG, "takeView: ");

    getHistory();
  }

  private void getHistory(){
    mDataManager.getAllHistory(getHistoryCallback());
  }

  private DataManager.ReqCallback<List<Translation>> getHistoryCallback(){
    return new DataManager.ReqCallback<List<Translation>>() {
      @Override
      public void onSuccess(List<Translation> res) {
        if (hasView()) getView().initView(res);
      }

      @Override
      public void onFailure(Throwable e) {

      }
    };
  }
}
