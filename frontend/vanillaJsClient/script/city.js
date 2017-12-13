'use strict'; 

let cityURL = "http://localhost/api/city";

let TOKEN = "JWT_TOKEN";

window.addEventListener('load', populateCityCountrySelect);

function getToken() {
    return sessionStorage.getItem(TOKEN);
}


function getAllCitiesFromAPI(URL, callback) {

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


function cityCallback(status, citiesArray) {

	if(status !== 200) {
		console.log("API Error! Can't get cities.");
		return;
	}

	let cityCountrySelector = document.getElementById("cityCountrySelect");

	if(cityCountrySelector === null) {
    	return;
    }

    if(citiesArray === null) {
    	console.log("Missing city data!");
    	return;
    }

	let cityJSONArray = JSON.parse(citiesArray);
	let cityCountryArray = [];

	cityJSONArray.forEach(function(cityObj) {
		cityCountryArray.push(cityObj["name"] + ", " + cityObj["country"]);
	});

	let fragment = document.createDocumentFragment();
	cityCountryArray.forEach(function(cityCountry, index) {
		let opt = document.createElement('option');
		opt.value = index;
		opt.innerHTML = cityCountry;
		fragment.appendChild(opt);
	});

	cityCountrySelector.appendChild(fragment);
}


function populateCityCountrySelect() {

	if(getToken() === null) {
		window.location = "login.html";
	}

	let URL = cityURL + "/get/all";

	getAllCitiesFromAPI(URL, cityCallback);

}

