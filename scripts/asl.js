// @description Search and display Age/Sex/Location information about a Popmundo character.
// @description Querying A/S/L for anyone is so lame, that's why this is so cool.  
// @example !asl Jonas Zapatero
// @example !asl 3 Mark Ward
var insults = [
	"F**k off, creep! You're scaring me.",
	"Get one step closer and I'm reporting you to the authorities.",
	"You hittin' on me, buttface? Well I don't like it.",
	"Who I am and where I am is none of your business.",
	"Get lost, jerktard.",
	"I got only one reply to you: Get lost.",
	"I have a gun, and I'm not afraid to use it.",
	"I have a knife, and I'm afraid I'll use it if you ask any more stupid questions.",
	"You deserve to be stabbed for asking silly stuff like that!",
	"I'm not gonna be your friend, so don't you even try to be one either.",
	"You keep asking, I keep stabbing.",
	"asl = a stabbable loser? Well yes, that's what you are. *stabs with rusty knife*",
	"Chuck Norris would cry if I told you that. And Chuck never cries.",
	"Go wank somewhere else. I'm not your bimbo.",
	"You're not good enough for me, so there's no point in telling you.",
	"Feeling lonely tonite? Guess what, you're not gonna get any from me either!",
	"I'm old enough to be your granny! Wanna roll in the hay, eh? ;)",
	"Eww, go away, I'm young enough to be your granddaughter!",
	"A: Secret, S: I get plenty, more than you. Ha! L: Far far away from you, idiot.",
	"A: More than your IQ. S: Stands for sexy, which I know I am. L: Right in front of your ugly face.",
	"I wish my location was somewhere else than here, so I wouldn't have to see you.",
	"Take a hike, pervert.",
	"It's \"abc\", you illiterate fool. Not \"asl\".",
];

if (param == API.getBotNick()) {
	response = insults[Math.floor(insults.length * Math.random())];
	response_to = channel;
} else {
	var line = API.getPageAsText("http://popodeus.com/namesearch/find.jsp?asl=" + API.encode(param));
	if (line) API.say(channel, line);
}
