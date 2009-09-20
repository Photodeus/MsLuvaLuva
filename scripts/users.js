// @private
// @description Don't use this....
var p = message.split(' ', 2);
var users = API.getUsers(channel);
var hosts = { };
var s = "";
for (var i=0; i<users.length; i++) {
	var u = users[i];
	var un = ""+u.toString();
	s += un + (i<users.length-1?", ":"");
}
response = s;
//response = "v: " + API.hasVoice(channel, p[1]) + ", o: " + API.isOp(channel, p[1]);
//nick + " prefix: " +
//response_to = channel;
response_to = nick;
