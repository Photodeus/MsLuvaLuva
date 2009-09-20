// @description it's really just a !seen command, that asks the bot when someone was seen last online saying something
// @example !seen nickname
function relativeTime(time) {
	var delta = Math.floor(new Date().getTime() / 1000 - time);
	var d, h, m, s;
	if (delta <= 60) {
		datemsg = Math.floor(delta) + " seconds ago";
	} else if (delta < 60 * 60) {
		m = Math.floor(delta / 60);
		s = Math.floor(delta) % 60;
		datemsg = m + " min " + s + " sec ago";
	} else if (delta < 24 * 60 * 60) {
		h = parseInt(delta / 3600);
		m = parseInt(delta / 60) % 60;
		s = delta % 60;
		//datemsg = h + " h and " + m + " min and " + s + "s ago";
		datemsg = h + " h and " + m + " min ago";
	} else {
		d = Math.floor(delta / 86400);
		h = Math.floor(delta / 3600) - d * 24;
		m = Math.floor(delta / 60) - d * 1440 - h * 60;
		s = Math.floor(delta) % 60;
		datemsg = d + " days and " + h + " hours ago";
	}
	return datemsg;
}

var lnick = param.toLowerCase();
var nostalk = API.getValue("nostalk." + lnick);

if (param == nick) {
	// ignore this
	API.notice(nick, 'Yes? Seen who, other than yourself. Others can stalk you: ' + nostalk);
	no_timeout = true;

} else if (param == '-off') {
	API.setValue("nostalk." + lnick, 1);
	//API.info(nick + " opted out from stalking");
	no_timeout = true;
	response = "OK: Will not let others stalk you " + nick;
	response_to = channel;

} else if (param == '-on') {
	API.setValue("nostalk." + lnick, 0);
	//API.info(nick + " opted in to stalking");
	no_timeout = true;

	response = "OK: Stalking enabled again, " + nick;
	response_to = channel;

} else {

	//API.info("nostalk."+lnick+": " + nostalk);
	if (nostalk || lnick.indexOf('alanas') >= 0 || lnick.indexOf('photodeus') >= 0 ) {
		response = "I'll be back in January 2010.";
		API.info("\n\n" + nick + " tried to stalk " + param + "\n\n");
	} else {

		var time = API.getValue("seen." + lnick + ".time");
		var msg = API.getValue("seen." + lnick + ".msg");
		var qtime = API.getValue('seen.' + lnick + '.quit.time');
		var qmsg = API.getValue('seen.' + lnick + '.quit.msg');
		//API.info(time + ", q: " + qtime);

		if (time && msg) {
			var dmsg = relativeTime(time);
			var qdata = "";
			if (qtime && qtime > time) {
				qdata = " (" + relativeTime(qtime) + ": " + qmsg + ")";
			}
			response = param + " was seen " + dmsg + " saying \"" + msg + "\"" + qdata;
		} else {
			response = "Sorry, no memory of " + param;
		}
	}
	response_to = channel;
}

//API.clearScriptCacheFor("stalk");