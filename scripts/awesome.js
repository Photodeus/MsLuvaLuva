var r = [
	"Oh yes you are $nick!",
	"You wish you were, $nick...",
];
response = r[Math.floor(Math.random()*r.length)].replace(/\$nick/, nick);
response_to = channel;