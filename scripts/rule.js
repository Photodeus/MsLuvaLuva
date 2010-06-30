// @description Link to rule x.x
// @example !rule 3.4
var m = param.match(/(\d\.\d)/);
if (m) {
	response = 'http://popodeus.com/chat/rules/#rule-' + m[1];
	response_to = channel;
} else {
	API.notice(nick, "Unrecognized rule. It should be in the form of n.n where n is a number");
}
