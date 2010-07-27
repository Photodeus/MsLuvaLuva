// @description Totally useless...
var r = [
	"Yours is a big one $nick",
	"I bet you inherited yours from a baboon, $nick!",
	"Spankable, baby! Spankable!",
	"Synonym for you, $nick!",
	"Stop shouting your nickname in here, it makes you look silly.",
	"Yea? If you want some, that'll make 150 dollars $nick. Cash up front.",
	"Who are you trying to impress, $nick. You're just being an ass today.",
	"Well aren't we a bit childish today?",
	"I bet you'd fancy some...",
];
response = r[Math.floor(Math.random()*r.length)].replace(/\$nick/, nick);
response_to = channel;