package com.hamom.yandexschool.mvp_contract;

import com.hamom.yandexschool.data.local.models.Translation;
import java.util.List;
import java.util.Map;

/**
 * Created by hamom on 07.04.17.
 */

public interface HistoryContract {

  interface View extends IView{

    void initView(List<Translation> history, Map<String, String> langs);

    void setTranslationFragment(Translation translation);

    void showMessage(String message);

  }

  interface Presenter {
    void takeView(View view);

    void dropView();

    void cleanHistory();

    void clickFavorite(Translation translation);

    void clickItem(Translation translation);
  }
}
