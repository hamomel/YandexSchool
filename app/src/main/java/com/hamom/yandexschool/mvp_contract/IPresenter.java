package com.hamom.yandexschool.mvp_contract;

/**
 * Created by hamom on 25.03.17.
 */

public interface IPresenter<V> {
  void takeView(V view);
  void dropView();
  boolean hasView();
}
