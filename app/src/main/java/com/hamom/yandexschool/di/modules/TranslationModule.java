package com.hamom.yandexschool.di.modules;

import com.hamom.yandexschool.di.scopes.TranslationScope;
import com.hamom.yandexschool.mvp_contract.TranslationContract;
import com.hamom.yandexschool.ui.fragments.translation.TranslationPresenter;
import dagger.Module;
import dagger.Provides;

/**
 * Created by hamom on 25.03.17.
 */
@Module
public class TranslationModule {
  @Provides
  @TranslationScope
  TranslationContract.TranslationPresenter provideTranslationPresenter() {
    return new TranslationPresenter();
  }
}
