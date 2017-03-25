package com.hamom.yandexschool.ui.fragments.translation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.hamom.yandexschool.R;

/**
 * Created by hamom on 25.03.17.
 */

public class TranslationFragment extends Fragment {

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_translation, container, false);
    return v;
  }
}
