package com.hamom.yandexschool.ui.fragments.translation;

import com.hamom.yandexschool.data.managers.DataManager;
import com.hamom.yandexschool.data.network.responce.LangsRes;
import com.hamom.yandexschool.mvp_contract.TranslationContract;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by hamom on 27.03.17.
 */
public class TranslationPresenterTest {

  @Mock
  DataManager mDataManager;

  @Mock
  TranslationContract.View mView;

  private TranslationContract.Presenter mPresenter;
  private Map<String, String> langs = new HashMap<>();

  @SuppressWarnings("unchecked")
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    langs.put("ru", "Russian");
    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        ((DataManager.ReqCallback) invocation.getArguments()[1]).onSuccess(langs);
        return null;
      }
    }).when(mDataManager).getLangs(anyString(), any(DataManager.ReqCallback.class));

    mPresenter = new TranslationPresenter(mDataManager);
  }

  @Test
  public void translate_INTERNET_AVAILABLE() throws Exception {
    when(mView.isNetworkAvailable()).thenReturn(true);
    mPresenter.takeView(mView);
    mPresenter.translate("text", "ru", "anyString");
    verify(mDataManager, times(1)).translate(anyString(), anyString(), any(DataManager.ReqCallback.class));
  }

  @Test
  public void translate_INTERNET_UNAVAILABLE() throws Exception {
    when(mView.isNetworkAvailable()).thenReturn(false);
    mPresenter.takeView(mView);
    mPresenter.translate("text", "ru", "anyString");
    verify(mDataManager, never()).translate(anyString(), anyString(), any(DataManager.ReqCallback.class));
    verify(mView, times(1)).showNoNetworkMessage();
  }

  @Test
  public void getLangs() throws Exception {
    String russian = "Russian";
    List<String> list = new ArrayList<>();
    list.add(russian);
    assertEquals(russian, mPresenter.getLangNames().get(0));
  }

  @Test
  public void saveLastLangs() throws Exception {
    mPresenter.saveLastLangs("anyString", "anyString");
    verify(mDataManager).saveLastLangs(anyString(), anyString());

  }

  @Test
  public void decodeLangsAndSet() throws Exception {
    String directoin = "ru-ru";
    String[] decoded = new String[]{"Russian", "Russian"};
    mPresenter.takeView(mView);
    mPresenter.decodeLangsAndSet(directoin);
    verify(mView, times(1)).setLastLangs(decoded);
  }

  @Test
  public void fetchLastLangs() throws Exception {
    String[] lastLangs = new String[]{"anyString", "anyString"};
    when(mDataManager.getLastLangs()).thenReturn(lastLangs);
    mPresenter.takeView(mView);
    mPresenter.fetchLastLangs();
    verify(mView, times(1)).setLastLangs(lastLangs);
  }

  @Test
  public void getLangNames() throws Exception {
    mPresenter.takeView(mView);
    assertEquals("Russian", mPresenter.getLangNames().get(0));
  }

}