package com.hamom.yandexschool.mvp_contract;

import com.hamom.yandexschool.data.local.models.Translation;
import java.util.List;

/**
 * Created by hamom on 25.03.17.
 */

public interface TranslationContract {

  interface View extends IView{

    void setTranslation(Translation translation);

    /**
     * shows given translation
     * @param text list of received translation
     */
    void updateTranslation(List<String> text);

    /**
     * shows given message in Toast
     * @param message message to show
     */
    void showMessage(String message);

    /**
     * shows to user that internet unavailable
     */
    void showNoNetworkMessage();

    boolean isNetworkAvailable();

    void initView(List<String> langs);

    void setLastLangs(String[] lastlangs);

    boolean hasLangs();
  }

  interface Presenter {

    /**
     * translate given text
     * @param text text to translate
     * @param from
     */
    void translate(String text, String from, String to);

    /**
     * returns list of languages from API
     * @return
     */
    List<String> getLangNames();

    /**
     * save last used pair of languages
     * @param langFrom
     * @param langTo
     */
    void saveLastLangs(String langFrom, String langTo);

    void takeView(View view);

    void dropView();

    /**
     * decode given direction and sets decoded to view
     * @param direction
     */
    void decodeLangsAndSet(String direction);

    void fetchLastLangs();
  }
}
