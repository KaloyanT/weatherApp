package com.weatherapp.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.weatherapp.model.entity.City;
import com.weatherapp.model.entity.DarkSkyForecast;
import com.weatherapp.model.entity.MeasurementUnit;
import com.weatherapp.repository.CityRepository;
import com.weatherapp.repository.MeasurementUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.plogitech.darksky.api.jackson.DarkSkyJacksonClient;
import tk.plogitech.darksky.forecast.APIKey;
import tk.plogitech.darksky.forecast.ForecastException;
import tk.plogitech.darksky.forecast.ForecastRequest;
import tk.plogitech.darksky.forecast.ForecastRequestBuilder;
import tk.plogitech.darksky.forecast.GeoCoordinates;
import tk.plogitech.darksky.forecast.Latitude;
import tk.plogitech.darksky.forecast.Longitude;
import tk.plogitech.darksky.forecast.model.Currently;
import tk.plogitech.darksky.forecast.model.DailyDataPoint;
import tk.plogitech.darksky.forecast.model.Forecast;
import javax.annotation.PostConstruct;

@Component
public class WeatherAppDarkSkyClient {

    @Value("${weatherapp.darksky.api.key}")
    private String DARKSKY_API_KEY;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private MeasurementUnitRepository measurementUnitRepository;

    @Autowired
    private DBClient dbClient;

    // Hardcode the measurement units into variables so there is only 1 database
    // access for them at Bean initialization and not at every request
    private static String TEMPERATURE_UNIT;

    private static String HUMIDITY_UNIT;

    private static String WIND_SPEED_UNIT;

    private static String PRECIP_INTENSITY_UNIT;

    private static String PRECIP_PROBABILITY_UNIT;

    private static String PRESSURE_UNIT;

    private static String CLOUD_COVER_UNIT;

    private static String VISIBILITY_UNIT;


    /**
     * Initializes the measurement units at Bean creation
     */
    @PostConstruct
    private void initUnits() {

        // First check if there are measurement units for these quantities saved in the database
        MeasurementUnit temperature = measurementUnitRepository.findByQuantity("temperature");
        MeasurementUnit humidity = measurementUnitRepository.findByQuantity("humidity");
        MeasurementUnit precipIntensity = measurementUnitRepository.findByQuantity("precipIntensity");
        MeasurementUnit precipProbability = measurementUnitRepository.findByQuantity("precipProbability");
        MeasurementUnit windSpeed = measurementUnitRepository.findByQuantity("windSpeed");
        MeasurementUnit pressure = measurementUnitRepository.findByQuantity("pressure");
        MeasurementUnit cloudCover = measurementUnitRepository.findByQuantity("cloudCover");
        MeasurementUnit visibility = measurementUnitRepository.findByQuantity("visibility");

        // Now set the units. Insert an empty space before the unit itself to avoid doing that every
        // time a unit is appended to the corresponding value
        TEMPERATURE_UNIT = (temperature != null) ? (" " + temperature.getMeasurementUnit()) : ("");
        HUMIDITY_UNIT = (humidity != null) ? (" " + humidity.getMeasurementUnit()) : ("");
        PRECIP_INTENSITY_UNIT = (precipIntensity != null) ? (" " + precipIntensity.getMeasurementUnit()) : ("");
        PRECIP_PROBABILITY_UNIT = (precipProbability != null) ? (" " + precipProbability.getMeasurementUnit()) : ("");
        WIND_SPEED_UNIT = (windSpeed != null) ? (" " + windSpeed.getMeasurementUnit()) : ("");
        PRESSURE_UNIT = (pressure != null) ? (" " + pressure.getMeasurementUnit()) : ("");
        CLOUD_COVER_UNIT = (cloudCover != null) ? (" " + cloudCover.getMeasurementUnit()) : ("");
        VISIBILITY_UNIT = (visibility != null) ? (" " + visibility.getMeasurementUnit()) : ("");
    }


