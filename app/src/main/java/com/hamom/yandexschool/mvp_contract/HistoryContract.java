package com.hamom.yandexschool.mvp_contract;

import com.hamom.yandexschool.data.local.models.Translation;
import java.util.List;
import okhttp3.ResponseBody;

/**
 * Created by hamom on 07.04.17.
 */

public interface HistoryContract {

  interface HistoryView extends IView{

    void initView(List<Translation> history);

    void setTranslationFragment(Translation translation);
  }

  interface HistoryPresenter{
    void takeView(HistoryView view);
    void dropView();

    void cleanHistory();

    void clickFavorite(Translation translation);

    void clickItem(Translation translation);
  }
}
