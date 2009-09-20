// @description Show weather in a city
// @example !weather Stockholm
// @example !weather Houston, Texas
var xml = API.getPage('http://api.wunderground.com/auto/wui/geo/ForecastXML/index.xml?query='+API.encode(param));

var p = API.getAsText(xml, "//simpleforecast/forecastday/period");
if (!p) {
	response_to = channel;
	response = "City not found";
} else {
	response_to = channel;
	response = "";
	var timezone;
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
			if (wd != null) {
				var h = API.getAsText(xml, "//forecastday["+i+"]/date/hour");
				var m = API.getAsText(xml, "//forecastday["+i+"]/date/min");
				var loc = API.getAsText(xml, "//forecastday["+i+"]/low/celsius");
				var lof = API.getAsText(xml, "//forecastday["+i+"]/low/fahrenheit");
				var hic = API.getAsText(xml, "//forecastday["+i+"]/high/celsius");
				var hif = API.getAsText(xml, "//forecastday["+i+"]/high/fahrenheit");
				var cond = API.getAsText(xml, "//forecastday["+i+"]/conditions");

				response += wd + " " + h  + ":"  + m + ", " + cond
				+ " " + loc+"/"+hic + "°C" + " (" + lof+"/"+hif+ "°F). ";
				if (timezone == null) {
					var tz = API.getAsText(xml, "//tz_long");
					if (tz != null) timezone = tz;
				}
			}
		}
	}
	response = (timezone != null?timezone+ ": ":"") + response;	
}
