package com.myweather.app.activity;

import com.myweather.app.R;
import com.myweather.app.util.HttpCallbackListener;
import com.myweather.app.util.HttpUtil;
import com.myweather.app.util.Utility;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
	
public class WeatherActivity extends Activity {

	private LinearLayout weatherInfoLayout;
	/**
	 * 用于显示城市名
	 */
	private TextView cityNameText;
	/**
	 * 用于显示发布时间
	 */
	private TextView publishText;
	/**
	 * 用于显示天气描述信息
	 */
	private TextView weatherDespText;
	/**
	 * 用于显示气温1
	 */
	private TextView temp1Text;
	/**
	 * 用于显示气温2
	 */
	private TextView temp2Text;
	/**
	 * 用于显示当前日期
	 */
	private TextView currentDateText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		//初始化各控件
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
		String countyCode = getIntent().getStringExtra("county_code");
		if(!TextUtils.isEmpty(countyCode)){
			//如果有县级代号时就去查询天气
			publishText.setText("同步中.....");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		}else{
			//没有县级代号时就直接显示本地天气
			showWeather();
		}
	}

	/**
	 * 查询县级代号所对应的天气地址
	 */
	private void queryWeatherCode(String countyCode) {
		String address = "https://route.showapi.com/9-2?area=" + countyCode + "&areaid=&showapi_appid=19563&showapi_sign=acaf858be0c94f2c884906bb41c07c72";
		queryFromServer(address);
	}
	
	/**
	 * 根据传入的地址和类型去向服务器查询天气代号或者天气信息
	 */ 
	private void queryFromServer(final String address){
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {	
				//处理服务器返回的天气信息
				Utility.handleWeatherResponse(WeatherActivity.this, response);
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						showWeather();
					}
				});				
			}
			
			@Override
			public void onError(Exception e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {						
						publishText.setText("同步失败");
					}
				});
			}
		});
	}
	
	/**
	 * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上
	 */
	private void showWeather() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText( prefs.getString("city_name", "失败"));
		temp1Text.setText(prefs.getString("temp1", "失败"));
		temp2Text.setText(prefs.getString("temp2", "失败"));
		weatherDespText.setText(prefs.getString("weather_desp", "失败"));
		publishText.setText("今天" + prefs.getString("publish_time", "失败") + "发布");
		currentDateText.setText(prefs.getString("current_date", "失败"));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
	}
}
