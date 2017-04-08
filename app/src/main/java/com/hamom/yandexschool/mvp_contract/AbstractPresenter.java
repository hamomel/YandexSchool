package com.hamom.yandexschool.mvp_contract;

import android.support.annotation.Nullable;
import com.hamom.yandexschool.data.managers.DataManager;

/**
 * Created by hamom on 08.04.17.
 */

public class AbstractPresenter<V> implements IPresenter<V> {

  protected V mView;
  protected DataManager mDataManager;

  public AbstractPresenter(DataManager dataManager) {
    mDataManager = dataManager;
  }

  @Override
  public void takeView(V view) {
    mView = view;
  }

  @Override
  public void dropView() {
    mView =null;
  }

  @Nullable
  @Override
  public V getView() {
    return mView;
  }

  @Override
  public boolean hasView() {
    return mView != null;
  }
}
