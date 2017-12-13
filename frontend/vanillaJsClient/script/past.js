'use strict';

let forecastUrl = "http://localhost/api/forecast";

let JWT_TOKEN = "JWT_TOKEN";

function getToken() {
    return sessionStorage.getItem(JWT_TOKEN);
}


function getForecastDataFromAPI(URL, callback, singleObject) {

    let xhttp = new XMLHttpRequest();

    xhttp.onreadystatechange = function() {
        if(xhttp.readyState == XMLHttpRequest.DONE) {
            callback(xhttp.status, xhttp.responseText, singleObject);
        }
    }

    xhttp.open("GET", URL, true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.setRequestHeader("Authorization", getToken());
    xhttp.send();
}


function forecastDataCallback(status, data, singleObject) {

    let responseHeading = document.getElementById("responseHeading");

    if (responseHeading === null) {
    	console.log("Missing response heading element!")
    	return;
    }

    let dataJson = JSON.parse(data);

    let forecasts = null;
    let units = null;

    let table = document.createElement("table");
    let tableRow = document.createElement("tr");
    let tableHeader = document.createElement("th");
    let tableData = document.createElement("td");
    let forecastTable = null;

    if(singleObject === true) {
      forecastTable = buildHtmlTableSingleObject(table, tableRow, tableHeader, tableData, dataJson);
    } else {
      forecasts = dataJson["forecasts"];
      units = dataJson["units"];
      forecastTable = buildHtmlTable(table, tableRow, tableHeader, tableData, forecasts);
    }

    
    forecastTable.setAttribute("id", "table1");

    responseHeading.innerHTML = "Status code: " + status + "<br>";

    // If there is a previous table, remove it
    let oldTable = document.getElementById("table1");

    if(oldTable !== null) {
      oldTable.parentNode.removeChild(oldTable);
    }

    document.getElementById("tableDiv").appendChild(forecastTable);
}

function getAllForecastDataForCity() {

  if(getToken() === null) {
    window.location = "login.html";
  }

  let selector = document.getElementById("cityCountrySelect");
  let responseHeading = document.getElementById("responseHeading");
  let startDateEl = document.getElementById("startDate").value;
  let endDateEl = document.getElementById("endDate").value;

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
  let URL = forecastUrl;
  let startDate = null;
  let endDate = null;
    
  if(startDateEl == "" && endDateEl == "") {

    URL += "/get/all/" + city + "/" + country;
    getForecastDataFromAPI(URL, forecastDataCallback, false);

  } else if(startDateEl != "" && endDateEl == "") {
      
    startDate = new Date(startDateEl);
    startDate = startDate.getDate() + "." + (startDate.getMonth() + 1) + "." + startDate.getFullYear();
    let parameters = {
      date: startDate
   	}

    URL += "/get/by-date/" + city + "/" + country + formatRequestParameters(parameters);
    getForecastDataFromAPI(URL, forecastDataCallback, true);
    
  } else if(startDateEl != "" && endDateEl != "") {
      
    startDate = new Date(startDateEl);
    endDate = new Date(endDateEl);
    startDate = startDate.getDate() + "." + (startDate.getMonth() + 1) + "." + startDate.getFullYear();
    endDate = endDate.getDate() + "." + (endDate.getMonth() + 1) + "." + endDate.getFullYear();
    let parameters = {
      start: startDate,
      end: endDate
    }

    URL += "/get/between-dates/" + city + "/" + country + formatRequestParameters(parameters);
    getForecastDataFromAPI(URL, forecastDataCallback, false);

  }
}


function formatRequestParameters(parameters) {
  return "?" + Object.keys(parameters)
                     .map(function(key) {
                        return key + "=" + encodeURIComponent(parameters[key])
                     })
                     .join("&")
}
