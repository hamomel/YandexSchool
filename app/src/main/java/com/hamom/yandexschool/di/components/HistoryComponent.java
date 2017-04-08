package com.hamom.yandexschool.di.components;

import com.hamom.yandexschool.di.modules.HistoryModule;
import com.hamom.yandexschool.di.scopes.HistoryScope;
import com.hamom.yandexschool.ui.fragments.history.HistoryFragment;
import dagger.Subcomponent;

/**
 * Created by hamom on 07.04.17.
 */
@HistoryScope
@Subcomponent(modules = HistoryModule.class)
public interface HistoryComponent {
  void inject(HistoryFragment historyFragment);
}
