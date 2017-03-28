package com.hamom.yandexschool.di.modules;

import android.content.Context;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/**
 * Created by hamom on 25.03.17.
 */
@Module
public class AppModule {
  private Context mContext;

  public AppModule(Context context) {
    mContext = context;
  }

  @Singleton
  @Provides
  Context provideContext() {
    return mContext;
  }
}
