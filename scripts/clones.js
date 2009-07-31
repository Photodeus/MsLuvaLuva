// @description Doesn't work
var channel = '#Popmundo';
var p = message.split(' ', 2);
var users = bot.getUsers(channel);
var hosts = { };
var s = "";
for (var i=0; i<users.length; i++) {
	var u = users[i];
	//s += u.getPrefix()+u.getNick() + ", ";
	if (hosts[u.getLogin()]) {
		s += u.getNick() + ", ";
	} else {
		hosts[u.getLogin()] = u.getNick();
	}
}
response = s;
//response = "v: " + bot.isVoice(channel, p[1]) + ", o: " + bot.isOp(channel, p[1]);
//nick + " prefix: " +
response_to = channel;