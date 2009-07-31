// @description	Shows definitions from Urban dictionary
// @example !ud jinxed
param = ""+bot.encode(param);
var text = bot.fetchUrl('http://popodeus.com/chat/bot/ud?' + param);
var lines = text.split("\n", 2);
//bot.sendMessage(channel, ""+lines[0]);
//bot.sendMessage(channel, text);
response = ""+lines[0];
response_to = channel;