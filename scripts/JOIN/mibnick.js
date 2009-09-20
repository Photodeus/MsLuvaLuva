var BOLD = String.fromCharCode(2);
var lnick = ""+nick.toLowerCase();

var isBad = nick.match(/^mib_|^[A-Z][a-z]+$|^[a-z]+$/);
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
	var example = "Example: \""+bad+"\" would be "+BOLD+"/nick "+good+BOLD;
	API.notice(nick, "Welcome " + nick +"! Your nickname seems to be improperly written and needs to be fixed if you wish to remain in chat. " +
					 "To change your nickname, please write " + BOLD + "/nick FirstnameLastname" + BOLD);
	API.notice(nick,  "Channel rule is that you "+BOLD+"must"+BOLD+" use your "+BOLD+"Popmundo"+BOLD+" name. " + 
					example + " Only A-Z allowed. No spaces, no accents. If in doubt, ask the operators for help. Thanks and have a good chat! :)");
	/*
	API.say(channel, "Welcome Mibbit user! Your nickname seems to be improper and needs to be fixed.");
	API.say(channel, "To change your nickname, please write " + BOLD + "/nick FirstnameLastname" + BOLD +
					 " Channel rule is that you "+BOLD+"must"+BOLD+" use your Popmundo name. " + example +
					" Only A-Z and a-z allowed. No spaces :)");
	*/
}