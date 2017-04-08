package com.hamom.yandexschool.data.local.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.hamom.yandexschool.data.local.models.Translation;
import com.hamom.yandexschool.data.local.models.WordTranslated;
import com.hamom.yandexschool.utils.AppConfig;
import com.hamom.yandexschool.utils.ConstantManager;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import com.hamom.yandexschool.data.local.database.TranslateDbContract.TransEntry;
import com.hamom.yandexschool.data.local.database.TranslateDbContract.WordEntry;

import static dagger.internal.Preconditions.checkNotNull;

/**
 * Created by hamom on 28.03.17.
 */

public class DbManager {
  private static String TAG = ConstantManager.TAG_PREFIX + "DbManager: ";
  private DbHelper mDbHelper;

  @Inject
  public DbManager(DbHelper dbHelper) {
    mDbHelper = dbHelper;
  }

  /**
   * save {@link Translation} to database, if it isn't exist in db
   * @param translation
   */
  public void saveTranslation(@NotNull Translation translation){
    if (AppConfig.DEBUG) Log.d(TAG, "saveTranslation: ");
    checkNotNull(translation);

    if (checkAlreadyExist(translation) != null){
      if (AppConfig.DEBUG) Log.d(TAG, "saveTranslation: exist");
      return;
    }

    SQLiteDatabase db = mDbHelper.getWritableDatabase();
    int favorite = (translation.isFavorite())? 1 : 0;
    ContentValues transValues = new ContentValues();
    transValues.put(TransEntry.COLUMN_NAME_WORD, translation.getWord());
    transValues.put(TransEntry.COLUMN_NAME_DIRECTION, translation.getDirection());
    transValues.put(TransEntry.COLUMN_NAME_FAVORITE, favorite);
    transValues.put(TransEntry.COLUMN_NAME_TIME, translation.getTime());

    long id = db.insert(TransEntry.TABLE_NAME, null, transValues);

    List<String> words = translation.getTranslations();
    for (String word : words) {
      ContentValues wordValues = new ContentValues();
      wordValues.put(WordEntry.COLUMN_NAME_WORD, word);
      wordValues.put(WordEntry.COLUMN_NAME_TRANSLATION_ID, id);
      db.insert(WordEntry.TABLE_NAME, null, wordValues);
    }
    db.close();
  }

  /**
   * check if {@link Translation} exist,  if so then update its {@link Translation#time}
   * @param translation
   * @return
   */
  public Translation checkAlreadyExist(Translation translation) {
    if (AppConfig.DEBUG) Log.d(TAG, "checkAlreadyExist: ");

    SQLiteDatabase readableDatabase = mDbHelper.getReadableDatabase();

    String[] projection = {
        TransEntry.COLUMN_NAME_ID,
        TransEntry.COLUMN_NAME_WORD,
        TransEntry.COLUMN_NAME_DIRECTION,
        TransEntry.COLUMN_NAME_FAVORITE,
        TransEntry.COLUMN_NAME_TIME
    };

    String selection = TransEntry.COLUMN_NAME_WORD + " LIKE ? AND " +
        TransEntry.COLUMN_NAME_DIRECTION + " LIKE ?";
    String[] selectionArgs = {translation.getWord(), translation.getDirection()};

    // get given Translation from DB
    Cursor c = readableDatabase.query(TransEntry.TABLE_NAME,
        projection, selection, selectionArgs, null, null, null);

    // if translation doesn't exist, return null
    if (c == null || !c.moveToFirst()){
      if (c != null) c.close();
      readableDatabase.close();
      return null;
    }

    c.moveToFirst();

    String id = c.getString(c.getColumnIndex(TransEntry.COLUMN_NAME_ID));
    long longId = Long.parseLong(id);
    int favorite = c.getInt(c.getColumnIndex(TransEntry.COLUMN_NAME_FAVORITE));

    translation.setTime(System.currentTimeMillis());
    translation.setId(longId);
    translation.setFavorite(favorite != 0);
    translation.setTranslations(getWords(longId));

    if (AppConfig.DEBUG) Log.d(TAG, "checkAlreadyExist: " + id + " " + translation.getTime());

    // update translation time
    updateTranslationTime(translation);

    c.close();
    readableDatabase.close();

    return translation;
  }

  private void updateTranslationTime(Translation translation) {
    String id = String.valueOf(translation.getId());
    SQLiteDatabase writableDb = mDbHelper.getWritableDatabase();

    ContentValues values = new ContentValues();
    values.put(TransEntry.COLUMN_NAME_TIME, translation.getTime());

    String sel = TransEntry.COLUMN_NAME_ID + " LIKE ?";
    String[] args = {id};
    writableDb.update(TransEntry.TABLE_NAME, values, sel, args);
    writableDb.close();
  }

  private List<String> getWords(long id){
    List<String> list = new ArrayList<>();
    SQLiteDatabase readableDatabase = mDbHelper.getReadableDatabase();

    String[] projection = {WordEntry.COLUMN_NAME_WORD};
    String selection = WordEntry.COLUMN_NAME_TRANSLATION_ID + " LIKE ?";
    String[] selectionArgs = {String.valueOf(id)};

    Cursor c = readableDatabase.query(WordEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

    // if cursor is empty, return empty list
    if (c == null || !c.moveToFirst()) {
      return list;
    }

    do {
      String word = c.getString(c.getColumnIndex(WordEntry.COLUMN_NAME_WORD));
      list.add(word);
    } while (c.moveToNext());

    c.close();
    return list;
  }

}
