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
					example + " Only A-Z allowed. No spaces, no accents. If in doubt, ask the operators for help. Thanks and enjoy chat! :)");
} else {
	var gone = API.getValue("seen." + lnick + ".quit.time");
	var now = parseInt(new Date().getTime()/1000);
	// If was gpne more than 30 seconds
	if (gone && now - gone > 30) {
		var nums = API.getLineCounts(channel, [5, 15, 60]);
		var s = "Average number of lines said in the last ";
		while (nums.hasNext()) {
			var e = nums.next();
			var ratio = Math.round(e.getValue() / e.getKey()*100)/100;
			s += e.getKey() + "min => " + ratio + ", ";
		}
		s = s.substring(0, s.lastIndexOf(","));
		API.notice(nick, s);
	}
}