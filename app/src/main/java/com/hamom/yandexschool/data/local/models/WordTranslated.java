package com.hamom.yandexschool.data.local.models;

/**
 * Created by hamom on 28.03.17.
 */

public class WordTranslated {
  long id;
  String word;

  public WordTranslated(long id, String word) {
    this.id = id;
    this.word = word;
  }

  public long getId() {
    return id;
  }

  public String getWord() {
    return word;
  }
}
