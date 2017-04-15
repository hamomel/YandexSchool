package com.hamom.yandexschool.ui.fragments.favorite;

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
import butterknife.OnClick;
import butterknife.OnLongClick;
import com.hamom.yandexschool.R;
import com.hamom.yandexschool.data.local.models.Translation;
import com.hamom.yandexschool.utils.AppConfig;
import com.hamom.yandexschool.utils.ConstantManager;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hamom on 15.04.17.
 */

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {
  private static String TAG = ConstantManager.TAG_PREFIX + "FavAdapt: ";

  private List<Translation> mHistory = new ArrayList<>();
  private WeakReference<Context> mContextWeakReference;
  private FavoriteAdapter.FavoriteClickListener mListener;
  private Map<String, String> mLangs;

  public FavoriteAdapter(FavoriteClickListener listener) {
    mListener = listener;
  }

  public void init(List<Translation> history, Map<String, String> langs){
    mLangs = langs;
    this.mHistory = history;
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
    return new FavoriteAdapter.ViewHolder(v, mListener);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    final Translation translation = mHistory.get(position);
    String translated = "";
    for (String s : translation.getTranslations()) {
      translated = translated.concat(s).concat("; ");
    }

    int iconId = translation.isFavorite() ? R.drawable.ic_favorite_accent_24dp
        : R.drawable.ic_favorite_border_accent_24dp;

    char arrow = 8594;
    String[] langCodes = translation.getDirection().split("-");
    String direction = mLangs.get(langCodes[0]) + " " + arrow + " " + mLangs.get(langCodes[1]);

    holder.wordTv.setText(translation.getWord());
    holder.translatedTv.setText(translated);
    holder.directionTv.setText(direction);
    holder.favoriteIv.setImageDrawable(mContextWeakReference.get().getResources().getDrawable(iconId));
  }

  @Override
  public int getItemCount() {
    return mHistory.size();
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

    FavoriteAdapter.FavoriteClickListener mListener;

    @OnClick(R.id.favorite_iv)
    void onFavoriteClick(View v){
      Translation translation = mHistory.get(getAdapterPosition());
      translation.changeFavorite();
      mListener.onClick(v, translation);
      mHistory.remove(translation);
      notifyDataSetChanged();
    }

    @OnClick(R.id.history_item)
    void onItemClick(View v){
      mListener.onClick(v, mHistory.get(getAdapterPosition()));
    }

    @OnLongClick(R.id.history_item)
    boolean onItemLongClick(View v){
      mListener.onLongClick(v, mHistory.get(getAdapterPosition()));
      return true;
    }

    public ViewHolder(View itemView, FavoriteAdapter.FavoriteClickListener listener) {
      super(itemView);
      mListener = listener;
      ButterKnife.bind(this, itemView);
    }
  }

  public interface FavoriteClickListener{
    void onClick(View v, Translation translation);
    void onLongClick(View v, Translation translation);
  }
}
