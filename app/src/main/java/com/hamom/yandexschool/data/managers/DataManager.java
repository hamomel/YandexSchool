package com.hamom.yandexschool.data.managers;

import android.view.MenuItem;
import com.hamom.yandexschool.data.local.database.DbManager;
import com.hamom.yandexschool.data.local.models.Translation;
import com.hamom.yandexschool.data.network.RestService;
import com.hamom.yandexschool.data.network.responce.LangsRes;
import com.hamom.yandexschool.data.network.responce.TranslateRes;
import com.hamom.yandexschool.utils.AppConfig;
import com.hamom.yandexschool.utils.ConstantManager;
import com.hamom.yandexschool.utils.errors.ApiError;
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

  @Inject
  public DataManager(RestService restService, DbManager dbManager,
      AppPreferencesManager preferencesManager) {
    mDbManager = dbManager;
    mRestService = restService;
    mAppPreferencesManager = preferencesManager;
    mExecutor = Executors.newSingleThreadExecutor();
  }

  //region===================== Translation ==========================
  public void translate(final String text, final String lang,
      final ReqCallback<Translation> callback) {

    mExecutor.submit(new Runnable() {
      @Override
      public void run() {
        Translation translation = new Translation(text, lang);

        if (mDbManager.getTranslationFromDb(translation) != null) {
          translation.setTime(System.currentTimeMillis());
          mDbManager.updateTranslation(translation);
          callback.onSuccess(translation);
          return;
        }

        Call<TranslateRes> call = mRestService.translate(AppConfig.API_KEY, text, lang);
        call.enqueue(new Callback<TranslateRes>() {
          @Override
          public void onResponse(Call<TranslateRes> call, Response<TranslateRes> response) {
            if (response.code() == 200) {
              Translation translation =
                  new Translation(text, response.body().getText(), lang, System.currentTimeMillis());

              mDbManager.saveTranslation(translation);
              callback.onSuccess(translation);
            } else {
              if (response.code() != 400) {
                callback.onFailure(new ApiError(response.code()));
              }
            }
          }

          @Override
          public void onFailure(Call<TranslateRes> call, Throwable t) {
            callback.onFailure(t);
          }
        });
      }
    });
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

    mExecutor.submit(new Runnable() {
      @Override
      public void run() {
        if (checkLangsOutdated(callback)) return;

        Call<LangsRes> call = mRestService.getLangs(uiLanguage, AppConfig.API_KEY);
        call.enqueue(new Callback<LangsRes>() {
          @Override
          public void onResponse(Call<LangsRes> call, Response<LangsRes> response) {
            if (response.code() == 200) {
              Map<String, String> langs = response.body().getLangs();
              mDbManager.saveLangs(langs);
              mAppPreferencesManager.saveLangsUpdateTime(System.currentTimeMillis());
              callback.onSuccess(langs);
            } else {
              callback.onFailure(new ApiError(response.code()));
            }
          }

          @Override
          public void onFailure(Call<LangsRes> call, Throwable t) {
            callback.onFailure(t);
          }
        });
      }
    });
  }

  private boolean checkLangsOutdated(ReqCallback<Map<String, String>> callback) {
    long sinceUpdate = System.currentTimeMillis() - mAppPreferencesManager.getLangsUpdateTime();
    if (AppConfig.LANGS_UPDATE_INTERVAL > sinceUpdate) {
      Map<String, String> langs = mDbManager.getLangs();
      if (langs != null && !langs.isEmpty()) {
        callback.onSuccess(langs);
        return true;
      }
    }
    return false;
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
    mExecutor.submit(new Runnable() {
      @Override
      public void run() {
        List<Translation> history = mDbManager.getAllHistory();
        callback.onSuccess(history);
      }
    });

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
    mExecutor.submit(new Runnable() {
      @Override
      public void run() {
        List<Translation> favorites = mDbManager.getFavoriteHistory();
        callback.onSuccess(favorites);
      }
    });
  }
  //endregion

  public interface ReqCallback<R> {
    void onSuccess(R res);

    void onFailure(Throwable e);
  }


}
