// @description Show weather in a city
// @example !w Stockholm
// @example !w Houston, Texas

var page = API.getPage("http://api.wunderground.com/auto/wui/geo/WXCurrentObXML/index.xml?query=" + API.encode(param));
if (!page) {
	response = "Error fetching data. Please try again later.";
	response_to = channel;
} else {
	
	var city = API.getAsText(page, "//display_location/city");
	var state = API.getAsText(page, "//display_location/state_name");
	
	if (state == "") {
		response = "City not found.";
		response_to = channel;
	
	} else {
		//var country = API.getAsText(page, "//display_location/country_iso3166");
		var local_time = API.getAsText(page, "//local_time");
		var weather = API.getAsText(page, "//weather");
		var temp_c = API.getAsText(page, "//temp_c");
		var temp_f = API.getAsText(page, "//temp_f");
		var humid = API.getAsText(page, "//relative_humidity");
		var heatidx_c = API.getAsText(page, "//heat_index_c");
		var heatidx_f = API.getAsText(page, "//heat_index_f");
		var chill_c = API.getAsText(page, "//windchill_c");
		var chill_f = API.getAsText(page, "//windchill_f");
	
		var wind  = API.getAsText(page, "//wind_string");
		
		response = "";
		response += city;
		if (state != "") response += " (" + state + ") ";
		if (weather != "") response += weather + ". ";
		if (temp_c != "") {
			response += temp_c + "°C/";
			response += temp_f + "°F ";
		}
		if (heatidx_c  && heatidx_c != "NA") {
			response += "Heat index: " + heatidx_c + "/"+heatidx_f + ", ";
		}
		if (chill_c && chill_c != "NA" && !chill_c.equals(temp_c)) {
			response += "Wind chill: " + chill_c + "/"+chill_f + ", ";
		}
		if (wind) {
			response += " Wind: " + wind + ", ";
		}
		response += "humidity " + humid + " ";
		response += "on " + local_time + ". ";
	
		response_to = channel;
		page = null;
	}
}