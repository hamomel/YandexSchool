package com.hamom.yandexschool.mvp_contract;

import android.support.annotation.Nullable;

/**
 * Created by hamom on 25.03.17.
 */

public interface IPresenter<V> {
  void takeView(V view);
  void dropView();
  @Nullable V getView();
  boolean hasView();
}
