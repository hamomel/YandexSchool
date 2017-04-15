package com.hamom.yandexschool.di.modules;

import com.hamom.yandexschool.data.managers.DataManager;
import com.hamom.yandexschool.di.scopes.FavoriteScope;
import com.hamom.yandexschool.mvp_contract.FavoriteContract;
import com.hamom.yandexschool.ui.fragments.favorite.FavoritePresenter;
import dagger.Module;
import dagger.Provides;

/**
 * Created by hamom on 15.04.17.
 */
@Module
public class FavoriteModule {
  @Provides
  @FavoriteScope
  FavoriteContract.Presenter providePresenter(DataManager dataManager) {
    return new FavoritePresenter(dataManager);
  }
}
