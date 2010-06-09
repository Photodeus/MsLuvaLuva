// @description Info about your nick
function addCommas(num) {
	return num; // does nothing yet
}
function relativeTime(time, sayAgo) {
	if (time <= 0) {
		return "(Unknown time" + (sayAgo ? " ago" : "") + ")";
	}
	var delta = parseInt(new Date().getTime() / 1000 - time);
	var d, h, m, s;
	var ago = sayAgo ? " ago" : "";
	if (delta <= 60) {
		datemsg = Math.floor(delta) + " seconds" + ago;
	} else if (delta < 60 * 60) {
		m = Math.floor(delta / 60);
		s = Math.floor(delta) % 60;
		datemsg = m + " min " + s + " sec" + ago;
	} else if (delta < 24 * 60 * 60) {
		h = parseInt(delta / 3600);
		m = parseInt(delta / 60) % 60;
		s = delta % 60;
		//datemsg = h + " h and " + m + " min and " + s + "s ago";
		datemsg = h + " h and " + m + " min" + ago;
	} else {
		d = Math.floor(delta / 86400);
		h = Math.floor(delta / 3600) - d * 24;
		m = Math.floor(delta / 60) - d * 1440 - h * 60;
		s = Math.floor(delta) % 60;
		datemsg = d + " days and " + h + " hours" + ago;
	}
	return datemsg;
}

//API.info("Running info.js");

var now = parseInt(new Date().getTime()/1000);
var time = Math.floor(API.getStartupTime()/1000);
var ctime = Math.floor(API.getConnectTime()/1000);
var lnick = ""+nick.toLowerCase();
var joined = API.getValue("seen."+lnick + ".join.time")*1000;

API.action(channel, "up and running " + relativeTime(time, false) + ", " +
					"connected for " + relativeTime(ctime, false) + ". " +
					nick + " joined chat " + relativeTime(joined, true) + 
					". Memory Max/Free: " +
					API.formatNum(java.lang.Runtime.getRuntime().maxMemory()) + "/" + API.formatNum(java.lang.Runtime.getRuntime().freeMemory())
		);
//java.lang.System.gc();
//var mem = Math.floor(java.lang.Runtime.getRuntime().freeMemory()/1024);
//"Free mem: " + addCommas(mem) + " MB. " +

API.clearScriptCacheFor("info");