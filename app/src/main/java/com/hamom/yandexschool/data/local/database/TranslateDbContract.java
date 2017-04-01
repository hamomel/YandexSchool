package com.hamom.yandexschool.data.local.database;

/**
 * Created by hamom on 28.03.17.
 */

final class TranslateDbContract {
  public TranslateDbContract() {}

  static abstract class TransEntry {
    static final String TABLE_NAME = "translation";
    static final String COLUMN_NAME_ID = "rowid";
    static final String COLUMN_NAME_WORD = "word";
    static final String COLUMN_NAME_DIRECTION = "direction";
    static final String COLUMN_NAME_TIME = "time";
    static final String COLUMN_NAME_FAVORITE = "favorite";
  }

  static abstract class WordEntry {
    static final String TABLE_NAME = "translated";
    static final String COLUMN_NAME_ID = "rowid";
    static final String COLUMN_NAME_WORD = "word";
    static final String COLUMN_NAME_TRANSLATION_ID = "translationId";
  }
}
