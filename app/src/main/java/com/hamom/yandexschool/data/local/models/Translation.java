package com.hamom.yandexschool.data.local.models;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

/**
 * Created by hamom on 28.03.17.
 */

public class Translation implements Parcelable{

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

  public Translation(String word, String direction) {
    this.word = word;
    this.direction = direction;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Translation that = (Translation) o;

    if (word != null ? !word.equals(that.word) : that.word != null) return false;
    return direction != null ? direction.equals(that.direction) : that.direction == null;
  }

  @Override
  public int hashCode() {
    int result = word != null ? word.hashCode() : 0;
    result = 31 * result + (direction != null ? direction.hashCode() : 0);
    return result;
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

  public void setTime(long time) {
    this.time = time;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setWord(String word) {
    this.word = word;
  }

  public void setTranslations(List<String> translations) {
    this.translations = translations;
  }

  public void setDirection(String direction) {
    this.direction = direction;
  }

  public void setFavorite(boolean favorite) {
    this.favorite = favorite;
  }

  //region===================== Parcelable ==========================
  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(id);
    dest.writeString(word);
    dest.writeStringList(translations);
    dest.writeString(direction);
    dest.writeByte((byte) (favorite ? 1 : 0));
    dest.writeLong(time);
  }

  protected Translation(Parcel in) {
    id = in.readLong();
    word = in.readString();
    translations = in.createStringArrayList();
    direction = in.readString();
    favorite = in.readByte() != 0;
    time = in.readLong();
  }

  public static final Creator<Translation> CREATOR = new Creator<Translation>() {
    @Override
    public Translation createFromParcel(Parcel in) {
      return new Translation(in);
    }

    @Override
    public Translation[] newArray(int size) {
      return new Translation[size];
    }
  };
  //endregion

}
