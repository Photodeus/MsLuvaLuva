// @description Give a link to wikipedia containing the given word
// @example !wiki Styrofoam
var str = API.encode(param.substring(0, 1).toUpperCase()) + (param.length > 1 ? API.encode(param.substring(1)) : "");
response = "http://en.wikipedia.org/wiki/"+str;
response_to = channel;