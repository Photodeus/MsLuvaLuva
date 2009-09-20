// @description You can send this command in the channel or as a private message to MsLuvaLuva and she will tell people to tone it down
var responses = [
	"Too much is too much. Less dirt, more finesse, please!",
	"Hey there, I'm talking to you. Yes YOU! You are being nasty. Now quit it.",
	"I didn't sign up for this! Why do I feel like I'm surrounded by sex-starved maniacs?",
	"Sex is fun. This isn't fun. Something must be wrong with this chat.",
	"Jeez!! Get a room. This is a public chat.",
	"I like dirty stuff. But not... this.",
	"Ahah! I think someone forgot to do a /msg KinkyGal Oh baby baby, let's do this in private.",
	"Who turned on the porno channel, can we switch back to discovery channel where they make this stuff sound interesting?",
	"You know what guys? Sometimes I think you all are a bit too gross.",
	"You know what guys? Sometimes I think you all are a bit too explicit!",
	"Wow they never taught us to say that in school. You guys really do know how to say the weirdest shit.",
	//	"- Tickle your ass with a feather? Huh? - I asked, \"Particulary nasty weather?\"",
];

var lnick = nick.toLowerCase();
API.info("gross by " + nick);
var last = API.getValue("gross." + lnick)
if (last && new Date().getTime() - last < 120000) {
	API.notice(nick, "Once is enough, aight?");
} else {
	response_to = "#Popmundo";
	response = responses[Math.floor(Math.random() * responses.length)];
}
API.setValue("gross." + lnick, new Date().getTime())
