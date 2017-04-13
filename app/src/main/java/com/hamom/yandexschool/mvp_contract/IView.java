package com.hamom.yandexschool.mvp_contract;

/**
 * Created by hamom on 25.03.17.
 */

public interface IView {
  boolean onBackPressed();

  boolean isNetworkAvailable();

  void showNoNetworkMessage();
}
