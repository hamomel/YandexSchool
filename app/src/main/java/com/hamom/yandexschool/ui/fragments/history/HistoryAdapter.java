package com.hamom.yandexschool.ui.fragments.history;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.hamom.yandexschool.R;
import com.hamom.yandexschool.data.local.models.Translation;
import com.hamom.yandexschool.utils.AppConfig;
import com.hamom.yandexschool.utils.ConstantManager;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hamom on 08.04.17.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
  private static String TAG = ConstantManager.TAG_PREFIX + "HistoryAdapt: ";
  private List<Translation> history = new ArrayList<>();
  private WeakReference<Context> mContextWeakReference;

  public void init(List<Translation> history){
    this.history = history;
    if (AppConfig.DEBUG) Log.d(TAG, "init: " + history);

    notifyDataSetChanged();
  }

  @Override
  public void onAttachedToRecyclerView(RecyclerView recyclerView) {
    super.onAttachedToRecyclerView(recyclerView);
    mContextWeakReference = new WeakReference<>(recyclerView.getContext());
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View v = inflater.inflate(R.layout.item_history, parent, false);
    return new ViewHolder(v);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    Translation translation = history.get(position);
    String translated = "";
    for (String s : translation.getTranslations()) {
      translated = translated.concat(s).concat("; ");
    }

    int iconId = translation.isFavorite() ? R.drawable.ic_favorite_accent_24dp
        : R.drawable.ic_favorite_border_accent_24dp;

    holder.wordTv.setText(translation.getWord());
    holder.translatedTv.setText(translated);
    holder.directionTv.setText(translation.getDirection());
    holder.favoriteIv.setImageDrawable(mContextWeakReference.get().getResources().getDrawable(iconId));
  }

  @Override
  public int getItemCount() {
    if (AppConfig.DEBUG) Log.d(TAG, "getItemCount: " + history.size());

    return history.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.word_tv)
    TextView wordTv;
    @BindView(R.id.translated_tv)
    TextView translatedTv;
    @BindView(R.id.direction_tv)
    TextView directionTv;
    @BindView(R.id.favorite_iv)
    ImageView favoriteIv;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
