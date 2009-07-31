// @description Op command only. Allows to set the minimum timeout in seconds between bot commands
// @example !timeout 10
bot.getLog().info(nick + " prefix: " + bot.getPrefix(channel, nick));
lnick = nick.toLowerCase();
if (lnick == 'maryjanelopes' || lnick == 'alanasanikonis' || lnick=='verarose' || lnick =='barendarets' || lnick=='brendaarets'
   ||  bot.isOp(nick, channel) || bot.isOwner(nick, channel)) {
	if (param.match(/\d+/)) {
		var time = parseInt(param.match(/\d+/));
		if (time > 0 && time <= 120) {
			bot.sendNotice(nick, "Ok: Setting command timeout to " + time + " seconds");
			bot.setTimeout(time * 1000);
		}
	} else {
		bot.sendNotice(channel, "Command timeout is " + (bot.timeout/1000) + " seconds");
	}
} else {
	bot.sendNotice(nick, 'Are you op?');
}
