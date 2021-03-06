package com.myweather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.myweather.app.model.City;
import com.myweather.app.model.County;
import com.myweather.app.model.MyWeatherDB;
import com.myweather.app.model.Province;
import com.myweather.app.model.WeatherInfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class Utility {

	/**
	 * 解析和处理服务器返回的省级数据
	 */
	public synchronized static boolean handleProvincesResponse(MyWeatherDB myWeatherDB, String response){
		if(!TextUtils.isEmpty(response)){
			String[] allProvinces = response.split(",");
			if(allProvinces != null && allProvinces.length > 0){
				for(String p : allProvinces){
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					//将解析出来的数据存储到Province表
					myWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		
		return false;
		
	}
	
	/**
	 * 解析和处理服务器返回的市级数据
	 */
	public static boolean handleCitiesResponse(MyWeatherDB myWeatherDB, String response, int provinceId){
		if(!TextUtils.isEmpty(response)){
			String[] allCities = response.split(",");
			if(allCities != null && allCities.length > 0){
				for(String c : allCities){
					String[] array = c.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					//将解析出来的数据存储到City表
					myWeatherDB.saveCity(city);
				}
				return true;
			}
		}		
		return false;
		
	}
	
	/**
	 * 解析和处理服务器返回的县级数据
	 */
	public static boolean handleCountiesResponse(MyWeatherDB myWeatherDB,String response, int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allCounties = response.split(",");
			if(allCounties != null && allCounties.length > 0){
				for(String c : allCounties){
					String[] array = c.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					//将解析出来的数据存储到County表
					myWeatherDB.saveCounty(county);
				}
				return true;
			}
		}		
		return false;	
		
	}
	
	/**
	 * 解析服务器返回的JSON数据，并将解析出的数据存储到本地
	 */
	public static void handleWeatherResponse(Context context, String response){
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject todayInfo = jsonObject.getJSONObject("showapi_res_body");
			JSONObject cityInfo = todayInfo.getJSONObject("cityInfo");
			JSONObject weatherInfo = todayInfo.getJSONObject("f1");
			
			
			String cityName = cityInfo.getString("c3");//城市名
			String weatherCode = cityInfo.getString("c1");//区域id 
			String temp1 = weatherInfo.getString("day_air_temperature");//白天气温
			String temp2 = weatherInfo.getString("night_air_temperature");//晚上气温
			String weatherDesp = weatherInfo.getString("day_weather");//白天天气
			String publishTime = todayInfo.getString("time");//预报发布时间
			
			//后六天天气情况
			WeatherInfo[] wInfo = {
					new WeatherInfo(),
					new WeatherInfo(),
					new WeatherInfo(),
					new WeatherInfo(),
					new WeatherInfo(),
					new WeatherInfo(),
			};
			for(int i = 0; i < 6; i++){
				String code = "f" + (i + 2);
				JSONObject weatherInfo2 = todayInfo.getJSONObject(code);
				wInfo[i].setDay_weather(weatherInfo2.getString("day_weather"));//天气情况
				wInfo[i].setDay_air_temperature(weatherInfo2.getString("day_air_temperature"));//白天气温
				wInfo[i].setNight_air_temperature(weatherInfo2.getString("night_air_temperature"));//晚上气温
				wInfo[i].setWeekday(String.valueOf((Integer.valueOf(weatherInfo2.getString("weekday")))));//周几								
			}
						
			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp, publishTime, wInfo);
		} catch (JSONException e) {
			Log.d("Utility", "解析JSON数据出错！");
			e.printStackTrace();
		}
	}
	

	/**
	 * 将服务器返回的所有天气信息存储到SharedPreferences文件中
	 */
	public static void saveWeatherInfo(Context context, String cityName, String weatherCode, String temp1,
			String temp2, String weatherDesp, String publishTime, WeatherInfo[] wInfo) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));
		
		//存储后六天天气信息
		for(int i = 0; i < wInfo.length; i++){
			String f = "f" + (i + 2);
			
			editor.putString(f + "_day_weather", wInfo[i].getDay_weather());
			editor.putString(f + "_day_air_temperature", wInfo[i].getDay_air_temperature());
			editor.putString(f + "_night_air_temperature", wInfo[i].getNight_air_temperature());
			editor.putString(f + "_weekday", wInfo[i].getWeekday());
		}
		
		editor.commit();
	}
	
	/**
	 * 将阿拉伯数字1-7转换为周一至周末
	 */
	public static String SwitchWeekToNum(String num){
		int i = (Integer.valueOf(num))%7;
		String number = null;
		switch (i) {
		case 1:
			number = "周一";
			break;
		case 2:
			number = "周二";
			break;
		case 3:
			number = "周三";
			break;
		case 4:
			number = "周四";
			break;
		case 5:
			number = "周五";
			break;
		case 6:
			number = "周六";
			break;
		case 0:
			number = "周末";
			break;
		default:
			break;
		}
		
		return number;
		
	}
	
	/**
	 * 将服务器返回的日期转为X点X分
	 */
	
	public static String swichTime(String time){
		if(time.length() != 14){
			return "解析失败";
		}
		String t3 = time.substring(8, 10) + "点";
		String t4 = time.substring(10, 12) + "分";
		
		return t3+t4;
		
	}
}
