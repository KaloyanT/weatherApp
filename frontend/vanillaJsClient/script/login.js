'use strict';

let loginURL = "http://localhost/api/auth";

let JWT_TOKEN = "JWT_TOKEN";

let JWT_PREFIX = "Bearer ";

document.getElementById('form-login').addEventListener('submit', login);

function getToken() {
    return sessionStorage.getItem(JWT_TOKEN);
}

function setToken(token) {
    sessionStorage.setItem(JWT_TOKEN, token);
}

function removeToken() {
    sessionStorage.removeItem(JWT_TOKEN);
}

function login(e) {
    e.preventDefault();

    let form = document.forms["form-login"];
    let username = form["inputUsername"].value;
    let password = form["inputPassword"].value;

    let loginData = {
        username: username,
        password: password
    }

    doLogin(JSON.stringify(loginData), loginCallback);
}

function doLogin(loginData, callback) {

    let xhttp = new XMLHttpRequest();

    xhttp.onreadystatechange = function() {
        if(xhttp.readyState == XMLHttpRequest.DONE) {
            callback(xhttp.status, xhttp.responseText);
        }
    }

    xhttp.open("POST", loginURL, true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(loginData);
}

function loginCallback(status, data) {

    if(status === 200) {
        let token = JSON.parse(data);
        setToken(JWT_PREFIX + token["token"]);
        window.location = "./index.html";
    } else if (status === 401) {
        alert("Wrong Username or Password");
        return; 
    }
}