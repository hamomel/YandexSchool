package com.hamom.yandexschool.data.network;

import com.hamom.yandexschool.data.network.responce.LangsRes;
import com.hamom.yandexschool.data.network.responce.TranslateRes;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by hamom on 25.03.17.
 */

public interface RestService {

  @POST("translate")
  Call<TranslateRes> translate(@Query("key") String key,
      @Query("text") String text, @Query("lang") String lang);

  @POST("getLangs")
  Call<LangsRes> getLangs(@Query("ui") String ui, @Query("key") String key);

}
