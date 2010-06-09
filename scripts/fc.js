// @description Show weather in a city
// @example !weather Stockholm
// @example !weather Houston, Texas

API.debug("Param: " + param);
if (!param || param.length == "" || param == nick) {
	response = "Weather Forcast: Need a city name (and optionally country / country code. For example !fc Oslo NO)";
	response_to = channel;
} else {
	var xml = API.getPage('http://api.wunderground.com/auto/wui/geo/ForecastXML/index.xml?query='+API.encode(param));
	if (xml) { 
		var BOLD = String.fromCharCode(2);
		var CLR = String.fromCharCode(3);
		function colorize(str, color) {
			return CLR + color + str + CLR;
		}
		
		var timezone = null;
		var p = API.getAsText(xml, "//simpleforecast/forecastday/period");
		if (!p) {
			response_to = channel;
			response = "City not found.";
		} else {
			response_to = channel;
			response = "";
			for (var i=1; i<=3; i++) {
				var title = API.getAsText(xml, "//txt_forecast/forecastday["+i+"]/title");
				var fctext = API.getAsText(xml, "//txt_forecast/forecastday["+i+"]/fcttext");
		
				if (fctext != "") {
					response += title;
					response += " ";
					response += fctext;
					response += " ";
				} else {
					var wd = API.getAsText(xml, "//forecastday["+i+"]/date/weekday");
					if (wd) {
						var h = API.getAsText(xml, "//forecastday["+i+"]/date/hour");
						var m = API.getAsText(xml, "//forecastday["+i+"]/date/min");
						var loc = API.getAsText(xml, "//forecastday["+i+"]/low/celsius");
						var lof = API.getAsText(xml, "//forecastday["+i+"]/low/fahrenheit");
						var hic = API.getAsText(xml, "//forecastday["+i+"]/high/celsius");
						var hif = API.getAsText(xml, "//forecastday["+i+"]/high/fahrenheit");
						var cond = API.getAsText(xml, "//forecastday["+i+"]/conditions");
						
						// Cold
						if (loc <= 0) loc = colorize(loc, 11); 
						if (hic <= 0) hic = colorize(hic, 11); 
						// Warm
						if (loc >= 25) loc = colorize(loc, 4); 
						if (hic >= 25) hic = colorize(hic, 4); 
						// Normal
						if (loc > 0 && loc < 25) loc = colorize(loc, 9); 
						if (hic > 0 && hic < 25) hic = colorize(hic, 9);
						
						//response += wd + " " + h  + ":"  + m + ", " + cond + " " + loc+"/"+hic + "°C" + " (" + lof+"/"+hif+ "°F). ";
						response += BOLD + wd.substring(0, 3) + BOLD + " " + cond + " " + loc+"/"+hic + "°C" + " (" + lof+"/"+hif+ "°F) ";
						if (!timezone) {
							var tz = API.getAsText(xml, "//tz_long");
							if (tz) timezone = tz;
						}
					}
				}
			}
			response += "Sunrise/Sunset: " 
						+ API.getAsText(xml, "//moon_phase/sunrise/hour") + ":"+ API.getAsText(xml, "//moon_phase/sunrise/minute")
						+ "/"
						+ API.getAsText(xml, "//moon_phase/sunset/hour") + ":"+ API.getAsText(xml, "//moon_phase/sunset/minute");
			response = response + (timezone ? " ("+timezone + ")":"") ;	
		}
	}
}
