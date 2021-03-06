package net.teslaa.flymate.ui;

import net.teslaa.flymate.BuildConfig;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * By teslaa on 12/7/17 at Flymate.
 */

public class OpenWeather extends WeatherSource {

  public OpenWeather(WeatherSourceCallback callback) {
    super(callback);
  }

  static {
    System.loadLibrary("keys");
  }

  private native String getNativeKey1();
  private String key = getNativeKey1();
  @Override
  protected String getForecastUrl(double latitude, double longitude) {
    return "https://api.openweathermap.org/data/2.5/weather?units=metric&"
        + "lat=" + latitude +
        "&lon=" + longitude +
        "&appid=" + BuildConfig.APIKEY;
  }

  @Override
  protected CurrentWeatherIcons parseForecastDetails(String forecastData)
      throws WeatherSourceException {
    CurrentWeatherIcons forecast = new CurrentWeatherIcons();

    try {
      forecast.setCurrent(getCurrentDetails(forecastData));
    } catch (JSONException e) {
      throw new WeatherSourceException(e);
    }

    return forecast;
  }

  private Current getCurrentDetails(String jsonData) throws JSONException {
    JSONObject forecast = new JSONObject(jsonData);
    Current current = new Current();

    String timezone = forecast.getString("name");

    JSONArray weather = forecast.getJSONArray("weather");
    JSONObject weatherObject = weather.getJSONObject(0);
    current.setSummary(weatherObject.getString("description"));
    current.setIcon(weatherObject.getString("icon"));

    JSONObject main = forecast.getJSONObject("main");
    current.setHumidity(main.getDouble("humidity"));
    current.setTemperature(main.getDouble("temp"));

    JSONObject sys = forecast.getJSONObject("sys");
    current.setTime(sys.getLong("sunrise"));

    JSONObject wind = forecast.getJSONObject("wind");
    current.setPrecipChance(wind.getDouble("speed"));

    current.setTimeZone(timezone);

    return current;
  }
}
