// @description it's really just a !seen command, that asks the bot when someone was seen last online saying something
// @example !seen nickname
function relativeTime(time, sayAgo) {
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

var lnick = param.toLowerCase().replace(/_$/, '');
var nostalk = API.getValue("nostalk." + lnick);

if (param == nick) {

	API.notice(nick, 'Others can stalk you: ' + (nostalk ? "No" : "Yes (default)"));
	no_timeout = true;

} else if (param == '-help') {

	response = "To turn off !stalking of your nickname, please type in !stalk -off. To enable it again, use !stalk -on";
	response_to = channel;
	no_timeout = true;

} else if (param == '-off') {
	API.setValue("nostalk." + lnick, "on");
	API.info(nick + " opted out from stalking");
	response = "OK: Will not let others stalk you " + nick;
	response_to = channel;
	no_timeout = true;

} else if (param == '-on') {

	API.removeValue("nostalk." + lnick);
	API.info(nick + " is opting in to stalking");
	response = "OK: Stalking enabled again, " + nick;
	response_to = channel;
	no_timeout = true;

} else {

	if (nostalk) {
		response = "This user is not tracked. For more information, /msg " + API.getBotNick() + " !stalk -help";
	} else {

		var time = API.getValue("seen." + lnick + ".time"); // time is seconds, not millis
		var msg = API.getValue("seen." + lnick + ".msg");
		var qtime = API.getValue('seen.' + lnick + '.quit.time'); // seconds
		var qmsg = API.getValue('seen.' + lnick + '.quit.msg');
		//API.info(time + ", q: " + qtime);

		if (time && msg) {
			var dmsg = relativeTime(time, true);
			var qdata = "";
			if (qtime && qtime > time) {
				qdata = " (" + relativeTime(qtime, true) + ": " + qmsg + ")";
			}
			response = param + " was seen " + dmsg + " saying \"" + msg + "\"" + qdata;
		} else {
			if (param.match(/your /)) param = param.replace("your ", "my ");
			response = "Sorry, I haven't seen " + param + " after I joined channel " + relativeTime(API.getStartupTime() / 1000, true) + ".";
			time = API.getLastActiveTime(param, channel);
			if (time) {
				time = time.longValue() / 1000;
				response += " Last log entry I have is " + relativeTime(time, false) + " old.";
			}
		}
	}
	response_to = channel;
}
