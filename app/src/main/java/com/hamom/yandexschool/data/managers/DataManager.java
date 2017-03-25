package com.hamom.yandexschool.data.managers;

import com.hamom.yandexschool.data.network.RestService;
import com.hamom.yandexschool.data.network.responce.TranslateRes;
import com.hamom.yandexschool.utils.ApiError;
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

  @Inject
  public DataManager(RestService restService) {
    mRestService = restService;
  }

  public void translate(String text, String lang, final ReqCallback callback){
    Call<TranslateRes> call = mRestService.translate(AppConfig.API_KEY, text, lang);
    call.enqueue(new Callback<TranslateRes>() {
      @Override
      public void onResponse(Call<TranslateRes> call, Response<TranslateRes> response) {
        if (response.code() == 200){
          callback.onSuccess(response.body());
        } else {
          callback.onFailure(new ApiError(response.code()));
        }
      }

      @Override
      public void onFailure(Call<TranslateRes> call, Throwable t) {
        callback.onFailure(t);
      }
    });
  }

  public interface ReqCallback {
    void onSuccess(Object res);
    void onFailure(Throwable e);
  }
}
