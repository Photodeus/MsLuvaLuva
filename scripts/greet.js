// @description Greet someone. The bot automatically greets everyone who joins chat using this same script.
// @example !greet Jonas
//API.notice(nick, API.greetings.size());
response = ""+API.getGreeting().replaceAll( "\\$nick", param );
response_to = channel;