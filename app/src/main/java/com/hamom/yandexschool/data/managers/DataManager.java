package com.hamom.yandexschool.data.managers;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import com.hamom.yandexschool.data.local.database.DbManager;
import com.hamom.yandexschool.data.local.models.Translation;
import com.hamom.yandexschool.data.network.RestService;
import com.hamom.yandexschool.data.network.responce.LangsRes;
import com.hamom.yandexschool.data.network.responce.TranslateRes;
import com.hamom.yandexschool.utils.AppConfig;
import com.hamom.yandexschool.utils.ConstantManager;
import com.hamom.yandexschool.utils.errors.ApiError;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by hamom on 25.03.17.
 */
@Singleton
public class DataManager {
  private static String TAG = ConstantManager.TAG_PREFIX + "DataManager: ";
  private RestService mRestService;
  private DbManager mDbManager;
  private AppPreferencesManager mAppPreferencesManager;
  private Handler mWorkerHandler;
  private Handler mUIHandler;


  @Inject
  public DataManager(RestService restService, DbManager dbManager,
      AppPreferencesManager preferencesManager) {
    mDbManager = dbManager;
    mRestService = restService;
    mAppPreferencesManager = preferencesManager;
    mUIHandler = new Handler();
    HandlerThread thread = new HandlerThread("data_manager_worker");
    thread.start();
    Looper looper = thread.getLooper();
    mWorkerHandler = new Handler(looper);
  }

  //region===================== Translation ==========================
  public void translate(final String text, final String lang,
      final ReqCallback<Translation> callback) {

    mWorkerHandler.post(() -> {
      Translation translation = new Translation(text, lang);
      Throwable error = null;
      if (mDbManager.getTranslationFromDb(translation) != null) {
        translation.setTime(System.currentTimeMillis());
        mDbManager.updateTranslation(translation);
      } else {
        Call<TranslateRes> call = mRestService.translate(AppConfig.API_KEY, text, lang);
        try {
          Response<TranslateRes> response = call.execute();
          if (response.code() == 200) {
            translation = new Translation(text, response.body().getText(), lang,
                    System.currentTimeMillis());
            mDbManager.saveTranslation(translation);
          } else {
            error = new ApiError(response.code());
          }
        } catch (IOException e) {
          error = e;
        }
      }

      Translation finalTranslation = translation;
      Throwable finalError = error;
      mUIHandler.post(() -> {
        if (finalError == null) {
          callback.onSuccess(finalTranslation);
        } else {
          callback.onFailure(finalError);
        }
      });
    });
  }

  public void updateTranslation(final Translation translation) {
    mWorkerHandler.post(() -> mDbManager.updateTranslation(translation));
  }

  public void deleteTranslation(final Translation translation) {
    mWorkerHandler.post(() -> mDbManager.deleteTranslation(translation));
  }
  //endregion

  //region===================== Langs ==========================
  public void getLangs(final String uiLanguage, final ReqCallback<Map<String, String>> callback) {

    mWorkerHandler.post(() -> {
      Throwable error = null;
      Map<String, String> result = new HashMap<>();

      if (isLangsOutdated()) {
        Call<LangsRes> call = mRestService.getLangs(uiLanguage, AppConfig.API_KEY);
        try {
          Response<LangsRes> response = call.execute();
          if (response.code() == 200) {
            result = response.body().getLangs();
          } else {
            error = new ApiError(response.code());
          }
        } catch (IOException e) {
          error = e;
        }
      } else {
       result = mDbManager.getLangs();
      }

      Throwable finalError = error;
      Map<String, String> finalResult = result;
      mUIHandler.post(() -> {
        if (finalError != null) {
          callback.onFailure(finalError);
        } else {
          callback.onSuccess(finalResult);
        }
      });
    });
  }

  private boolean isLangsOutdated() {
    long timeFromUpdate = System.currentTimeMillis() - mAppPreferencesManager.getLangsUpdateTime();
    return timeFromUpdate > AppConfig.LANGS_UPDATE_INTERVAL;
  }

  public void saveLastLangs(String from, String to) {
    mAppPreferencesManager.saveLastLangs(from, to);
  }

  public String[] getLastLangs() {
    return mAppPreferencesManager.getLastLangs();
  }
  //endregion

  //region===================== History ==========================
  public void getAllHistory(final ReqCallback<List<Translation>> callback) {
    mWorkerHandler.post(() -> {
      List<Translation> translations = mDbManager.getAllHistory();
      mUIHandler.post(() -> callback.onSuccess(translations));
    });
  }

  public void deleteAllHistory() {
    mWorkerHandler.post(() -> mDbManager.deleteAllTranslations());
  }

  public void getFavoriteHistory(final ReqCallback<List<Translation>> callback) {
    mWorkerHandler.post(() -> {
      List<Translation> translations = mDbManager.getFavoriteHistory();
      mUIHandler.post(() -> callback.onSuccess(translations));
    });
  }
  //endregion

  public interface ReqCallback<R> {
    void onSuccess(R res);

    void onFailure(Throwable e);
  }
}
