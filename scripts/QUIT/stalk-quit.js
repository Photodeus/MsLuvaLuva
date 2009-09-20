var lnick = ""+nick.toLowerCase();

API.setValue("seen."+lnick + ".quit.time", Math.floor(new Date().getTime()/1000));
API.setValue("seen."+lnick + ".quit.msg", message);
