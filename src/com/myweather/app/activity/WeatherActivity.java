package com.myweather.app.activity;

import com.myweather.app.R;
import com.myweather.app.service.AutoUpdateService;
import com.myweather.app.util.HttpCallbackListener;
import com.myweather.app.util.HttpUtil;
import com.myweather.app.util.Utility;
import com.myweather.app.view.SlidingMenu;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherActivity extends Activity implements OnClickListener {

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
	/**
	 * 切换城市按钮
	 */
	private Button switchCity;
	/**
	 * 更新天气按钮
	 */
	private Button refreshWeather;
	/**
	 * 菜单ID
	 */
	private SlidingMenu mLeftMenu;
	/**
	 * 切换菜单按钮
	 */
	private Button swichMenu;
	/**
	 * 设置按钮
	 */
	private Button set;
	
	//后六天天气
	private TextView day1;
	private TextView day2;
	private TextView day3;
	private TextView day4;
	private TextView day5;
	private TextView day6;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mian_activity);
		// 初始化各控件
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
		switchCity = (Button) findViewById(R.id.swich_city);
		refreshWeather = (Button) findViewById(R.id.refresh_weather);
		mLeftMenu = (SlidingMenu) findViewById(R.id.id_menu);
		swichMenu = (Button) findViewById(R.id.select_menu);
		set = (Button) findViewById(R.id.set);
		day1 = (TextView) findViewById(R.id.day1);
		day2 = (TextView) findViewById(R.id.day2);
		day3 = (TextView) findViewById(R.id.day3);
		day4 = (TextView) findViewById(R.id.day4);
		day5 = (TextView) findViewById(R.id.day5);
		day6 = (TextView) findViewById(R.id.day6);
		
		set.setOnClickListener(this);
		swichMenu.setOnClickListener(this);
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
		String countyCode = getIntent().getStringExtra("county_code");
		if (!TextUtils.isEmpty(countyCode)) {
			// 如果有县级代号时就去查询天气
			publishText.setText("同步中.....");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		} else {
			// 没有县级代号时就直接显示本地天气
			showWeather();
		}
	}

	/**
	 * 查询县级代号所对应的天气地址
	 */
	private void queryWeatherCode(String countyCode) {
		String address = "https://route.showapi.com/9-2?area=" + countyCode
				+ "&areaid=&showapi_appid=19563&needMoreDay=1&showapi_sign=acaf858be0c94f2c884906bb41c07c72";
		queryFromServer(address);
	}

	/**
	 * 根据传入的地址和类型去向服务器查询天气代号或者天气信息
	 */
	private void queryFromServer(final String address) {
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				// 处理服务器返回的天气信息
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
		cityNameText.setText(prefs.getString("city_name", "失败"));
		temp1Text.setText(prefs.getString("temp1", "失败") + "℃");
		temp2Text.setText(prefs.getString("temp2", "失败") + "℃");
		weatherDespText.setText(prefs.getString("weather_desp", "失败"));
		publishText.setText("今天" + Utility.swichTime(prefs.getString("publish_time", "失败")) + "发布");
		currentDateText.setText(prefs.getString("current_date", "失败"));
		
		//读取后六天天气信息
		String[] info = new String[6];
		for(int i = 0; i < 6; i++){
			String f = "f" + (i + 2);
			String day_weather = prefs.getString(f + "_day_weather", "失败");
			String day_air_temperature = prefs.getString(f + "_day_air_temperature", "失败");
			String night_air_temperature = prefs.getString(f + "_night_air_temperature", "失败");
			String weekday = prefs.getString(f + "_weekday", "失败");
			info[i] = Utility.SwitchWeekToNum(weekday) + "\n" + day_air_temperature + "℃~" + night_air_temperature +"℃\n" + day_weather;
			
		}
		
		day1.setText(info[0]);
		day2.setText(info[1]);
		day3.setText(info[2]);
		day4.setText(info[3]);
		day5.setText(info[4]);
		day6.setText(info[5]);
		
		
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		Intent intent = new Intent(this, AutoUpdateService.class);
		startService(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.swich_city:
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			publishText.setText("同步中.....");
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode = prefs.getString("weather_code", "");
			if (!TextUtils.isEmpty(weatherCode)) {
				queryWeatherCode(weatherCode);
			}
			break;
		case R.id.select_menu:
			mLeftMenu.toggle();
			break;
		case R.id.set:
			Toast.makeText(this, "Developing", Toast.LENGTH_SHORT).show();
		default:
			break;
		}
	}
}
