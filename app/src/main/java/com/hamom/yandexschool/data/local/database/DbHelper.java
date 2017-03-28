package com.hamom.yandexschool.data.local.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import javax.inject.Inject;

/**
 * Created by hamom on 28.03.17.
 */

public class DbHelper extends SQLiteOpenHelper {
  private static final String  DB_NAME = "Translation.db";
  private static final int DB_VERSION = 1;

  private static final String TEXT_TYPE = " TEXT";

  private static final String INTEGER_TYPE = " INTEGER";

  private static final String COMMA_SEP = ",";

  private static final String SQL_CREATE_TRANSLATE_ENTRIES = "CREATE TABLE " +
      TranslateDbContract.TransEntry.TABLE_NAME + " (" +
      TranslateDbContract.TransEntry.COLUMN_NAME_WORD + TEXT_TYPE + COMMA_SEP +
      TranslateDbContract.TransEntry.COLUMN_NAME_DIRECTION + TEXT_TYPE + COMMA_SEP +
      TranslateDbContract.TransEntry.COLUMN_NAME_TIME + INTEGER_TYPE +
      TranslateDbContract.TransEntry.COLUMN_NAME_FAVORITE + INTEGER_TYPE +
      " )";

  private static final String SQL_CREATE_WORD_ENTRIES = "CREATE TABLE " +
      TranslateDbContract.WordEntry.TABLE_NAME + " (" +
      TranslateDbContract.WordEntry.COLUMN_NAME_WORD + TEXT_TYPE + COMMA_SEP +
      TranslateDbContract.WordEntry.COLUMN_NAME_TRANSLATION_ID + INTEGER_TYPE + COMMA_SEP +
      " FOREIGN KEY(" + TranslateDbContract.WordEntry.COLUMN_NAME_TRANSLATION_ID + ")" +
      " REFERENCES " + TranslateDbContract.TransEntry.TABLE_NAME + "(" +
      TranslateDbContract.TransEntry.COLUMN_NAME_ID + ")" +
      " )";

  @Inject
  public DbHelper(Context context) {
    super(context, DB_NAME, null, DB_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(SQL_CREATE_TRANSLATE_ENTRIES);
    db.execSQL(SQL_CREATE_WORD_ENTRIES);
    //db.setForeignKeyConstraintsEnabled(true);
    }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

  }
}
