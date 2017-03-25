package com.hamom.yandexschool.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by hamom on 17.12.16.
 */

public class NetworkStatusChecker {
  public static Boolean isNetworkAvailable(){
    ConnectivityManager cm = (ConnectivityManager) App.getAppContext().getSystemService(
        Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
    return networkInfo != null && networkInfo.isConnectedOrConnecting();
  }
}
