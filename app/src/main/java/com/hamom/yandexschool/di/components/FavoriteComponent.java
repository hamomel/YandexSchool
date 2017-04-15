package com.hamom.yandexschool.di.components;

import com.hamom.yandexschool.di.modules.FavoriteModule;
import com.hamom.yandexschool.di.scopes.FavoriteScope;
import com.hamom.yandexschool.ui.fragments.favorite.FavoriteFragment;
import dagger.Subcomponent;

/**
 * Created by hamom on 15.04.17.
 */
@Subcomponent(modules = FavoriteModule.class)
@FavoriteScope
public interface FavoriteComponent {
  void inject(FavoriteFragment favoriteFragment);
}
