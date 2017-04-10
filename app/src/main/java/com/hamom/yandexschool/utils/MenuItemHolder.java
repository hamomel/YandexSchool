package com.hamom.yandexschool.utils;

import android.view.MenuItem;

/**
 * Created by hamom on 10.04.17.
 */

public class MenuItemHolder {
  private int mItemResId;
  private CharSequence mItemTitle;
  private int mShowAsAction;
  private MenuItem.OnMenuItemClickListener mListener;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    MenuItemHolder holder = (MenuItemHolder) o;

    if (mItemResId != holder.mItemResId) return false;
    return mItemTitle.equals(holder.mItemTitle);
  }

  @Override
  public int hashCode() {
    int result = mItemResId;
    result = 31 * result + mItemTitle.hashCode();
    return result;
  }

  public int getItemResId() {
    return mItemResId;
  }

  public void setItemResId(int itemResId) {
    mItemResId = itemResId;
  }

  public CharSequence getItemTitle() {
    return mItemTitle;
  }

  public void setItemTitle(CharSequence itemTitle) {
    mItemTitle = itemTitle;
  }

  public int getShowAsAction() {
    return mShowAsAction;
  }

  public void setShowAsAction(int showAsAction) {
    mShowAsAction = showAsAction;
  }

  public MenuItem.OnMenuItemClickListener getListener() {
    return mListener;
  }

  public void setListener(MenuItem.OnMenuItemClickListener listener) {
    mListener = listener;
  }
}
