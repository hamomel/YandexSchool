package com.hamom.yandexschool.utils;

import android.app.Application;
import android.content.Context;
import com.hamom.yandexschool.di.components.AppComponent;
import com.hamom.yandexschool.di.components.DaggerAppComponent;
import com.hamom.yandexschool.di.modules.AppModule;

/**
 * Created by hamom on 25.03.17.
 */

public class App extends Application{

  private static AppComponent mAppComponent;
  private static Context sContext;

  @Override
  public void onCreate() {
    super.onCreate();
    sContext = getApplicationContext();
    createAppComponent();
  }

  public static Context getAppContext() {
    return sContext;
  }

  public static AppComponent getAppComponent(){
    return mAppComponent;
  }

  private void createAppComponent(){
    mAppComponent = DaggerAppComponent.builder()
        .appModule(new AppModule())
        .build();
  }
}
