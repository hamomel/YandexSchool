package com.hamom.yandexschool.data.network;

import com.hamom.yandexschool.data.network.responce.TranslateRes;
import com.hamom.yandexschool.di.components.AppComponent;
import com.hamom.yandexschool.utils.AppConfig;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by hamom on 25.03.17.
 */

public interface RestService {

  @POST("translate")
  Call<TranslateRes> translate(@Query("key") String key,
      @Query("text") String text, @Query("lang") String lang);
}
