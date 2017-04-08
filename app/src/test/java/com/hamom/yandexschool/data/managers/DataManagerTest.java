package com.hamom.yandexschool.data.managers;

import com.hamom.yandexschool.data.local.database.DbManager;
import com.hamom.yandexschool.data.local.models.Translation;
import com.hamom.yandexschool.data.network.RestService;
import com.hamom.yandexschool.resourses.MockTranslateRes;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by hamom on 27.03.17.
 */
public class DataManagerTest {

  private MockWebServer mMockWebServer;
  private DataManager mDataManager;

  private Translation mTestRes;
  private Throwable mThrowable;

  @Mock
  private DbManager mDbManager;
  @Mock
  private AppPreferencesManager mAppPreferencesManager;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    mMockWebServer = new MockWebServer();

    Retrofit retrofit = new Retrofit.Builder().baseUrl(mMockWebServer.url("").toString())
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    RestService restService = retrofit.create(RestService.class);
    mDataManager = new DataManager(restService, mDbManager, mAppPreferencesManager);
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
    mDataManager.translate("взгляд", "en", new DataManager.ReqCallback<Translation>() {
      @Override
      public void onSuccess(Translation res) {
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
    assertEquals("mock", mTestRes.getTranslations().get(0));
    assertNull(mThrowable);
  }

  @Test
  public void translate_FAIL_401() throws Exception {
    final CountDownLatch lock = new CountDownLatch(1);

    MockResponse response = new MockResponse().setResponseCode(401);

    mMockWebServer.enqueue(response);
    mDataManager.translate("взгляд", "en", new DataManager.ReqCallback<Translation>() {
      @Override
      public void onSuccess(Translation res) {
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

  @Test
  public void translate_ALREADY_EXIST() throws Exception {
    final CountDownLatch lock = new CountDownLatch(1);

    when(mDbManager.checkAlreadyExist(any(Translation.class))).thenReturn(new Translation("взгляд", "en"));
    mDataManager.translate("взгляд", "en", new DataManager.ReqCallback<Translation>() {
      @Override
      public void onSuccess(Translation res) {
        mTestRes = res;
        lock.countDown();
      }

      @Override
      public void onFailure(Throwable e) {
      }
    });

    lock.await(1000, TimeUnit.MILLISECONDS);
    assertEquals("взгляд", mTestRes.getWord());
  }

}