package com.hamom.yandexschool.di.modules;

import com.hamom.yandexschool.data.managers.DataManager;
import com.hamom.yandexschool.di.scopes.HistoryScope;
import com.hamom.yandexschool.mvp_contract.HistoryContract;
import com.hamom.yandexschool.ui.fragments.history.HistoryPresenter;
import dagger.Module;
import dagger.Provides;

/**
 * Created by hamom on 07.04.17.
 */
@Module
public class HistoryModule {

  @Provides
  @HistoryScope
  HistoryContract.Presenter provideHistoryPresenter(DataManager dataManager) {
    return new HistoryPresenter(dataManager);
  }
}
