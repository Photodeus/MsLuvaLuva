var BOLD = String.fromCharCode(2);
var lnick = ""+nick.toLowerCase();

//var onecapital = lnick.match(/^[A-Z][a-z]+$/);
var isBad = lnick.match(/^mib_/);

if (isBad) {
	var examples = {
		'JoseOTurdot' : "José O'Türdöt",
		'LeylaTahti' : 'Léyla Tähti',
		'MimiOBona' : "Mími O'Boña",
		'IdaBettaKos' : 'Ida-Betta Koş'
	}
	var r = Math.floor(Math.random()*4);
	var i=0, bad;
	for (var good in examples) {
		if (i == r) {
			bad = examples[good];
			break;
		}
		++i;
	}
	var keyy = 'badnick.'+ident+host;
	var example = "Example: "+bad+" would be "+BOLD+"/nick "+good+BOLD;
	bot.sendNotice(nick, "Welcome Mibbit user! Your nickname seems to be improper and needs to be fixed.");
	bot.sendNotice(nick, "To change your nickname, please write " + BOLD + "/nick FirstnameLastname" + BOLD +
					 " Channel rule is that you "+BOLD+"must"+BOLD+" use your Popmundo name. " + example +
					" Only A-Z and a-z allowed. No spaces :)");
	/*
	bot.sendMessage(channel, "Welcome Mibbit user! Your nickname seems to be improper and needs to be fixed.");
	bot.sendMessage(channel, "To change your nickname, please write " + BOLD + "/nick FirstnameLastname" + BOLD +
					 " Channel rule is that you "+BOLD+"must"+BOLD+" use your Popmundo name. " + example +
					" Only A-Z and a-z allowed. No spaces :)");
	*/
}