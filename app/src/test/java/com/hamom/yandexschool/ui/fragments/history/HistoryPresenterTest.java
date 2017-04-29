package com.hamom.yandexschool.ui.fragments.history;

import com.hamom.yandexschool.data.local.models.Translation;
import com.hamom.yandexschool.data.managers.DataManager;
import com.hamom.yandexschool.mvp_contract.HistoryContract;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by hamom on 08.04.17.
 */
public class HistoryPresenterTest {

  @Mock
  private DataManager mockDataManager;

  @Mock
  private HistoryContract.View mockView;

  @Captor
  private ArgumentCaptor<DataManager.ReqCallback> mReqCallbackArgumentCaptor;

  private HistoryPresenter mPresenter;
  private Translation mTranslation;
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    mPresenter = new HistoryPresenter(mockDataManager);
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
    verify(mockDataManager, times(1)).getAllHistory(mReqCallbackArgumentCaptor.capture());
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

  @Test
  public void cleanHistory() throws Exception {
    mPresenter.cleanHistory();
    verify(mockDataManager, times(1)).deleteAllHistory();
  }

  @Test
  public void deleteMenuClickTest() throws Exception {
    List<Translation> items = new ArrayList<>();
    items.add(new Translation("anyString", "anyString"));
    when(mockView.getSelectedItems()).thenReturn(items);

    mPresenter.takeView(mockView);
    mPresenter.deleteMenuClick();

    verify(mockDataManager, atLeast(1)).deleteTranslation(any(Translation.class));
    verify(mockView, times(1)).deleteSelectedItems();
  }
}