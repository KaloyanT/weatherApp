'use strict';

function buildHtmlTable(table, tableRow, tableHeader, tableData, array) {

  let tableLocal = table.cloneNode(false);
  let columns = addColumnHeaders(array, table, tableRow, tableHeader);

  for (let i = 0; i < array.length; i++) {

    let tr = tableRow.cloneNode(false);
    
    for (let j = 0; j < columns.length; j++) {
      let td = tableData.cloneNode(false);
      let cellValue = undefined;

      if(columns[j] === "timeStamp") {
        
        // JavaScript's Date constructor requires a timestamp in milliseconds and the API 
        // returns a timestamp in seconds, so multiply by 1000 to get the desired value
        let date = new Date(array[i][columns[j]] * 1000);
        cellValue = date.getDate() + "/" + (date.getMonth() + 1) + "/" + date.getFullYear();
      } else {
        cellValue = array[i][columns[j]];
      }
             
      td.appendChild(document.createTextNode(cellValue || ''));
      tr.appendChild(td);
    }

    table.appendChild(tr);
  }
  return table;
}


function addColumnHeaders(array, table, tableRow, tableHeader) {

  let columnSet = [];
  let tr = tableRow.cloneNode(false);

  for(let i = 0; i < array.length; i++) {
    for(let key in array[i]) {
      if(array[i].hasOwnProperty(key) && columnSet.indexOf(key) === -1) {
        columnSet.push(key);
        let formattedKey = convertHeaderKeys(key);
        let th = tableHeader.cloneNode(false);
        th.appendChild(document.createTextNode(formattedKey));
        tr.appendChild(th);
      }
    }
  }

  table.appendChild(tr);
  return columnSet;
}


function buildHtmlTableSingleObject(table, tableRow, tableHeader, tableData, json) {

  let tableLocal = table.cloneNode(false);
  let columns = addColumnHeadersSingleObject(json, table, tableRow, tableHeader);
  let tr = tableRow.cloneNode(false);

  for (let j = 0; j < columns.length; j++) {

  	let td = tableData.cloneNode(false);
    let cellValue = undefined;

    if(columns[j] === "timeStamp") {

    	// JavaScript's Date constructor requires a timestamp in milliseconds and the API
      // returns a timestamp in seconds, so multiply by 1000 to get the desired value
      let date = new Date(json[columns[j]] * 1000);
      cellValue = date.getDate() + "/" + date.getMonth() + "/" + date.getFullYear();
    } else {
    	cellValue = json[columns[j]];
      }

    td.appendChild(document.createTextNode(cellValue || ''));
    tr.appendChild(td);

  }

  table.appendChild(tr);
  return table;
}

function addColumnHeadersSingleObject(json, table, tableRow, tableHeader) {

  let columnSet = [];
    let tr = tableRow.cloneNode(false);
    for(let key in json) {
      if(key !== null && columnSet.indexOf(key) === -1) {
        columnSet.push(key);
          let th = tableHeader.cloneNode(false);
          let formattedKey = convertHeaderKeys(key);
          th.appendChild(document.createTextNode(formattedKey));
          tr.appendChild(th);
      }
    }

  table.appendChild(tr);
  return columnSet;
}

function convertHeaderKeys(header) {

  switch(header) {

    case 'timeStamp': 
      header = "date";
      break;
    case 'apparentTemperatureLow': 
      header = "apparent temp low";
      break;
    case 'apparentTemperatureHigh': 
      header = "apparent temp high";
      break; 
    case 'apparentTemperature': 
      header = "apparent temp";
      break;    
    case 'windSpeed':
      header = "wind speed";
      break;
    case 'precipProbability': 
      header = "precipitation probability";
      break;
    case 'precipIntensity':
      header = "precipitation intensity";
      break;
    case 'precipType':
      header = "precipitation type";
      break;
    case 'dewPoint': 
      header = "dew point";
      break;
    case 'cloudCover':
      header = "cloud cover";
      break;
    case 'darkSkyForecastId':
      header = "id";
      break;
    case 'temperatureLow':
      header = "temp low";
      break;
    case 'temperatureHigh':
      header = "temp high";
      break;
    case 'timezone':
			header = "time zone"; 
			break;          
  }

  return header;
}