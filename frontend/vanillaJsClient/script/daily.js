'use strict';

let darkSkyDailyUrl = "http://localhost/api/darksky/daily/7-days";

let JWT_TOKEN = "JWT_TOKEN";

function getToken() {
    return sessionStorage.getItem(JWT_TOKEN);
}

window.addEventListener('load', initPage);

function initPage() {
	setCorrectDates(Math.floor(Date.now() / 1000));
	document.getElementById("forecastTableId").style.visibility = "hidden";
}


function getForecastDataFromAPI(URL, callback) {

  let xhttp = new XMLHttpRequest();

  xhttp.onreadystatechange = function() {
    if(xhttp.readyState == XMLHttpRequest.DONE) {
      callback(xhttp.status, xhttp.responseText);
    }
  }

  xhttp.open("GET", URL, true);
  xhttp.setRequestHeader("Content-type", "application/json");
  xhttp.setRequestHeader("Authorization", getToken());
  xhttp.send();
}


function forecastDataCallback(status, data) {

  let responseHeading = document.getElementById("responseHeading");

  if (responseHeading === null) {
    console.log("Missing response heading element!")
    return;
  }

  let dataJson = JSON.parse(data);
  let forecasts = dataJson["forecast"];
  let units = dataJson["units"];

  setCorrectDates(forecasts[0]["timeStamp"]);

  let selector = document.getElementById("cityCountrySelect");

  document.getElementById("cityAndCountryDiv").innerHTML = selector.options[selector.selectedIndex].text;
  document.getElementById("currentDayTemperature").innerHTML = Math.ceil(Number(forecasts[0]["temperatureMax"])) + " <sup>o</sup>C";
  document.getElementById("precipProbabilitySpan").innerHTML = '<img src="img/icon-umberella.png" alt="">' + forecasts[0]["precipProbability"] + "%";
  document.getElementById("windSpeedSpan").innerHTML = '<span id="windSpeedSpan"><img src="img/icon-wind.png" alt="">' + Math.ceil(Number(forecasts[0]["windSpeed"])) + " km/h";
  document.getElementById("humiditySpan").innerHTML = '<img src="img/icon-humidity.png" alt="">' + forecasts[0]["humidity"] + "%";
  document.getElementById("currentDayIcon").innerHTML = pickIcon(forecasts[0]["icon"], true);

  updateForecastTable(forecasts);

  document.getElementById("forecastTableId").style.visibility = "visible";
}


function updateForecastTable(forecastArray) {

  let dayIndex = 2;
  let tempSymbol = "<sup>o</sup>C"

  // The forecastArray contains the forecast for 8 days in total: The current day and the next 7 days
  for(let i = 1; i < 7; i++) {
    let currentDayHighTempDiv = `day${dayIndex}TemperatureHigh`;
    let currentDayLowTempDiv = `day${dayIndex}TemperatureLow`;
    let currentDayIconDiv = `day${dayIndex}Icon`;

    document.getElementById(currentDayHighTempDiv).innerHTML = Math.ceil(Number(forecastArray[i]["temperatureMax"])) + " <sup>o</sup>C";
    document.getElementById(currentDayLowTempDiv).innerHTML = Math.ceil(Number(forecastArray[i]["temperatureLow"])) + " <sup>o</sup>C";
    document.getElementById(currentDayIconDiv).innerHTML = pickIcon(forecastArray[i]["icon"], false);

    dayIndex++;
  }
}


function pickIcon(iconName, currentDay) {

  let width = 0;
  let height = 0;
  let alt = iconName.split("-").join(" ");

  if(currentDay === true) {
    width = 80;
    height = 60.75;
  } else {
    width = 75;
    height = 50;
  }

  let iconTag = '<img src="img/icons/' + iconName + '.svg" alt="' + alt + '" width=' + width + ' height=' + height + '>';
  return iconTag;
}


function setCorrectDates(startingDate) {

  let dayDiv = document.getElementById("currentDay");
  let dateDiv = document.getElementById("currentDate");
  let day2 = document.getElementById("day2");
  let day3 = document.getElementById("day3");
  let day4 = document.getElementById("day4");
  let day5 = document.getElementById("day5");
  let day6 = document.getElementById("day6");
  let day7 = document.getElementById("day7");

  let currentDate = new Date(startingDate);
  let days = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
  let months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
  let date = new Date();

  dayDiv.innerHTML = days[date.getDay()];
  dateDiv.innerHTML = date.getDate() + " " + months[date.getMonth()];

  day2.innerHTML = days[incrementCurrentDate(1).getDay()];

  day3.innerHTML = days[incrementCurrentDate(2).getDay()];

  day4.innerHTML = days[incrementCurrentDate(3).getDay()];

  day5.innerHTML = days[incrementCurrentDate(4).getDay()];

  day6.innerHTML = days[incrementCurrentDate(5).getDay()];

  day7.innerHTML = days[incrementCurrentDate(6).getDay()];

}

/*
 * Increments the date with the specified number of days. The current date + the number of days to add 
 * should not exceed the end of next month
*/
function incrementCurrentDate(increment) {

	let currentDate = new Date();
	let daysInMonth = new Date(currentDate.getFullYear(), currentDate.getMonth() + 1, 0).getDate();
	let incrementDate = currentDate.getDate() + increment;

	if(incrementDate > daysInMonth) {
		incrementDate = incrementDate % daysInMonth;		
		currentDate.setMonth(currentDate.getMonth() + 1);
		
	}

	currentDate.setDate(incrementDate);

	return currentDate;
}


function show7DayForecastForCity() {

  let selector = document.getElementById("cityCountrySelect");
  let responseHeading = document.getElementById("responseHeading");

  if(selector === null) {
    console.log("Missing city selector element!")
    return;
  } else if (responseHeading === null) {
    console.log("Missing response heading element!")
    return;
  }

  if(selector.options.length === 0) {
    responseHeading.innerHTML = "There are no cities and countries in the list!";
    return;
  }

  let cityAndCountry = selector.options[selector.selectedIndex].text;

  if(cityAndCountry === null || cityAndCountry.trim() == "") {
    responseHeading.innerHTML = "Invalid city or country";
    return;
  }

  cityAndCountry = cityAndCountry.split(", ");

  let city = cityAndCountry[0];
  let country = cityAndCountry[1];
  let URL = darkSkyDailyUrl + "/" + city + "/" + country;

  getForecastDataFromAPI(URL, forecastDataCallback, city, country);
}