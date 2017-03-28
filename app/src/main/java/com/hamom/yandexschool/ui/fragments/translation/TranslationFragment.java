package com.hamom.yandexschool.ui.fragments.translation;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.hamom.yandexschool.R;
import com.hamom.yandexschool.di.modules.TranslationModule;
import com.hamom.yandexschool.mvp_contract.TranslationContract;
import com.hamom.yandexschool.ui.activities.MainActivity;
import com.hamom.yandexschool.utils.App;
import com.hamom.yandexschool.utils.AppConfig;
import com.hamom.yandexschool.utils.ConstantManager;
import java.util.List;
import javax.inject.Inject;

/**
 * Created by hamom on 25.03.17.
 */

public class TranslationFragment extends Fragment implements TranslationContract.TranslationView {
  private static String TAG = ConstantManager.TAG_PREFIX + "TransFrag: ";

  @BindView(R.id.translation_tv)
  TextView translationTv;

  @BindView(R.id.user_input_et)
  EditText userInputEt;

  private Handler mHandler;
  private Runnable mTranslateRunnable;

  //region===================== Events ==========================
    @OnClick(R.id.clear_button_ib)
    void onClearClick(){
      if (AppConfig.DEBUG) Log.d(TAG, "onClearClick: ");

      userInputEt.setText("");
    }

    @OnTextChanged(R.id.user_input_et)
    void onTextChanged(final CharSequence text){
      if (AppConfig.DEBUG) Log.d(TAG, "onUserInputChanged: ");

      mHandler.removeCallbacks(mTranslateRunnable);
      if (!TextUtils.isEmpty(text)){
        mHandler.postDelayed(mTranslateRunnable, 1000);
      } else {
        translationTv.setText("");
      }
    }
  //endregion

  @Inject
  TranslationContract.TranslationPresenter mPresenter;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (AppConfig.DEBUG) Log.d(TAG, "onCreate: ");
    createRunnable();
    App.getAppComponent().getTranslationComponent(new TranslationModule()).inject(this);
  }

  @SuppressWarnings("unchecked")
  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    Log.d(TAG, "onCreateView: ");
    View v = inflater.inflate(R.layout.fragment_translation, container, false);
    ButterKnife.bind(this, v);
    mPresenter.takeView(this);
    return v;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mPresenter.dropView();
  }

  private void createRunnable(){
    mHandler = new Handler();
    mTranslateRunnable = new Runnable() {
      @Override
      public void run() {
        mPresenter.translate(userInputEt.getText().toString());
      }
    };
  }

  @Override
  public boolean onBackPressed() {
    return false;
  }

  @Override
  public void updateTranslation(List<String> text) {
    StringBuilder sb = new StringBuilder();
    for (String s : text) {
      sb.append(s).append("\n");
    }
    translationTv.setText(sb.toString());
  }

  @Override
  public void showMessage(String message) {
    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
  }

  @Override
  public void showNoNetworkMessage() {
    Toast.makeText(getContext(), getString(R.string.no_internet_connectiviti), Toast.LENGTH_SHORT).show();
  }

  @Override
  public boolean isNetworkAvailable() {
    return ((MainActivity) getActivity()).isNetworkAvailable();
  }
}
