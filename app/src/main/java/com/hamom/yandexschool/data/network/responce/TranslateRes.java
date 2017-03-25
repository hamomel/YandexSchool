package com.hamom.yandexschool.data.network.responce;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class TranslateRes{

	@SerializedName("code")
	@Expose
	private int code;

	@SerializedName("text")
	@Expose
	private List<String> text;

	@SerializedName("lang")
	@Expose
	private String lang;

	public int getCode(){
		return code;
	}

	public List<String> getText(){
		return text;
	}

	public String getLang(){
		return lang;
	}
}