package com.hamom.yandexschool.mvp_contract;

import java.util.List;

/**
 * Created by hamom on 25.03.17.
 */

public interface TranslationContract {
  public interface TranslationView extends IView{

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

    void showNoNetworkMessage();

    boolean isNetworkAvailable();

    void setLangs(List<String> langs);

    void setLastLangs(String[] lastlangs);

    boolean hasLangs();
  }

  public interface TranslationPresenter<V> extends IPresenter<V> {

    /**
     * translate given text
     * @param text text to translate
     * @param from
     */
    void translate(String text, String from, String to);

    List<String> getLangs();

    void saveLastlangs(String langFrom, String langTo);
  }
}
