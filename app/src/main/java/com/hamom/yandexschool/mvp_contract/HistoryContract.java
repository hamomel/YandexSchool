package com.hamom.yandexschool.mvp_contract;

import okhttp3.ResponseBody;

/**
 * Created by hamom on 07.04.17.
 */

public interface HistoryContract {

  interface HistoryView extends IView{

  }

  interface HistoryPresenter{
    void takeView(HistoryView view);
    void dropView();
  }
}
