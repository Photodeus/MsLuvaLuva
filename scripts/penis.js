// @description Totally useless... Why would anyone use this?
var val = Math.round((Math.random() * 28 + 6) * 10) / 10;
if (param.toLowerCase().indexOf('alanas') == 0 || param.toLowerCase().indexOf('ananas') == 0) {
	API.say(channel, "Sometimes, too much information can get you killed.... I refuse to answer this question.");
} else {
	// Vera gets special handling ;)
	if (param.toLowerCase() == 'verarose') val += 100;
	var meats = [
		"club of meat",
		"meat stick",
		"schlong-a-long",
		"pink snake",
		"one-eyed snake",
		"wang",
		"pocket rocket",
		"love missile",
		"little soldier",
		"pork stick",
		"babygun",
		"doinker",
		"member",
		"hog",
		"ding-dong",
		"power-drill",
		"mushroom",
		"pecker",
		"baton",
		"cannon",
		"lollipop",
		"pink sword",
		"phat stick",
		"totem pole",
		"third leg",
		"skin flute",
		"twig and berries",
		"\"mini me\"",
		"mortadella",
	];
	var styles = [
		"$nick is rumored to carry a $sizecm $item!",
		"$nick is has a $item that is of epic proportions: $sizecm!!",
		"I saw in the gym showers that $nick has a $sizecm $item!",
		"I'm quite positive $nick has a $sizecm $item tucked inside the pants",
		"I saw a naked photo of $nick on the internet. I believe the $item is at least $sizecm.",
		"I found a naked photo of $nick under $nack's mom's pillow! :O I estimate the $item to be $sizecm.",
		"I'd be dying if I'd be lying about $nick's $item. It's been measured to be exactly $sizecm.",
		"I'm sorry to say this, but $nick's $item is exactly $sizecm.",
		"It's not nice to gossip, but I really have to tell you that $nick's $item is $sizecm!",
		"Honestly, $nick has got a $sizecm $item!!",
		"Seriously, $nick is hiding a $sizecm $item in there.",
		"There's no need to lie about it. $nick has got a $sizecm $item!",
		"Fact: $nick is equipped with a $sizecm $item.",
		"Drumroll please.... I hereby announce that $nick is infact a proud owner of a $sizecm $item!",
	];

	if (Math.random() < 0.1) {
		response = param + " has no $item at all!".replace(/\$item/, meats[Math.floor(Math.random() * meats.length)]);
	} else {
		response = styles[Math.floor(Math.random() * styles.length)]
				.replace(/\$item/, meats[Math.floor(Math.random() * meats.length)])
				.replace(/\$size/, val)
				.replace(/\$nick/, param)
				.replace(/\$nack/, nick)
				;
	}
	response_to = channel;
}