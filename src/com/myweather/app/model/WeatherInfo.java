package com.myweather.app.model;

public class WeatherInfo {
	/**
	 * ����
	 */
	private String day_weather;
	/**
	 * ���������¶�
	 */
	private String day_air_temperature;
	/**
	 * ���������¶�
	 */
	private String night_air_temperature;
	/**
	 * �ܼ�
	 */
	private String weekday;
	public String getDay_weather() {
		return day_weather;
	}
	public void setDay_weather(String day_weather) {
		this.day_weather = day_weather;
	}
	public String getDay_air_temperature() {
		return day_air_temperature;
	}
	public void setDay_air_temperature(String day_air_temperature) {
		this.day_air_temperature = day_air_temperature;
	}
	public String getNight_air_temperature() {
		return night_air_temperature;
	}
	public void setNight_air_temperature(String night_air_temperature) {
		this.night_air_temperature = night_air_temperature;
	}
	public String getWeekday() {
		return weekday;
	}
	public void setWeekday(String weekday) {
		this.weekday = weekday;
	}
	
}
