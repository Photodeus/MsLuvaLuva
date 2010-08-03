// @private
var tmp = param.match(/(\#?[a-zA-Z]+)\s(.+)/);
if (tmp) {
	if (API.isOwner(tmp[1], nick) || API.isOp(tmp[1], nick) || nick == "AlmaDD" || nick == "AlmaDavidsson") {
		API.say(tmp[1], tmp[2]);
	}
}
no_timeout = true;
