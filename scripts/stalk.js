// @private
// @description it's really just a !seen command, that asks the bot when someone was seen last online saying something
// @example !seen nickname
if (param == nick) {
	// ignore this
	bot.sendNotice(nick, 'Yes? Seen who, other than yourself.');
} else {

	var lnick = param.toLowerCase();
	var time = bot.getValue("seen." + lnick + ".time");
	var msg = bot.getValue("seen." + lnick + ".msg");

	if (time && msg) {
		var datemsg = "";
		var delta = (new Date().getTime() / 1000 - time);
		if (delta <= 120) {
			datemsg = Math.round(delta) + " seconds ago";
		} else if (delta < 60 * 60) {
			datemsg = Math.round(delta / 60) + " minutes ago";
		} else {
			var h = Math.round(delta / 60);
			var m = Math.round(delta) % 60;
			datemsg = h + " hours and " + m + " minutes ago";
		}
		response = param + " was seen " + datemsg + " saying \"" + msg + "\"";
	} else {
		response = "Sorry, no memory of " + param;
	}
	response_to = channel;
}