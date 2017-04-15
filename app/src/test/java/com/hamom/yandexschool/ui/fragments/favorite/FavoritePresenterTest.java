package com.hamom.yandexschool.ui.fragments.favorite;

import com.hamom.yandexschool.data.local.models.Translation;
import com.hamom.yandexschool.data.managers.DataManager;
import com.hamom.yandexschool.mvp_contract.FavoriteContract;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by hamom on 15.04.17.
 */
public class FavoritePresenterTest {
  @Mock
  FavoriteContract.View mockView;
  @Mock
  DataManager mockDataManager;

  @Captor
  private ArgumentCaptor<DataManager.ReqCallback> mReqCallbackArgumentCaptor;

  private FavoritePresenter mPresenter;
  private Translation mTranslation;


  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    mPresenter = new FavoritePresenter(mockDataManager);
    mTranslation = new Translation("anyString", "anyString");
  }

  @SuppressWarnings("unchecked")
  @Test
  public void takeView_Test() throws Exception {
    List<Translation> history = new ArrayList<>();
    Map<String, String> langs = new HashMap<>();
    history.add(mTranslation);
    when(mockView.isNetworkAvailable()).thenReturn(true);

    mPresenter.takeView(mockView);

    verify(mockDataManager, times(1)).getLangs(anyString(), mReqCallbackArgumentCaptor.capture());
    mReqCallbackArgumentCaptor.getValue().onSuccess(langs);
    verify(mockDataManager, times(1)).getFavoriteHistory(mReqCallbackArgumentCaptor.capture());
    mReqCallbackArgumentCaptor.getValue().onSuccess(history);
    verify(mockView, times(1)).initView(history, langs);
  }

  @Test
  public void clickFavorite() throws Exception {
    mPresenter.clickFavorite(mTranslation);
    verify(mockDataManager, times(1)).updateTranslation(mTranslation);
  }

  @Test
  public void clickItem() throws Exception {
    mPresenter.takeView(mockView);
    mPresenter.clickItem(mTranslation);
    verify(mockView, times(1)).setTranslationFragment(mTranslation);
  }
}