// @description Show weather in a city
// @example !weather Stockholm
// @example !weather Houston, Texas
param = ""+bot.encode(param);
response = ""+bot.fetchUrl('http://popodeus.com/chat/bot/weather.jsp?w='+param);
response_to = channel;