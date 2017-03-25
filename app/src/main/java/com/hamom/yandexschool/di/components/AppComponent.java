package com.hamom.yandexschool.di.components;

import com.hamom.yandexschool.di.modules.AppModule;
import com.hamom.yandexschool.di.modules.NetworkModule;
import com.hamom.yandexschool.di.modules.TranslationModule;
import dagger.Component;
import javax.inject.Singleton;

/**
 * Created by hamom on 25.03.17.
 */
@Singleton
@Component(modules = {AppModule.class, NetworkModule.class})
public interface AppComponent {
  TranslationComponent getTranslationComponent(TranslationModule translationModule);
}
