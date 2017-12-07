package com.hamom.yandexschool.data.managers;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.view.MenuItem;
import com.hamom.yandexschool.data.local.database.DbManager;
import com.hamom.yandexschool.data.local.models.Translation;
import com.hamom.yandexschool.data.network.RestService;
import com.hamom.yandexschool.data.network.responce.LangsRes;
import com.hamom.yandexschool.data.network.responce.TranslateRes;
import com.hamom.yandexschool.utils.AppConfig;
import com.hamom.yandexschool.utils.ConstantManager;
import com.hamom.yandexschool.utils.errors.ApiError;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit2.Call;
import retrofit2.Callback;
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
  private ExecutorService mExecutor;
  private Handler mWorkerHandler;
  private Handler mUIHandler;


  @Inject
  public DataManager(RestService restService, DbManager dbManager,
      AppPreferencesManager preferencesManager) {
    mDbManager = dbManager;
    mRestService = restService;
    mAppPreferencesManager = preferencesManager;
    mExecutor = Executors.newSingleThreadExecutor();
    mUIHandler = new Handler();
    HandlerThread thread = new HandlerThread("data_manager_worker");
    thread.start();
    Looper looper = thread.getLooper();
    mWorkerHandler = new Handler(looper);
  }

  //region===================== Translation ==========================
  public void translate(final String text, final String lang,
      final ReqCallback<Translation> callback) {

    AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
      Translation translation = new Translation(text, lang);
      Throwable error;

      @Override
      protected Void doInBackground(Void... params) {
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
        return null;
      }

      @Override
      protected void onPostExecute(Void aVoid) {
        if (error == null) {
          callback.onSuccess(translation);
        } else {
          callback.onFailure(error);
        }
      }
    };

    asyncTask.execute();
  }

  public void updateTranslation(final Translation translation) {
    mExecutor.submit(new Runnable() {
      @Override
      public void run() {
        mDbManager.updateTranslation(translation);
      }
    });
  }

  public void deleteTranslation(final Translation translation) {
    mExecutor.submit(new Runnable() {
      @Override
      public void run() {
        mDbManager.deleteTranslation(translation);
      }
    });
  }
  //endregion

  //region===================== Langs ==========================
  public void getLangs(final String uiLanguage, final ReqCallback<Map<String, String>> callback) {

    AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
      Throwable error;
      Map<String, String> result;

      @Override
      protected Void doInBackground(Void... params) {
        if ((result = checkLangsOutdated()) != null) return null;

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
        return null;
      }

      @Override
      protected void onPostExecute(Void aVoid) {
        if (error != null) {
          callback.onFailure(error);
        } else {
          callback.onSuccess(result);
        }
      }
    };

    asyncTask.execute();
  }

  private Map<String, String> checkLangsOutdated() {
    long sinceUpdate = System.currentTimeMillis() - mAppPreferencesManager.getLangsUpdateTime();
    if (AppConfig.LANGS_UPDATE_INTERVAL > sinceUpdate) {
      return mDbManager.getLangs();
    }
    return null;
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
    AsyncTask<Void, Void, List<Translation>> asyncTask =
        new AsyncTask<Void, Void, List<Translation>>() {
          @Override
          protected List<Translation> doInBackground(Void... params) {
            return mDbManager.getAllHistory();
          }

          @Override
          protected void onPostExecute(List<Translation> translations) {
            callback.onSuccess(translations);
          }
        };

    asyncTask.execute();
  }

  public void deleteAllHistory() {
    mExecutor.submit(new Runnable() {
      @Override
      public void run() {
        mDbManager.deleteAllTranslations();
      }
    });
  }

  public void getFavoriteHistory(final ReqCallback<List<Translation>> callback) {

    AsyncTask<Void, Void, List<Translation>> asyncTask =
        new AsyncTask<Void, Void, List<Translation>>() {
          @Override
          protected List<Translation> doInBackground(Void... params) {
            return mDbManager.getFavoriteHistory();
          }

          @Override
          protected void onPostExecute(List<Translation> translations) {
            callback.onSuccess(translations);
          }
        };

    asyncTask.execute();
  }
  //endregion

  public interface ReqCallback<R> {
    void onSuccess(R res);

    void onFailure(Throwable e);
  }
}
