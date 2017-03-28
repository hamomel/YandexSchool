package com.hamom.yandexschool.data.local.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.hamom.yandexschool.data.local.models.Translation;
import com.hamom.yandexschool.utils.AppConfig;
import com.hamom.yandexschool.utils.ConstantManager;
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

    ContentValues transValues = new ContentValues();
    transValues.put(TransEntry.COLUMN_NAME_WORD, translation.getWord());
    transValues.put(TransEntry.COLUMN_NAME_DIRECTION, translation.getDirection());
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
  private Translation checkAlreadyExist(Translation translation) {
    if (AppConfig.DEBUG) Log.d(TAG, "checkAlreadyExist: ");

    SQLiteDatabase readableDatabase = mDbHelper.getReadableDatabase();

    String[] projection = {
        TransEntry.COLUMN_NAME_ID,
        TransEntry.COLUMN_NAME_WORD,
        TransEntry.COLUMN_NAME_DIRECTION
    };

    String selection = TransEntry.COLUMN_NAME_WORD + " LIKE ? AND " +
        TransEntry.COLUMN_NAME_DIRECTION + " LIKE ?";
    String[] selectionArgs = {translation.getWord(), translation.getDirection()};

    Cursor c = readableDatabase.query(TransEntry.TABLE_NAME,
        projection, selection, selectionArgs, null, null, null);

    if (c == null || !c.moveToFirst()){
      if (c != null) c.close();
      readableDatabase.close();
      return null;
    }

    translation.setTime(System.currentTimeMillis());

    c.moveToFirst();
    String id = c.getString(c.getColumnIndex(TransEntry.COLUMN_NAME_ID));

    if (AppConfig.DEBUG) Log.d(TAG, "checkAlreadyExist: " + id);

    SQLiteDatabase writableDb = mDbHelper.getWritableDatabase();

    ContentValues values = new ContentValues();
    values.put(TransEntry.COLUMN_NAME_TIME, translation.getTime());

    String sel = TransEntry.COLUMN_NAME_ID + " LIKE ?";
    String[] args = {id};
    writableDb.update(TransEntry.TABLE_NAME, values, sel, args);

    c.close();
    readableDatabase.close();
    writableDb.close();

    return translation;
  }
}
