

var taunts = [
	"I love the smell of fresh napalm in the morning! And some bruised ass.",
	"And theeeeeere she goooooeeeeesss. See you on the other side.",
	"Score SCORE to the boot of doom!",
	"Tender meat on sale today!",
	"Ouch, that gotta hurt.",
	"I'd give that kick SCORE of 10",
	"Meh, I've seen better.",
	"Suckah!",
	"Is it just me, or is the temperature in here a bit too much?",
];

response = taunts[Math.floor(Math.random()*taunts.length)].replace(/SCORE/, Math.floor(Math.random())/10+1);
response_to = channel;
cancel = true;
