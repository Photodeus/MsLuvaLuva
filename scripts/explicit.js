// @description Send this command in the channel or as a private message to MsLuvaLuva and she will ask people to tone down on explicit talk.
var responses = [
	"Yowza! Please take overly graphic and explicit conversations to private messaging or create another channel for it.",
];

var lnick = nick.toLowerCase();
API.info("explicit by " + nick);
if (!explicit) explicit = { };
var last = explicit[lnick];
if (last && new Date().getTime() - last < 120000) {
	API.action(nick, "Once is enough, aight?");
} else {
	response_to = "#Popmundo";
	response = responses[Math.floor(Math.random() * responses.length)];
}
explicit[lnick] = new Date().getTime();
