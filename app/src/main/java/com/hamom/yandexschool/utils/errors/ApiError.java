package com.hamom.yandexschool.utils.errors;

/**
 * Created by hamom on 25.03.17.
 */

public class ApiError extends Throwable {
  public ApiError(int code) {
    super("Не возможно получить ответ сервера. Код ошибки: " + code);
  }
}
