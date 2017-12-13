'use strict';

let currentForecastURL = "http://localhost/api/darksky/currently";

function getCurrentForecastDataFromAPI(URL, callback) {

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

    let table = document.createElement("table");
    let tableRow = document.createElement("tr");
    let tableHeader = document.createElement("th");
    let tableData = document.createElement("td");

    let forecastTable = buildHtmlTableSingleObject(table, tableRow, tableHeader, tableData, dataJson);
    forecastTable.setAttribute("id", "table1");

    responseHeading.innerHTML = "Status code: " + status + "<br>";

    // If there is a previous table, remove it
    let oldTable = document.getElementById("table1");

    if(oldTable !== null) {
      oldTable.parentNode.removeChild(oldTable);
    }

    document.getElementById("tableDiv").appendChild(forecastTable);
}



function getCurrentForecastForCity() {

    if(getToken() === null) {
    	window.location = "login.html";
    }

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
    let URL = currentForecastURL + "/" + city + "/" + country;

    getCurrentForecastDataFromAPI(URL, forecastDataCallback);
}
