package com.hamom.yandexschool.ui.fragments.history;

import com.hamom.yandexschool.data.local.models.Translation;
import com.hamom.yandexschool.data.managers.DataManager;
import com.hamom.yandexschool.mvp_contract.HistoryContract;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by hamom on 08.04.17.
 */
public class HistoryPresenterImplTest {

  @Mock
  private DataManager mockDataManager;

  @Mock
  private HistoryContract.HistoryView mockView;

  @Captor
  private ArgumentCaptor<DataManager.ReqCallback> mReqCallbackArgumentCaptor;

  private HistoryPresenterImpl mPresenter;
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    mPresenter = new HistoryPresenterImpl(mockDataManager);
  }

  @Test
  public void takeView_Test() throws Exception {
    List<Translation> history = new ArrayList<>();
    history.add(new Translation("anyString", "anyString"));

    mPresenter.takeView(mockView);

    verify(mockDataManager, times(1)).getAllHistory(mReqCallbackArgumentCaptor.capture());
    mReqCallbackArgumentCaptor.getValue().onSuccess(history);
    verify(mockView, times(1)).initView(history);
  }
}