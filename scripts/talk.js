// @private
var tmp = param.split(' ', 2);
if (tmp[0].match(/\#[a-zA-Z]+/)) {
	bot.sendMessage(tmp[0], tmp[1]);
} else {
	bot.sendNotice(nick, "Usage: #channel message");
}