    /**
     * Returns the daily forecast for the current day and the next 7 days for the given
     * city in the given country
     * @param city The city for which the forecast should be returned
     * @return The daily forecast for the given city for the current day and the next 7 days
     */
    public Forecast getDarkSkyDailyForecast(City city) {

        if(city == null) {
            return null;
        }

        ForecastRequest request = new ForecastRequestBuilder()
                .key(new APIKey(DARKSKY_API_KEY))
                .units(ForecastRequestBuilder.Units.si)
                .language(ForecastRequestBuilder.Language.en)
                .location(new GeoCoordinates(new Longitude(city.getLongitude()), new Latitude(city.getLatitude())))
                .exclude(ForecastRequestBuilder.Block.alerts,
                        ForecastRequestBuilder.Block.flags,
                        ForecastRequestBuilder.Block.minutely,
                        ForecastRequestBuilder.Block.hourly,
                        ForecastRequestBuilder.Block.currently)
                .build();

        DarkSkyJacksonClient client = new DarkSkyJacksonClient();

        Forecast forecast = null;
        try {
            forecast = client.forecast(request);
        } catch (ForecastException e) {
            e.printStackTrace();
        }

        return forecast;
    }

    
    /**
     * Returns the current forecast for the given city in the given country
     * @param city The city for which the forecast should be returned
     * @return The current forecast for the given city
     */
    public Forecast getDarkSkyCurrentForecast(City city) {

        if(city == null) {
            return null;
        }

        ForecastRequest request = new ForecastRequestBuilder()
                .key(new APIKey(DARKSKY_API_KEY))
                .units(ForecastRequestBuilder.Units.si)
                .language(ForecastRequestBuilder.Language.en)
                .location(new GeoCoordinates(new Longitude(city.getLongitude()), new Latitude(city.getLatitude())))
                .exclude(ForecastRequestBuilder.Block.alerts,
                        ForecastRequestBuilder.Block.flags,
                        ForecastRequestBuilder.Block.minutely,
                        ForecastRequestBuilder.Block.hourly,
                        ForecastRequestBuilder.Block.daily)
                .build();

        DarkSkyJacksonClient client = new DarkSkyJacksonClient();

        Forecast forecast = null;
        try {
            forecast = client.forecast(request);
        } catch (ForecastException e) {
            e.printStackTrace();
        }

        return forecast;
    }


    /**
     * Converts all units, which are measured in percents, from 0.x notation to
     * percent notation, i.e. x
     * @param forecast The Forecast object for which the units have to be converted
     */
    public void convertUnitsDaily(Forecast forecast, boolean currentDayOnly) {

        if(forecast == null) {
            return;
        }

        // The Daily forecast includes 8 days in total: Today and the next 7 days
        // For the scheduled method however it will be more efficient to only convert
        // the units for the current day since we don't need the others
        int lastIndex = (currentDayOnly == true) ? (0) : (7);


        for(int i = 0; i <= lastIndex; i++) {

            DailyDataPoint current = forecast.getDaily().getData().get(i);

            // Turn into percentage
            double precipIntensity = current.getPrecipIntensity() * 100;
            double precipProbability = current.getPrecipProbability() * 100;
            double humidity = current.getHumidity() * 100;
            double cloudCover = current.getCloudCover() * 100;

            // Round to 2 decimal places
            precipIntensity = Math.round(precipIntensity * 100.0) / 100.0;
            precipProbability = Math.round(precipProbability * 100.0) / 100.0;
            humidity = Math.round(humidity * 100.0) / 100.0;
            cloudCover = Math.round(cloudCover * 100.0) / 100.0;

            current.setPrecipIntensity(precipIntensity);
            current.setPrecipProbability(precipProbability);
            current.setHumidity(humidity);
            current.setCloudCover(cloudCover);

            forecast.getDaily().getData().set(i, current);
        }
    }


