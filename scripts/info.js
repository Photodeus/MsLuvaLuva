// @description Info about your nick
API.info("Running info.js");
var time = Math.floor((new Date().getTime()-API.getStartupTime())/1000);
var ctime = Math.floor((new Date().getTime()-API.getConnectTime())/1000);
java.lang.System.gc();
var mem = java.lang.Runtime.getRuntime().freeMemory();
response = "Bot has been up and running " + time + " seconds, and connected " + ctime + " seconds. Free mem: " + mem;
response_to = channel
API.clearScriptCacheFor("info");