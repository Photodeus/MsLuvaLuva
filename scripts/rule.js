// @description Link to rule x.x
// @example !rule 3.4
var m = param.match(/rule.(\d\.\d)/);
if (m) {
	response = 'http://popodeus.com/chat/rules/#' + m[1];
	response_to = channel;
}