    /**
     * Creates a JSON (ObjectNode) for the given DarkSkyForecast and adds the measurement
     * units for each quantity
     * @param darkSkyForecast The DarkSkyForecast from which a JSON has to be created
     * @return A JSON contained the information from the DarkSkyForecast with measurement units
     * or an empty JSON if the DarkSkyForecast object is null
     */
    public ObjectNode buildJsonForDarkSkyForecastWithUnits(DarkSkyForecast darkSkyForecast) {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();

        // Return an empty json if the dailyForecast is null
        if(darkSkyForecast ==  null) {
            return json;
        }

        json.put("timeStamp", darkSkyForecast.getTimeStamp());
        json.put("summary", darkSkyForecast.getSummary());
        json.put("icon", darkSkyForecast.getIcon());
        json.put("temperatureLow", darkSkyForecast.getTemperatureLow() + TEMPERATURE_UNIT);
        json.put("temperatureMax", darkSkyForecast.getTemperatureHigh() + TEMPERATURE_UNIT);
        json.put("apparentTemperatureLow", darkSkyForecast.getApparentTemperatureLow() + TEMPERATURE_UNIT);
        json.put("apparentTemperatureHigh", darkSkyForecast.getApparentTemperatureHigh() + TEMPERATURE_UNIT);
        json.put("humidity", darkSkyForecast.getHumidity() + HUMIDITY_UNIT);
        json.put("precipIntensity", darkSkyForecast.getPrecipIntensity()+ PRECIP_INTENSITY_UNIT);
        json.put("precipProbability", darkSkyForecast.getPrecipProbability() + PRECIP_PROBABILITY_UNIT);
        json.put("precipType", darkSkyForecast.getPrecipType());
        json.put("dewPoint", darkSkyForecast.getDewPoint() + TEMPERATURE_UNIT);
        json.put("windSpeed", darkSkyForecast.getWindSpeed() + WIND_SPEED_UNIT);
        json.put("visibility", darkSkyForecast.getVisibility() + VISIBILITY_UNIT);
        json.put("pressure", darkSkyForecast.getPressure() + PRESSURE_UNIT);
        json.put("cloudCover", darkSkyForecast.getCloudCover() + CLOUD_COVER_UNIT);

        return json;
    }


    public ObjectNode buildJsonForDailyDataPoint(DailyDataPoint dailyDataPoint) {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();

        // Return an empty json if the dailyForecast is null
        if (dailyDataPoint == null) {
            return json;
        }

        json.put("timeStamp", dailyDataPoint.getTime().getEpochSecond());
        json.put("summary", dailyDataPoint.getSummary());
        json.put("icon", dailyDataPoint.getIcon());
        json.put("temperatureLow", dailyDataPoint.getTemperatureMin());
        json.put("temperatureMax", dailyDataPoint.getTemperatureMax());
        json.put("apparentTemperatureLow", dailyDataPoint.getApparentTemperatureMin());
        json.put("apparentTemperatureMax", dailyDataPoint.getApparentTemperatureMax());
        json.put("humidity", dailyDataPoint.getHumidity());
        json.put("precipIntensity", dailyDataPoint.getPrecipIntensity());
        json.put("precipProbability", dailyDataPoint.getPrecipProbability());
        json.put("precipType", dailyDataPoint.getPrecipType());
        json.put("dewPoint", dailyDataPoint.getDewPoint());
        json.put("windSpeed", dailyDataPoint.getWindSpeed());
        json.put("visibility", dailyDataPoint.getVisibility());
        json.put("pressure", dailyDataPoint.getPressure());
        json.put("cloudCover", dailyDataPoint.getCloudCover());

        return json;
    }


