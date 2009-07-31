// @private
// @description Don't use this....
var p = message.split(' ', 2);
var users = bot.getUsers('#Popmundo');
var hosts = { };
var s = "";
for (var i=0; i<users.length; i++) {
	var u = users[i];
	var un = ""+u.getNick();
	/*
	//if (un == nick) continue;
	//if (un.indexOf('Bot') >= 0) continue;
	//if (un.indexOf('MsLuvaLuva') >= 0) continue;
	//s += i + ': ' + u.getPrefix()+u.getNick() + ", ";
	var tmp = "";
	if (un.match(/^[^A-Za-z]/)) {
		tmp = un;
	} else {
		var prefix = bot.getPrefix(channel, un);
		if (prefix && prefix.match(/^[^A-Za-z]/)) {
			tmp = prefix + un;
		}
	}
	if (tmp) {
		s += i + ' ' + un + (i<users.length-1?", ":"");
	}
	*/
	s += un + (i<users.length-1?", ":"");
}
response = s;
//response = "v: " + bot.isVoice(channel, p[1]) + ", o: " + bot.isOp(channel, p[1]);
//nick + " prefix: " +
//response_to = channel;
response_to = nick;
