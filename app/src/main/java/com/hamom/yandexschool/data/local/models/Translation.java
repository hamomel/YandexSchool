package com.hamom.yandexschool.data.local.models;

import java.util.List;

/**
 * Created by hamom on 28.03.17.
 */

public class Translation {

  private long id;
  private String word;
  private List<String> translations;
  private String direction;
  private boolean favorite = false;
  private long time;

  public Translation(String word, List<String> translations, String direction, long time) {
    this.word = word;
    this.translations = translations;
    this.direction = direction;
    this.time = time;
  }

  public Translation(long id, String word, List<String> translations, String direction, long time, boolean favorite) {
    this.id = id;
    this.word = word;
    this.translations = translations;
    this.direction = direction;
    this.time = time;
    this.favorite = favorite;
  }

  public long getId() {
    return id;
  }

  public String getWord() {
    return word;
  }

  public List<String> getTranslations() {
    return translations;
  }

  public String getDirection() {
    return direction;
  }

  public long getTime() {
    return time;
  }

  public boolean isFavorite() {
    return favorite;
  }

  public void changeFavorite() {
    favorite = !favorite;
  }
}
