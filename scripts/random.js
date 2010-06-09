// @description Gives you a random value between 1 and 6 unless upper limit is specified
// @example !random
// @example !random 100
// @example !random milk beer soda water
var BOLD = String.fromCharCode(2);

if (param == '-help') {
	response = "!random [number | list of items to pick from] example !random kiss tickle punch"
}
if (param == nick || param == ' ' || param.match(/^\d+$/)) {
	max = param.match(/^\d+$/) ? Math.abs(parseInt(param)) : 6;
	if (max == 2) {
		response = "Randomly picking from [Yes or No]: " + BOLD + (Math.random() < 0.5 ? 'Yes' : 'No') + BOLD;
	} else {
		var val = (Math.floor(Math.random() * max) + 1);
		response = "Your random value between 1 - " + max + ": " + BOLD + val + BOLD;
	}
} else {
	var arr = param.split(' ');
	if (arr.length == 1) {
		response = nick + " is stupid. I'll pick.... \"" + param + "\"!";
	} else {
		response = arr[Math.floor(arr.length * Math.random())];
	}
}

response_to = channel;
