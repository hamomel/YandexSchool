package com.hamom.yandexschool.data.managers;

import com.hamom.yandexschool.data.local.database.DbManager;
import com.hamom.yandexschool.data.local.models.Translation;
import com.hamom.yandexschool.data.network.RestService;
import com.hamom.yandexschool.resourses.MockLangsRes;
import com.hamom.yandexschool.resourses.MockTranslateRes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.SynchronousQueue;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by hamom on 27.03.17.
 */
public class DataManagerTest {

  private MockWebServer mMockWebServer;
  private DataManager mDataManager;

  private Translation mTransRes;
  private Map<String, String> mLangsRes;
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
        mTransRes = res;
        lock.countDown();
      }

      @Override
      public void onFailure(Throwable e) {
        mThrowable = e;
      }
    });

    lock.await(500, TimeUnit.MILLISECONDS);
    assertNotNull(mTransRes);
    assertEquals("mock", mTransRes.getTranslations().get(0));
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
        mTransRes = res;
      }

      @Override
      public void onFailure(Throwable e) {
       mThrowable = e;
        lock.countDown();
      }
    });

    lock.await(500, TimeUnit.MILLISECONDS);
    assertEquals("Не возможно получить ответ сервера. Код ошибки: 401", mThrowable.getMessage());
    assertNull(mTransRes);
  }

  @Test
  public void translate_ALREADY_EXIST() throws Exception {
    final CountDownLatch lock = new CountDownLatch(1);

    when(mDbManager.getTranslationFromDb(any(Translation.class))).thenReturn(new Translation("взгляд", "en"));
    mDataManager.translate("взгляд", "en", new DataManager.ReqCallback<Translation>() {
      @Override
      public void onSuccess(Translation res) {
        mTransRes = res;
        lock.countDown();
      }

      @Override
      public void onFailure(Throwable e) {
      }
    });

    lock.await(500, TimeUnit.MILLISECONDS);
    assertEquals("взгляд", mTransRes.getWord());
  }

  @Test
  public void getAllHistory() throws Exception {
    final List<Translation> history = new ArrayList<>();
    history.add(new Translation("anyString", "anyString"));
    final List<Translation> responce = new ArrayList<>();
    final CountDownLatch lock = new CountDownLatch(1);

    when(mDbManager.getAllHistory()).thenReturn(history);

    mDataManager.getAllHistory(new DataManager.ReqCallback<List<Translation>>() {
      @Override
      public void onSuccess(List<Translation> res) {
        responce.addAll(res);
       lock.countDown();
      }

      @Override
      public void onFailure(Throwable e) {
        mThrowable = e;
      }
    });

    lock.await(500, TimeUnit.MILLISECONDS);

    assertNotEquals(0, responce.size());
    assertEquals(history.get(0), responce.get(0));
    assertNull(mThrowable);

  }

  @Test
  public void getLangsTest_FROM_WEB() throws Exception {
    final CountDownLatch lock = new CountDownLatch(1);
    MockResponse response = new MockResponse().setResponseCode(200)
        .setBody(MockLangsRes.TRANSLATE_RES_200);

    mMockWebServer.enqueue(response);
    mDataManager.getLangs("ru", new DataManager.ReqCallback<Map<String, String>>() {
      @Override
      public void onSuccess(Map<String, String> res) {
        mLangsRes = res;
        lock.countDown();
      }

      @Override
      public void onFailure(Throwable e) {
        mThrowable = e;
      }
    });

    lock.await(500, TimeUnit.MILLISECONDS);
    assertEquals("Русский", mLangsRes.get("ru"));
    assertNull(mThrowable);
  }

  @Test
  public void getLangsTest_FROM_WEB_FAILURE() throws Exception {
    final CountDownLatch lock = new CountDownLatch(1);
    MockResponse response = new MockResponse().setResponseCode(401);

    mMockWebServer.enqueue(response);
    mDataManager.getLangs("ru", new DataManager.ReqCallback<Map<String, String>>() {
      @Override
      public void onSuccess(Map<String, String> res) {
        mLangsRes = res;
        lock.countDown();
      }

      @Override
      public void onFailure(Throwable e) {
        mThrowable = e;
      }
    });

    lock.await(500, TimeUnit.MILLISECONDS);
    assertEquals("Не возможно получить ответ сервера. Код ошибки: 401", mThrowable.getMessage());
  }

  @Test
  public void getLangs_FROM_DB() throws Exception {
    final CountDownLatch lock = new CountDownLatch(1);
    final Map<String, String> langs = new HashMap<>();
    langs.put("ru", "Русский");
    when(mAppPreferencesManager.getLangsUpdateTime()).thenReturn(System.currentTimeMillis());
    when(mDbManager.getLangs()).thenReturn(langs);

    mDataManager.getLangs("ru", new DataManager.ReqCallback<Map<String, String>>() {
      @Override
      public void onSuccess(Map<String, String> res) {
        mLangsRes = res;
        lock.countDown();
      }

      @Override
      public void onFailure(Throwable e) {
        mThrowable = e;
      }
    });

    lock.await(500, TimeUnit.MILLISECONDS);
    assertEquals(langs, mLangsRes);
    assertNull(mThrowable);
  }

  @Test
  public void saveLastLangs() throws Exception {
    String from = "from";
    String to = "to";
    mDataManager.saveLastLangs(from, to);
    verify(mAppPreferencesManager, times(1)).saveLastLangs(from, to);
  }

  @Test
  public void getLastLangs() throws Exception {
    String[] lastLangs = new String[]{"anyString", "anyString"};
    when(mAppPreferencesManager.getLastLangs()).thenReturn(lastLangs);
    assertArrayEquals(lastLangs, mDataManager.getLastLangs());
  }

  @Test
  public void updateTranslation() throws Exception {
    Translation translation = new Translation("anyString", "anyString");
    mDataManager.updateTranslation(translation);
    Thread.sleep(500);
    verify(mDbManager, times(1)).updateTranslation(translation);
  }

  @Test
  public void deleteTranslation() throws Exception {
    Translation translation = new Translation("anyString", "anyString");
    mDataManager.deleteTranslation(translation);
    Thread.sleep(500);
    verify(mDbManager, times(1)).deleteTranslation(translation);
  }


  @Test
  public void deleteAllHistory() throws Exception {
    mDataManager.deleteAllHistory();
    Thread.sleep(500);
    verify(mDbManager, times(1)).deleteAllTranslations();
  }

  @Test
  public void getFavoriteHistory() throws Exception {
    final List<Translation> history = new ArrayList<>();
    history.add(new Translation("anyString", "anyString"));
    final List<Translation> response = new ArrayList<>();
    final CountDownLatch lock = new CountDownLatch(1);

    when(mDbManager.getFavoriteHistory()).thenReturn(history);

    mDataManager.getFavoriteHistory(new DataManager.ReqCallback<List<Translation>>() {
      @Override
      public void onSuccess(List<Translation> res) {
        System.out.println("response :" + res.size());
        response.addAll(res);
        lock.countDown();
      }

      @Override
      public void onFailure(Throwable e) {
        mThrowable = e;
      }
    });

    lock.await(500, TimeUnit.MILLISECONDS);

    assertNotEquals(0, response.size());
    assertEquals(history.get(0), response.get(0));
    assertNull(mThrowable);
  }
}