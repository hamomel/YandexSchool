package com.hamom.yandexschool.ui.fragments.history;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.HapticFeedbackConstants;
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
import com.hamom.yandexschool.mvp_contract.FavoriteContract;
import com.hamom.yandexschool.utils.AppConfig;
import com.hamom.yandexschool.utils.ConstantManager;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by hamom on 08.04.17.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
  private static String TAG = ConstantManager.TAG_PREFIX + "HistoryAdapt: ";
  private List<Translation> mHistory = new ArrayList<>();
  private WeakReference<Context> mContextWeakReference;
  private HistoryClickListener mListener;
  private Map<String, String> mLangs;
  private List<Translation> mSelectedItems = new ArrayList<>();

  public HistoryAdapter(HistoryClickListener listener) {
    mListener = listener;
    setHasStableIds(true);
  }

  @Override
  public long getItemId(int position) {
    return mHistory.get(position).getId();
  }

  public void init(List<Translation> history, Map<String, String> langs){
    mLangs = langs;
    this.mHistory = history;
    notifyDataSetChanged();
  }

  public void addSelection(Translation translation){
    mSelectedItems.add(translation);
    if (AppConfig.DEBUG) Log.d(TAG, "addSelection: " + mSelectedItems);

    notifyDataSetChanged();
  }

  public List<Translation> getSelectedItems(){
    return mSelectedItems;
  }

  public void deSelectItem(Translation translation) {
    mSelectedItems.remove(translation);
    notifyDataSetChanged();
  }

  public void deleteSelectedItems() {
    for (Translation selectedItem : mSelectedItems) {
      mHistory.remove(selectedItem);
    }
    notifyDataSetChanged();
  }

  public void setNormalMode() {
    mSelectedItems.clear();
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
    return new ViewHolder(v, mListener);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, final int position) {
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
    if (mSelectedItems.contains(mHistory.get(position))){
      setItemSelected(holder);
    } else {
      setItemNormal(holder);
    }
  }

  private void setItemSelected(ViewHolder holder) {
    int color = mContextWeakReference.get().getResources().getColor(R.color.colorPrimaryLight);
    holder.historyItem.setBackgroundColor(color);
  }

  private void setItemNormal(ViewHolder holder) {
    TypedArray array = mContextWeakReference.get().obtainStyledAttributes(new int[]{android.R.attr.colorBackground});
    int color = array.getColor(0, 0x000000);
    holder.historyItem.setBackgroundColor(color);
    array.recycle();
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
    @BindView(R.id.history_item)
    ConstraintLayout historyItem;

    HistoryClickListener mListener;

    public ViewHolder(View itemView, HistoryClickListener listener) {
      super(itemView);
      mListener = listener;
      ButterKnife.bind(this, itemView);
    }

    @OnClick(R.id.favorite_iv)
    void onFavoriteClick(View v){
      mHistory.get(getAdapterPosition()).changeFavorite();
      mListener.onClick(v, mHistory.get(getAdapterPosition()));
      notifyDataSetChanged();
    }

    @OnClick(R.id.history_item)
    void onItemClick(View v){
      mListener.onClick(v, mHistory.get(getAdapterPosition()));
    }

    @OnLongClick(R.id.history_item)
    boolean onItemLongClick(View v){
      v.setHapticFeedbackEnabled(true);
      v.performHapticFeedback(HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);
      mListener.onLongClick(v, mHistory.get(getAdapterPosition()));
      return true;
    }
  }

  interface HistoryClickListener {
    void onClick(View v, Translation translation);
    void onLongClick(View v, Translation translation);
  }
}
