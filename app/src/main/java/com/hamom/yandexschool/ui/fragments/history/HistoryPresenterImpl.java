package com.hamom.yandexschool.ui.fragments.history;

import com.hamom.yandexschool.data.managers.DataManager;
import com.hamom.yandexschool.di.scopes.HistoryScope;
import com.hamom.yandexschool.mvp_contract.AbstractPresenter;
import com.hamom.yandexschool.mvp_contract.HistoryContract;

/**
 * Created by hamom on 07.04.17.
 */
@HistoryScope
public class HistoryPresenterImpl extends AbstractPresenter<HistoryContract.HistoryView>
    implements HistoryContract.HistoryPresenter {

  public HistoryPresenterImpl(DataManager dataManager) {
    super(dataManager);
  }


}
