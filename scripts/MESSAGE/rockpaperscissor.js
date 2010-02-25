var lnick = ""+nick.toLowerCase();
var x = API.getValue("seen."+lnick + ".join.time");
var delta = parseInt(new Date().getTime()/1000-x);
// If they joined no more than 20 seconds ago and say either rock, paper or scis, then
// we assume they got the game greeting from the bot. Let's play!
if (delta < 20) {
	var msg = message.toLowerCase();
	if (msg.indexOf('rock') >= 0 || msg.indexOf('paper') >= 0 || msg.indexOf('scis') >= 0 ) {
		response_to = channel;
		response = "I pick Chuck Norris, and Chuck always wins, ha ha. Loser!!";
	}
	if (msg.indexOf('chuck') >= 0) {
		response_to = channel;
		response = "Don't be lame, you can't pick Chuck. Cheater!";
	}
}

