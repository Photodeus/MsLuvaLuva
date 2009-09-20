// @description Greet someone
//API.notice(nick, API.greetings.size());
response = ""+API.getGreeting().replaceAll( "\\$nick", param );
response_to = channel;