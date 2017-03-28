package com.hamom.yandexschool.ui.fragments.translation;

import com.hamom.yandexschool.data.managers.DataManager;
import com.hamom.yandexschool.data.network.responce.TranslateRes;
import com.hamom.yandexschool.mvp_contract.TranslationContract;
import com.hamom.yandexschool.utils.NetworkStatusChecker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
  TranslationContract.TranslationView mView;

  private TranslationContract.TranslationPresenter mPresenter;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    mPresenter = new TranslationPresenter(mDataManager);
  }

  @After
  public void tearDown() throws Exception {

  }

  @Test
  public void translate_INTERNET_AVAILABLE() throws Exception {
    when(mView.isNetworkAvailable()).thenReturn(true);
    mPresenter.takeView(mView);
    mPresenter.translate("anyString");
    verify(mDataManager, times(1)).translate(anyString(), anyString(), any(DataManager.ReqCallback.class));
  }

  @Test
  public void translate_INTERNET_UNAVAILABLE() throws Exception {
    when(mView.isNetworkAvailable()).thenReturn(false);
    mPresenter.takeView(mView);
    mPresenter.translate("anyString");
    verify(mDataManager, never()).translate(anyString(), anyString(), any(DataManager.ReqCallback.class));
    verify(mView, times(1)).showNoNetworkMessage();
  }

}