package com.hamom.yandexschool.di.scopes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Scope;

/**
 * Created by hamom on 15.04.17.
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface FavoriteScope {
}
