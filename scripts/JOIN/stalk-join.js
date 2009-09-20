var lnick = ""+nick.toLowerCase();

var qtime = API.getValue('seen.' + lnick + '.quit.time');
var time = Math.floor(new Date().getTime()/1000);
if (time - qtime < 30) {
	// Quit and rejoin in less than 30 seconds
}
API.setValue("seen."+lnick + ".join.time", Math.round(new Date().getTime()/1000));
