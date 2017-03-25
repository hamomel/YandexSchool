package com.hamom.yandexschool.di.components;

import com.hamom.yandexschool.di.modules.TranslationModule;
import com.hamom.yandexschool.di.scopes.TranslationScope;
import com.hamom.yandexschool.ui.fragments.translation.TranslationFragment;
import dagger.Subcomponent;

/**
 * Created by hamom on 25.03.17.
 */
@TranslationScope
@Subcomponent(modules = TranslationModule.class)
public interface TranslationComponent {

  void inject(TranslationFragment translationFragment);

}
