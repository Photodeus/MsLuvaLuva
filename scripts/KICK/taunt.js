
var taunts = [
	"I love the smell of fresh napalm in the morning! And some bruised ass.",
	"And theeeeeere she goooooeeeeesss. See you on the other side.",
	"Score SCORE to the boot of doom!",
	"Tender meat on sale today!",
	"Ouch, that gotta hurt.",
	"Lol!!1 That gotta hurt like hell.",
	"I'd give that kick SCORE of 10",
	"Meh, I've seen better.",
	"Suckah!",
	"Good job! I didn't even like that person.",
	"I bet we'll see them come crawling back for more punishment.",
	"Is it just me, or is the temperature in here a bit too much?",
	"I shouldn't say this, but that was funny \\:D/",
	"I couldn't have said it better!",
	"Out of sight, out of mind.",
	"And that, my friends, is how we solve our problems.",
	"Kids, that's what happens when you do drugs. Let it be a warning to all of you.",
	"Yippie kay ee!",
];

response = taunts[Math.floor(Math.random()*taunts.length)].replace(/SCORE/, Math.floor(Math.random())/10+1);
response_to = channel;
cancel = true;

var lnick = ""+nick.toLowerCase();
API.setValue("seen."+lnick + ".quit.time", parseInt(new Date().getTime()/1000));
// TODO needs to store kick reason. It should be in the message variable...
API.setValue("seen."+lnick + ".quit.msg", '[kicked]');
