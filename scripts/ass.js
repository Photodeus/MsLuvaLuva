// @description Totally useless...
var r = [
	"Yours is a big one $nick",
	"I bet you inherited yours from a baboon, $nick!",
	"Spankable, baby! Spankable!",
	"Synonym for you, $nick!",
	"That'll make 50 dollars $nick. Cash up front.",
	"Who are you trying to impress, $nick. You're just being an ass today.",
];
response = r[Math.floor(Math.random()*r.length)].replace(/\$nick/, nick);
response_to = channel;