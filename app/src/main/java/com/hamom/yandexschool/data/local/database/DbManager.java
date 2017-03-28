package com.hamom.yandexschool.data.local.database;

import android.content.ContentValues;
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

  public void saveTranslation(@NotNull Translation translation){
    if (AppConfig.DEBUG) Log.d(TAG, "saveTranslation: ");

    checkNotNull(translation);
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
}
