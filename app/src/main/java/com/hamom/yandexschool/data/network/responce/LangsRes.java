package com.hamom.yandexschool.data.network.responce;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hamom on 02.04.17.
 */

public class LangsRes {
  @SerializedName("dirs")
  @Expose
  private List<String> dirs = null;

  @SerializedName("langs")
  @Expose
  private Map<String, String> langs = new HashMap<>();

  public List<String> getDirs() {
    return dirs;
  }

  public Map<String, String> getLangs() {
    return langs;
  }

  public LangsRes(Map<String, String> langs) {
    this.langs = langs;
  }
}
