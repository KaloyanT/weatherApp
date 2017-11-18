package com.weatherapp.util;

import com.weatherapp.model.entity.City;
import com.weatherapp.model.entity.DarkSkyForecast;
import com.weatherapp.repository.DarkSkyForecastRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.plogitech.darksky.forecast.model.DailyDataPoint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class DBClient {

    @Autowired
    private DarkSkyForecastRepository darkSkyForecastRepository;

    /**
     * Saves a daily forecast (without measurement units) for a given city in to the database.
     * This method returns the object that it is saving so the REST controller can return it
     * as a response
     * @param city he city for which the daily forecast is
     * @param dailyForecast The daily forecast for this city
     * @return The forecast object that has been saved or null if the object cannot be saved
     */
    public DarkSkyForecast saveDailyForecast(City city, DailyDataPoint dailyForecast) {

        if(city == null || dailyForecast == null) {
            return null;
        }

        if(dailyForecast.getTime() == null) {
            return null;
        }

        // Format the date to dd/MM/yyyy to strip the time from the Dark Sky API
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dateAsStr = sdf.format(Date.from(dailyForecast.getTime()));
        Date temp = null;
        try {
            temp = sdf.parse(dateAsStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long timeStamp = temp.toInstant().getEpochSecond();

        // Check if a forecast for the given date and city already exists and if so, just update it
        DarkSkyForecast darkSkyForecast = darkSkyForecastRepository.getByCityAndTimeStamp(city, timeStamp);

        // Otherwise create a new entry
        if(darkSkyForecast == null) {
            darkSkyForecast = new DarkSkyForecast();
        }

        darkSkyForecast.setCity(city);
        darkSkyForecast.setTimeStamp(timeStamp);

        // Check all the values because sometimes some of them are missing
        // from the API response
        if(dailyForecast.getTemperatureMax() != null) {
            darkSkyForecast.setTemperatureHigh(dailyForecast.getTemperatureMax());
        }

        if(dailyForecast.getTemperatureMin() != null) {
            darkSkyForecast.setTemperatureLow(dailyForecast.getTemperatureMin());
        }

        if(dailyForecast.getApparentTemperatureMax() != null) {
            darkSkyForecast.setApparentTemperatureHigh(dailyForecast.getApparentTemperatureMax());
        }

        if(dailyForecast.getApparentTemperatureMin() != null) {
            darkSkyForecast.setApparentTemperatureLow(dailyForecast.getApparentTemperatureMin());
        }

        if(dailyForecast.getCloudCover() != null) {
            darkSkyForecast.setCloudCover(dailyForecast.getCloudCover());
        }

        if(dailyForecast.getDewPoint() != null) {
            darkSkyForecast.setDewPoint(dailyForecast.getDewPoint());
        }

        if(dailyForecast.getHumidity() != null) {
            darkSkyForecast.setHumidity(dailyForecast.getHumidity());
        }

        if(dailyForecast.getIcon() != null) {
            darkSkyForecast.setIcon(dailyForecast.getIcon());
        }

        if(dailyForecast.getPrecipIntensity() != null) {
            darkSkyForecast.setPrecipIntensity(dailyForecast.getPrecipIntensity());
        }

        if(dailyForecast.getPrecipProbability() != null) {
            darkSkyForecast.setPrecipProbability(dailyForecast.getPrecipProbability());
        }

        if(dailyForecast.getPrecipType() != null) {
            darkSkyForecast.setPrecipType(dailyForecast.getPrecipType());
        }

        if(dailyForecast.getSummary() != null) {
            darkSkyForecast.setSummary(dailyForecast.getSummary());
        }

        if(dailyForecast.getPressure() != null) {
            darkSkyForecast.setPressure(dailyForecast.getPressure());
        }

        if(dailyForecast.getVisibility() != null) {
            darkSkyForecast.setVisibility(dailyForecast.getVisibility());
        }

        if(dailyForecast.getWindSpeed() != null) {
            darkSkyForecast.setWindSpeed(dailyForecast.getWindSpeed());
        }

        city.addForecast(darkSkyForecast);

        darkSkyForecastRepository.save(darkSkyForecast);

        return darkSkyForecast;
    }
}