    /**
     *
     * @param forecast
     * @return
     */
    public ObjectNode buildJsonForCurrentForecast(Forecast forecast) {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();

        // Return an empty json if the forecast or the current forecast is null
        if (forecast == null || forecast.getCurrently() == null) {
            return json;
        }

        Currently currentForecast = forecast.getCurrently();

        // Turn into percentage like with the daily forecast. There is no need for another
        // method here, since we are only working wiht 1 current forecast
        double precipIntensityValue = currentForecast.getPrecipIntensity() * 100;
        double precipProbabilityValue = currentForecast.getPrecipProbability() * 100;
        double humidityValue = currentForecast.getHumidity() * 100;
        double cloudCoverValue = currentForecast.getCloudCover() * 100;

        // Round to 2 decimal places
        precipIntensityValue = Math.round(precipIntensityValue * 100.0) / 100.0;
        precipProbabilityValue = Math.round(precipProbabilityValue * 100.0) / 100.0;
        humidityValue = Math.round(humidityValue * 100.0) / 100.0;
        cloudCoverValue = Math.round(cloudCoverValue * 100.0) / 100.0;

        // Build JSON
        json.put("timeStamp", currentForecast.getTime().getEpochSecond());
        json.put("summary", currentForecast.getSummary());
        json.put("icon", currentForecast.getIcon());
        json.put("temperature", currentForecast.getTemperature() + TEMPERATURE_UNIT);
        json.put("apparentTemperature", currentForecast.getApparentTemperature() + TEMPERATURE_UNIT);
        json.put("humidity", humidityValue + HUMIDITY_UNIT);
        json.put("precipIntensity", precipIntensityValue + PRECIP_INTENSITY_UNIT);
        json.put("precipProbability", precipProbabilityValue + PRECIP_PROBABILITY_UNIT);
        json.put("precipType", currentForecast.getPrecipType());
        json.put("dewPoint", currentForecast.getDewPoint() + TEMPERATURE_UNIT);
        json.put("windSpeed", currentForecast.getWindSpeed() + WIND_SPEED_UNIT);
        json.put("visibility", currentForecast.getVisibility() + VISIBILITY_UNIT);
        json.put("pressure", currentForecast.getPressure() + PRESSURE_UNIT);
        json.put("cloudCover", cloudCoverValue + CLOUD_COVER_UNIT);

        return json;
    }


    /**
     * Converts the DailyDataPoint object from a Forecast object, which is
     * returned by the Dark Sky API, to a DarkSkyForecast object, which can be saved in the database
     * Note that this method is different from the one in the DBClient class. This method only converts
     * a DailyDataPoint into a DarkSkyObject, but doesn't save it in the database
     * @param dailyForecast The DailyDataPoint forecast object that has to be converted DarkSkyForecast object
     * @return The DarkSkyForecast object
     */
    public DarkSkyForecast convertDailyDataPointForecastToDarkSkyForecast(DailyDataPoint dailyForecast) {

        if(dailyForecast == null) {
            return null;
        }

        DarkSkyForecast darkSkyForecast = new DarkSkyForecast();

        darkSkyForecast.setTemperatureHigh(dailyForecast.getTemperatureMax());
        darkSkyForecast.setTemperatureLow(dailyForecast.getTemperatureMin());
        darkSkyForecast.setApparentTemperatureHigh(dailyForecast.getApparentTemperatureMax());
        darkSkyForecast.setApparentTemperatureLow(dailyForecast.getApparentTemperatureMin());
        darkSkyForecast.setCloudCover(dailyForecast.getCloudCover());
        darkSkyForecast.setDewPoint(dailyForecast.getDewPoint());
        darkSkyForecast.setHumidity(dailyForecast.getHumidity());
        darkSkyForecast.setIcon(dailyForecast.getIcon());
        darkSkyForecast.setPrecipIntensity(dailyForecast.getPrecipIntensity());
        darkSkyForecast.setPrecipProbability(dailyForecast.getPrecipProbability());
        darkSkyForecast.setPrecipType(dailyForecast.getPrecipType());
        darkSkyForecast.setSummary(dailyForecast.getSummary());
        darkSkyForecast.setPressure(dailyForecast.getPressure());
        darkSkyForecast.setTimeStamp(dailyForecast.getTime().getEpochSecond());
        darkSkyForecast.setVisibility(dailyForecast.getVisibility());
        darkSkyForecast.setWindSpeed(dailyForecast.getWindSpeed());

        return darkSkyForecast;
    }


    /**
     * This is a scheduled method which gets the daily forecast from the API for each city
     * in the database and saves the data in the database
     * Fires every day at 12:00 and 18:00 UTC
     */
    @Scheduled(cron = "0 0 0,6,12,18 * * ?")
    private void getDarkSkyDailyForecastScheduled() {

        Iterable<City> cities = cityRepository.findAll();

        if(cities == null) {
            return;
        }

        for(City c : cities) {
            Forecast forecast = getDarkSkyDailyForecast(c);

            if(forecast == null) {
                continue;
            }
            convertUnitsDaily(forecast, true);
            dbClient.saveDailyForecast(c, forecast.getDaily().getData().get(0));
        }
    }
}
