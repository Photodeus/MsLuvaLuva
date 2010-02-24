var lnick = ""+nick.toLowerCase();

// We don't like milliseconds, so we shave them off
var time = parseInt(new Date().getTime()/1000);
// Put information into persistent storage for later use and to be restored on bot restarts
API.setValue("seen."+lnick + ".join.time", parseInt(new Date().getTime()/1000));
