package com.hamom.yandexschool.utils;

/**
 * Created by hamom on 25.03.17.
 */

public class AppConfig {
  public static final boolean DEBUG = true;
  public static final String BASE_URL = "https://translate.yandex.net/api/v1.5/tr.json/";
  public static final String API_KEY = "trnsl.1.1.20170320T183352Z.daa0464b0c246aad.234ff7542b281e6e61c5855cfef2b062ddef7692";
  public static final long MAX_CONNECT_TIMEOUT = 5000;
  public static final long MAX_READ_TIMEOUT = 5000;
  public static final long MAX_WRITE_TIMEOUT = 5000;
  public static final long LANGS_UPDATE_INTERVAL = 24 * 60 * 60 * 1000;
}
