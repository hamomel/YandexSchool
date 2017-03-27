package com.hamom.yandexschool.data.managers;

import com.hamom.yandexschool.data.network.RestService;
import com.hamom.yandexschool.data.network.responce.TranslateRes;
import com.hamom.yandexschool.resourses.MockTranslateRes;
import com.hamom.yandexschool.utils.ApiError;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.*;

/**
 * Created by hamom on 27.03.17.
 */
public class DataManagerTest {

  private MockWebServer mMockWebServer;
  private DataManager mDataManager;
  private TranslateRes mTestRes;
  private Throwable mThrowable;

  @Before
  public void setUp() throws Exception {
    mMockWebServer = new MockWebServer();

    Retrofit retrofit = new Retrofit.Builder().baseUrl(mMockWebServer.url("").toString())
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    RestService restService = retrofit.create(RestService.class);
    mDataManager = new DataManager(restService);
  }

  @After
  public void tearDown() throws Exception {
    mMockWebServer.shutdown();
  }

  @Test
  public void translate_OK_200() throws Exception {
    final CountDownLatch lock = new CountDownLatch(1);

    MockResponse response = new MockResponse().setResponseCode(200)
        .setBody(MockTranslateRes.TRANSLATE_RES_200);

    mMockWebServer.enqueue(response);
    mDataManager.translate("взгляд", "en", new DataManager.ReqCallback<TranslateRes>() {
      @Override
      public void onSuccess(TranslateRes res) {
        mTestRes = res;
        lock.countDown();
      }

      @Override
      public void onFailure(Throwable e) {
        mThrowable = e;
      }
    });

    lock.await(1000, TimeUnit.MILLISECONDS);
    assertNotNull(mTestRes);
    assertEquals(200, mTestRes.getCode());
    assertNull(mThrowable);
  }

  @Test
  public void translate_FAIL_401() throws Exception {
    final CountDownLatch lock = new CountDownLatch(1);

    MockResponse response = new MockResponse().setResponseCode(401);

    mMockWebServer.enqueue(response);
    mDataManager.translate("взгляд", "en", new DataManager.ReqCallback<TranslateRes>() {
      @Override
      public void onSuccess(TranslateRes res) {
        mTestRes = res;
      }

      @Override
      public void onFailure(Throwable e) {
       mThrowable = e;
        lock.countDown();
      }
    });

    lock.await(1000, TimeUnit.MILLISECONDS);
    assertEquals("Не возможно получить ответ сервера. Код ошибки: 401", mThrowable.getMessage());
    assertNull(mTestRes);
  }

}