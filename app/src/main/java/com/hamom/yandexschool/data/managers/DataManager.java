package com.hamom.yandexschool.data.managers;

import com.hamom.yandexschool.data.local.database.DbManager;
import com.hamom.yandexschool.data.network.RestService;
import com.hamom.yandexschool.data.network.responce.LangsRes;
import com.hamom.yandexschool.data.network.responce.TranslateRes;
import com.hamom.yandexschool.data.local.models.Translation;
import com.hamom.yandexschool.utils.ConstantManager;
import com.hamom.yandexschool.utils.errors.ApiError;
import com.hamom.yandexschool.utils.AppConfig;
import java.util.List;
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

  @Inject
  public DataManager(RestService restService, DbManager dbManager, AppPreferencesManager preferencesManager) {
    mDbManager = dbManager;
    mRestService = restService;
    mAppPreferencesManager = preferencesManager;

  }

  public void translate(final String text, final String lang, final ReqCallback<Translation> callback){

    Translation translation = new Translation(text, lang);

    if (mDbManager.checkAlreadyExist(translation) != null){
      callback.onSuccess(translation);
      return;
    }

    Call<TranslateRes> call = mRestService.translate(AppConfig.API_KEY, text, lang);
    call.enqueue(new Callback<TranslateRes>() {
      @Override
      public void onResponse(Call<TranslateRes> call, Response<TranslateRes> response) {
        if (response.code() == 200){
          Translation translation = new Translation(text,
              response.body().getText(),
              lang,
              System.currentTimeMillis());

          mDbManager.saveTranslation(translation);

          callback.onSuccess(translation);
        } else {
          if (response.code() != 400){
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

  public void getLangs(String ui, final ReqCallback<LangsRes> callback){
    Call<LangsRes> call = mRestService.getLangs(ui, AppConfig.API_KEY);
    call.enqueue(new Callback<LangsRes>() {
      @Override
      public void onResponse(Call<LangsRes> call, Response<LangsRes> response) {
        if (response.code() == 200){
          callback.onSuccess(response.body());
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

  public void saveLastLangs(String from, String to){
    mAppPreferencesManager.saveLastLangs(from, to);
  }

  public String[] getLastlangs(){
    return mAppPreferencesManager.getLastFromLang();
  }

  public void getAllHistory(ReqCallback<List<Translation>> callback){
    List<Translation> history = mDbManager.getAllHistory();
    callback.onSuccess(history);
  }

  public void updateTranslation(Translation translation) {
    mDbManager.updateTranslation(translation);
  }

  public interface ReqCallback<R> {
    void onSuccess(R res);
    void onFailure(Throwable e);
  }
}
