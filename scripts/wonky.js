// @description MsLuvaLuva is feeling really wonky in her pants. What is she dreaming of right now?
// @example !wonky

var words = [
	"eyeball",
	"fake fang",
	"vampire fang",
	"gorilla fang",
	"cheek",
	"stubbled cheek",
	"chin",
	"thumb",
	"pinky",
	"fist",
	"knuckle",
	"belly",
	"navel",
	"groin",
	"forehead",
	"tongue",
	"arm",
	"toe",
	"hip",
	"nose",
	"lip",
	"mouth",
	"ear",
	"neck",
	"throat",
	"shoulder",
	"bicep",
	"elbow",
	"hand",
	"palm",
	"chest",
	"boobie",
	"titty",
	"cleavage",
	"knee",
	"ribcage",
	"leg",
	"foot",
	"ball",
	"hairy ball",
	"shaved ball",
	"****",
	"alligator",
	"fish",
	"whale",
	"zombie",
	"vampire",
	"rubber duck",
	"latex glove",
	"wizard hat",
	"nipple",
	"leather mask",
	"bacon",
	"red robe",
	"Connie doll",
	"Chuck Norris doll",
	"Kobe Tai doll",
	"black hole",
	/*
	 // BAAAAD
	 "arse",
	 "foreskin",
	 "anus",
	 "skin",
	 "sextoy",
	 "dildo",
	 "pussy",
	 "pussylip",
	 "cunt",
	 "dog",
	 "gagball",
	 */
];

var actions = [
	"caressing",
	"whipping",
	"tickling",
	"licking",
	"kissing",
	"whacking",
	"biting",
	"eating",
	"twisting",
	"pulling",
	"hugging",
	"clawing",
	"gnawing",
	"chewing",
	"squeezing",
	 "holding",
	 //	"spreading",
	 //	"fapping",
	 //	"f***ing",
	 //	"sucking",
	"french kissing",
	"bondage",
	"slapping",
	"ripping",
	"burning",
	"freezing",
	"punching",
	"strangling",
	"breaking",
	"crushing",
	"brushing",
	"mocking",
	"pumpin'",
	"jizzing",
	"squirting",
	"$stuff creaming",
	"massaging",
	"candle waxing",
	"pissing",
	"swallowing",
	"groping",
	"shaking",
	//"shaving",
	"kicking",
	"pinching",
	"sponge washing",
	"washing with $stuff",
	"covering in $stuff",
	"nibbling",
	"humpin'",
];
var stuff = [
	"chocolate",
	"honey",
	"burning acid",
	"raspberry jam",
	"shampoo",
];
var levels = [
	"painful",
	"drugged",
	"joyful",
	//	"oversexual",
	"blissful",
	"furious",
	"gentle",
	"sexy",
	"reckless",
	"arrogant",
	"porno",
	"deranged",
	"mad",
	"amazing",
	"cheap",
	"nasty",
	"orgasmic",
	"feisty",
	"innocent",
	"cute",
	"glorious",
	"funtastic",
	"psycho",
	"tantric",
	"exciting",
	"dripping",
	"surprise",
	"secret",
	"boring",
	"dangerous",
	"forbidden",
	"illegal",
	"loud",
	"frantic",
	"sloppy",
	"slutty",
	"smelly",
	"spasmic",
	"sweaty",
	"slippery",
	"exposed",
	"greasy",
	"freaky",
	"punk rock",
	"rock'n roll",
	"S&M",
	"far out",
	"over the top",
	"dominating",
	"romantic",
	"loving",
	"hateful",
	"super friendly",
	"slimy",
	"bloody",
	"normal",
	"gangsta",
	"nocturnal",
	// BAD
	"horny",
];


var lines = [
	"I'm curious about trying out some $level $target-$act with $nick!",
	"Would you consider some $level $target-$act with me, $nick?",
	//"How about $level $act of $target, $nick? I bet you'd like that.",
	"$nick, how about some $target-$act, $level style?",
	"I am in the mood for a little of $target-$act! Or is it too \"$level\" for you, $nick?",
	"Let's do $level $target-$act, $nick!",
	"My fantasy is to have $nick go all $level and then do some $target-$act...",
	"I'm longing for some $target-$act with $nick.",
	"I could do some $target-$act with $nick tonight!",
	"$nick and me should go for some $level $target-$act... ;)",
	"Hey everyone, let's try some $nick-$act!!",
	"Hey everyone, let's try some $nick-$act!! With extra $stuff on top.",
	"I'm feeling a bit $level tonight. How about some $target-$act, $nick?",
	"$level $target $act with $nick!",
	"Me and you $nick, $level $target-$act, now!",
	"Oh $nick! Let's do some $level $target-$act, please?",
];
//if (!param) param = nick;
if (param == '-info') {
	response = "I can come up with " + (words.length * actions.length * levels.length) + " variations of things to do! Imaginative, huh?";
} else {
	param = nick;
	var line = lines[Math.floor(Math.random() * lines.length)];
	line = line.replace(/\$target/, words[Math.floor(Math.random() * words.length)])
			.replace(/\$act/, actions[Math.floor(Math.random() * actions.length)])
			.replace(/\$level/, levels[Math.floor(Math.random() * levels.length)])
			.replace(/\$stuff/, stuff[Math.floor(Math.random() * stuff.length)])
			.replace(/\$nick/, param)
			;
	response = line;
}
response_to = channel;
