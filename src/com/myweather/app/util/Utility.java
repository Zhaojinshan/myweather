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
	 * �����ʹ�����������ص�ʡ������
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
					//���������������ݴ洢��Province��
					myWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		
		return false;
		
	}
	
	/**
	 * �����ʹ�����������ص��м�����
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
					//���������������ݴ洢��City��
					myWeatherDB.saveCity(city);
				}
				return true;
			}
		}		
		return false;
		
	}
	
	/**
	 * �����ʹ�����������ص��ؼ�����
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
					//���������������ݴ洢��County��
					myWeatherDB.saveCounty(county);
				}
				return true;
			}
		}		
		return false;	
		
	}
	
	/**
	 * �������������ص�JSON���ݣ����������������ݴ洢������
	 */
	public static void handleWeatherResponse(Context context, String response){
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject todayInfo = jsonObject.getJSONObject("showapi_res_body");
			JSONObject cityInfo = todayInfo.getJSONObject("cityInfo");
			JSONObject weatherInfo = todayInfo.getJSONObject("f1");
			
			
			String cityName = cityInfo.getString("c3");//������
			String weatherCode = cityInfo.getString("c1");//����id 
			String temp1 = weatherInfo.getString("day_air_temperature");//��������
			String temp2 = weatherInfo.getString("night_air_temperature");//��������
			String weatherDesp = weatherInfo.getString("day_weather");//��������
			String publishTime = todayInfo.getString("time");//Ԥ������ʱ��
			
			//�������������
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
				wInfo[i].setDay_weather(weatherInfo2.getString("day_weather"));//�������
				wInfo[i].setDay_air_temperature(weatherInfo2.getString("day_air_temperature"));//��������
				wInfo[i].setNight_air_temperature(weatherInfo2.getString("night_air_temperature"));//��������
				wInfo[i].setWeekday(String.valueOf((Integer.valueOf(weatherInfo2.getString("weekday")))));//�ܼ�								
			}
						
			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp, publishTime, wInfo);
		} catch (JSONException e) {
			Log.d("Utility", "����JSON���ݳ���");
			e.printStackTrace();
		}
	}
	

	/**
	 * �����������ص�����������Ϣ�洢��SharedPreferences�ļ���
	 */
	public static void saveWeatherInfo(Context context, String cityName, String weatherCode, String temp1,
			String temp2, String weatherDesp, String publishTime, WeatherInfo[] wInfo) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy��M��d��",Locale.CHINA);
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
		
		//�洢������������Ϣ
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
	 * ������������1-7ת��Ϊ��һ����ĩ
	 */
	public static String SwitchWeekToNum(String num){
		int i = (Integer.valueOf(num))%7;
		String number = null;
		switch (i) {
		case 1:
			number = "��һ";
			break;
		case 2:
			number = "�ܶ�";
			break;
		case 3:
			number = "����";
			break;
		case 4:
			number = "����";
			break;
		case 5:
			number = "����";
			break;
		case 6:
			number = "����";
			break;
		case 0:
			number = "��ĩ";
			break;
		default:
			break;
		}
		
		return number;
		
	}
	
	/**
	 * �����������ص�����תΪX��X��
	 */
	
	public static String swichTime(String time){
		if(time.length() != 14){
			return "����ʧ��";
		}
		String t3 = time.substring(8, 10) + "��";
		String t4 = time.substring(10, 12) + "��";
		
		return t3+t4;
		
	}
}
