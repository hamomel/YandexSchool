package com.hamom.yandexschool.data.managers;

import com.hamom.yandexschool.data.local.database.DbManager;
import com.hamom.yandexschool.data.network.RestService;
import com.hamom.yandexschool.data.network.responce.TranslateRes;
import com.hamom.yandexschool.data.local.models.Translation;
import com.hamom.yandexschool.utils.errors.ApiError;
import com.hamom.yandexschool.utils.AppConfig;
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

  private RestService mRestService;
  private DbManager mDbManager;

  @Inject
  public DataManager(RestService restService, DbManager dbManager) {
    mDbManager = dbManager;
    mRestService = restService;
  }

  public void translate(final String text, final String lang, final ReqCallback<Translation> callback){
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

  public interface ReqCallback<R> {
    void onSuccess(R res);
    void onFailure(Throwable e);
  }
}