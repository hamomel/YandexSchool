package com.hamom.yandexschool.ui.fragments.translation;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.hamom.yandexschool.R;
import com.hamom.yandexschool.data.local.models.Translation;
import com.hamom.yandexschool.di.modules.TranslationModule;
import com.hamom.yandexschool.mvp_contract.TranslationContract;
import com.hamom.yandexschool.ui.activities.MainActivity;
import com.hamom.yandexschool.utils.App;
import com.hamom.yandexschool.utils.AppConfig;
import com.hamom.yandexschool.utils.ConstantManager;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.inject.Inject;

/**
 * Created by hamom on 25.03.17.
 */

public class TranslationFragment extends Fragment implements TranslationContract.View {
  private static String TAG = ConstantManager.TAG_PREFIX + "TransFrag: ";

  private static final String TRANSLATION_ARG_KEY = "TRANSLATION_ARG_KEY";

  @BindView(R.id.translation_tv)
  TextView translationTv;

  @BindView(R.id.user_input_et)
  EditText userInputEt;

  @BindView(R.id.toolbar)
  Toolbar toolbar;

  @BindView(R.id.spinner_from)
  Spinner spinnerFrom;

  @BindView(R.id.spinner_to)
  Spinner spinnerTo;


  @BindView(R.id.swap_language_iv)
  ImageButton changeLanguageIv;

  @Inject
  TranslationContract.Presenter mPresenter;

  private String mLangFrom;
  private String mLangTo;
  private List<String> mLangs;
  private Timer mTimer;

  //region===================== Events ==========================
    @OnClick(R.id.clear_button_ib)
    void onClearClick(){
      if (AppConfig.DEBUG) Log.d(TAG, "onClearClick: ");

      userInputEt.setText("");
    }

    @OnClick(R.id.user_input_layout)
    void clickOnUserInputLayout(){
      userInputEt.requestFocus();
      InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.showSoftInput(userInputEt, InputMethodManager.SHOW_IMPLICIT);
    }

  @OnClick(R.id.swap_language_iv)
    void onSwapClick(){

    String tmp = mLangFrom;
    mLangFrom = mLangTo;
    mLangTo = tmp;
    spinnerFrom.setSelection(mLangs.indexOf(mLangFrom));
    spinnerTo.setSelection(mLangs.indexOf(mLangTo));

    }

  @OnTextChanged(R.id.user_input_et)
  void onTextChanged(final CharSequence text){
    if (AppConfig.DEBUG) Log.d(TAG, "onUserInputChanged: " + text);

    if (!TextUtils.isEmpty(text)){
      translate();
    } else {
      translationTv.setText("");
    }
  }
  //endregion

  public static TranslationFragment newInstance(Translation translation) {
    TranslationFragment fragment = new TranslationFragment();
    Bundle bundle = new Bundle();
    bundle.putParcelable(TRANSLATION_ARG_KEY, translation);
    fragment.setArguments(bundle);
    return fragment;
  }

  //region===================== LifeCycle ==========================
  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
    if (AppConfig.DEBUG) Log.d(TAG, "onCreate: ");
    App.getAppComponent().getTranslationComponent(new TranslationModule()).inject(this);
  }

  @SuppressWarnings("unchecked")
  @Nullable
  @Override
  public android.view.View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    Log.d(TAG, "onCreateView: ");
    android.view.View v = inflater.inflate(R.layout.fragment_translation, container, false);
    ButterKnife.bind(this, v);
    mPresenter.takeView(this);
    initToolbar();
    return v;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mPresenter.saveLastLangs(mLangFrom, mLangTo);

  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (AppConfig.DEBUG) Log.d(TAG, "onDestroy: ");

    mPresenter.dropView();
  }
  //endregion

  private void translate(){
    if (mTimer != null) {
      mTimer.cancel();
    }
    if (!TextUtils.isEmpty(userInputEt.getText())){
      mTimer = new Timer();
      mTimer.schedule(new TimerTask() {

        @Override
        public void run() {
          if (getActivity() != null){
            getActivity().runOnUiThread(new Runnable() {
              @Override
              public void run() {
                mPresenter.translate(userInputEt.getText().toString(), mLangFrom, mLangTo);
              }
            });
          }
        }
      }, ConstantManager.TRANSLATION_DELAY_MILLIS);
    }
  }

  @Override
  public boolean onBackPressed() {
    return false;
  }

  @Override
  public void setTranslation(Translation translation) {
    userInputEt.setText(translation.getWord());

    List<String> translations = translation.getTranslations();
    StringBuilder sb = new StringBuilder();
    for (String s : translations) {
      sb.append(s).append("\n");
    }
    translationTv.setText(sb.toString());
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
    Toast.makeText(getContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
  }

  @Override
  public boolean isNetworkAvailable() {
    return ((MainActivity) getActivity()).isNetworkAvailable();
  }

  @Override
  public void initView(List<String> langs) {
    mLangs = langs;
    initFromSpinner(langs);
    initToSpinner(langs);
    if (getArguments() != null && getArguments().getParcelable(TRANSLATION_ARG_KEY) != null){
      Translation translation = getArguments().getParcelable(TRANSLATION_ARG_KEY);
      setTranslation(translation);
      mPresenter.decodeLangsAndSet(translation.getDirection());
    } else {
      if (AppConfig.DEBUG) Log.d(TAG, "initView: ");

      mPresenter.fetchLastLangs();
    }
  }

  @Override
  public void setLastLangs(String[] lastlangs) {
    if (AppConfig.DEBUG) Log.d(TAG, "setLastLangs: " + lastlangs[0] + " " + lastlangs[1]);

    if (!lastlangs[0].isEmpty()) spinnerFrom.setSelection(mLangs.indexOf(lastlangs[0]));
    if (!lastlangs[1].isEmpty()) spinnerTo.setSelection(mLangs.indexOf(lastlangs[1]));
  }

  @Override
  public boolean hasLangs() {
    return !(mLangs == null || !mLangs.isEmpty());
  }

  //region===================== Toolbar ==========================
  private void initToolbar() {
    AppCompatActivity activity = ((AppCompatActivity) getActivity());
    activity.setSupportActionBar(toolbar);
    activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
  }

  private void initToSpinner(List<String> langs) {
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
        android.R.layout.simple_spinner_item, langs);
    spinnerTo.setAdapter(adapter);
    spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
        mLangTo = mLangs.get(position);
        translate();
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });
  }

  private void initFromSpinner(List<String> langs) {
    if (AppConfig.DEBUG) Log.d(TAG, "initFromSpinner: ");

    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
        android.R.layout.simple_spinner_item, langs){
      // set gravity fot TextView in Spinner
      @NonNull
      @Override
      public android.view.View getView(int position, @Nullable android.view.View convertView, @NonNull ViewGroup parent) {
        android.view.View v = super.getView(position, convertView, parent);
        ((TextView) v).setGravity(Gravity.END);
        return v;
      }
    };
    spinnerFrom.setAdapter(adapter);
    spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
        mLangFrom = mLangs.get(position);
        translate();
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });
  }


  //endregion

}
