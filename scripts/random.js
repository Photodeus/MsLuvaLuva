// @description Gives you a random value between 1 and 6 unless upper limit is specified
// @example !random
// @example !random 100
var max = 6;
if (param.match(/\d+/)) {
	max = parseInt(param);
}
var val = (Math.floor(Math.random()*max)+1);
response = "Your random value between 1 - "+max+ ": " + val;
response_to = channel;