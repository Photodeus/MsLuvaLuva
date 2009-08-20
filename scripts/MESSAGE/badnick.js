function WarningHolder() {
	this.warnings = [];
}
function Warning(nick, count) {
	this.nick = nick;
	this.count = count;
}

var BOLD = String.fromCharCode(2);
var lnick = "" + nick.toLowerCase();

bot.setValue("seen." + lnick + ".time", Math.round(new Date().getTime() / 1000));
bot.setValue("seen." + lnick + ".msg", message);

if (nick == 'Bot' || nick == 'MsLuvaLuva') {
	// Do nothing
} else {
	var isBad = lnick.match(/[0-9]|^[A-Z][a-z]+$/) || lnick.length < 4;
	if (isBad) {
		var examples = {
			'JoseTurdot' : 'José Türdöt',
			'LeylaTahti' : 'Léyla Tähti',
			'MimiBona' : 'Mími Boña',
			'IdaBettaKos' : 'Ida-Betta Koş'
		}
		var r = Math.floor(Math.random() * 4);
		var i = 0, bad;
		for (var good in examples) {
			if (i == r) {
				bad = examples[good];
				break;
			}
			++i;
		}
		var keyy = 'badnick.' + ident + host;
		var count = bot.getValue(keyy);
		if (!count) count = 0;
		bot.getLog().info(keyy + " ==> " + count);
		if (count % 4 == 0) {
			var example = "Example: " + bad + " would be " + BOLD + "/nick " + good + BOLD + " (only a-z and no spaces)";
			bot.sendNotice(nick,
					"Please write " + BOLD + "/nick FirstnameLastname" + BOLD +
					" You must use your Popmundo name in this chat. " + example);
		}
		++count;
		bot.setValue(keyy, count);
	} else {

		var warnings = bot.getValue('lowercase.warnings');
		if (!warnings) {
			warnings = { }
		}
		if (lnick == nick) {
			var warning = warnings[nick];
			if (!warning) {
				warning = new Warning(nick, 0);
			}
			++warning.count;
			//bot.getLog().info("Warning count: " + warning.count);

			if (warning.count % 5 == 0) {
				bot.sendNotice(nick, "Please don't use a lowercase nick while talking in channel.")
			}
			if (warning.count % 8 == 0) {
				bot.getLog().info(nick + " warning count: " + warning.count);
				var w = [
					"$nick, please fix your nick...",
					"$nick, fix your nick... <3",
					"$nick, are you away or are you here? Your nickname says you are away.",
					"$nick, should you stay or should you go?",
					"If you are gonna stay here and talk with us, fix your nickname $nick."
				];
				var line = w[ Math.floor(Math.random() * w.length) ];
				//bot.sendMessage(channel, nick + ", go away since you're already pretending to be away. Or fix your nick <3");
				bot.sendMessage(channel, line.replace(/\$nick/, nick));
			} else if (warning.count == 20) {
				bot.sendMessage(channel, nick + ", go away or fix your nickname! NOW!");
			} else if (warning.count == 30) {
				bot.sendMessage(channel, nick + ", go away and stay away!");
			}
			warnings[nick] = warning;
		} else {
			if (warnings && warnings[lnick]) {
				delete warnings[lnick];
			}
		}
		//bot.getLog().info("Warnings: " + warnings);
		bot.setValue('lowercase.warnings', warnings);
	}
}
