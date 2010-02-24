var lnick = ""+nick.toLowerCase();

API.setValue("seen."+lnick + ".quit.time", parseInt(new Date().getTime()/1000));
API.setValue("seen."+lnick + ".quit.msg", message);
