package com.hamom.yandexschool.mvp_contract;

import android.view.View;
import com.hamom.yandexschool.data.local.models.Translation;
import java.util.List;
import java.util.Map;

/**
 * Created by hamom on 15.04.17.
 */

public interface FavoriteContract {
  public interface View extends IView{
    void initView(List<Translation> history);

    void setTranslationFragment(Translation translation);

    void showMessage(String message);

    void setLangs(Map<String, String> langs);
  }

  interface Presenter{
    void takeView(View view);

    void dropView();

    void clickFavorite(Translation translation);

    void clickItem(Translation translation);
  }

}
