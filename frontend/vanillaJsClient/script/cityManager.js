'use strict'; 

let cityURL = "http://localhost/api/city";

let TOKEN = "JWT_TOKEN";


window.addEventListener('load', checkLogin);
document.getElementById('form-cityInsert').addEventListener('submit', insertCity);

function getToken() {
    return sessionStorage.getItem(TOKEN);
}


function checkLogin() {

    if(getToken() === null) {   
        window.location = "login.html";
    }
}

function insertCity(e) {
	
    if(getToken() === null) {   
        window.location = "login.html";
    }

	e.preventDefault();

	let form = document.forms["form-cityInsert"];
    let cityName = form["inputCityName"].value;
    let countryName = form["inputCountryName"].value;
    let cityLatitude = form["inputCityLatitude"].value;
    let cityLongitude = form["inputCityLongitude"].value;
    let responseHeading = document.getElementById("responseHeading");

    // Clear old error messages
    responseHeading.innerHTML = "";
    responseHeading.style.color = "red";

    if(cityName === null || cityName === undefined || cityName.trim().length === 0 || !cityName.match(/^[a-zA-Z\s]*$/)) {
    	responseHeading.innerHTML = "Invalid city name";
    	return;
    }

    if(countryName === null || countryName === undefined || countryName.trim().length === 0 || !countryName.match(/^[a-zA-Z]+$/)) {
    	responseHeading.innerHTML = "Invalid country name";
    	return;
    }

    if(isNaN(cityLongitude) || (cityLongitude > 180 || cityLongitude < -180))  {
    	responseHeading.innerHTML = "Invalid longitude";
    	return;
    }

    if(isNaN(cityLatitude) ||  (cityLatitude > 85 || cityLatitude < -85) ) {
    	responseHeading.innerHTML = "Invalid latitude";
    	return;
    }
  
    responseHeading.style.color = "black";

    let cityData = {
        name: cityName,
       	country: countryName,
       	latitude: cityLatitude,
       	longitude: cityLongitude
    }

	let URL = cityURL + "/insert";

    insertCityInDb(JSON.stringify(cityData), URL, insertCityCallback);
}


function insertCityInDb(cityData, URL, callback) {

	let xhttp = new XMLHttpRequest();

    xhttp.onreadystatechange = function() {
        if(xhttp.readyState == XMLHttpRequest.DONE) {
            callback(xhttp.status, xhttp.responseText);
        }
    }

    xhttp.open("POST", URL, true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.setRequestHeader("Authorization", getToken());
    xhttp.send(cityData);

}


function insertCityCallback(status, data) {

    if(status !== 201) {
        console.log("API Error! Can't insert city.");
        return;
    }

    let responseHeading = document.getElementById("responseHeading");

    if (responseHeading === null) {
        console.log("Missing response heading element!")
        return;
    }

    responseHeading.innerHTML = "Status code: " + status + "<br>";
}