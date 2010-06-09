// @description !remind nn [min|sec] Do this and that 
// @example !remind 10 min Take the cake out of the oven
//java.util.Timer
//API.clearScriptCacheFor('remind');
function Reminder(time, nick, channel, reminder, key) {
	this.start = new Date().getTime();
	this.time = time;
	this.nick = nick;
	this.channel = channel;
	this.reminder = reminder;
	this.key = key;
}
function parsetime(param) {
	var time = param.match(/^(\d+)\s?m(in)?(ute)?s?/);
	var deltatime = -1;
	if (time) {
		deltatime = time[1] * 60;
	} else {
		time = param.match(/^(\d+)\s?h?(ours?)/);
		if (time) {
			deltatime = time[1] * 60 * 60;
		} else {
			time = param.match(/^(\d+)\s?s?(ec)?/);
			deltatime = time[1];
		}
	}
	return deltatime;
}

var reminders;
if (!reminders) {
	reminders = {};
}
//API.say(channel, 'Reminder script v0.2');
var key = 'reminder.' + nick.toLowerCase() + '_' + ident;
var previous = reminders[key];

API.info('Previous reminder: ' + (previous?previous.reminder+' at ' + previous.time:'None'));
if (param.length == 0) {
	if (previous) {
		var timeleft = parseInt((new Date().getTime() - previous.start)/1000);
		API.say(channel, "Already have reminder for \"" + previous.reminder + "\" in " + timeleft + " sec");
	} else {
		API.say(channel, "You have no active reminders.");
	}
} else {
	if (param == '-list') {
		var s = ""; 
		for (var k in reminders) {
			s += reminders[k].nick + ", "; 
		}
		if (s.length > 0) {
			response = "Active reminders: " + s.substring(0, s.length-2);			
			response_to = channel;
		} else {
			response = "There are no active reminders.";			
			response_to = channel;
		}
	} else if (param.indexOf('-clear') >= 0) {
		if (previous) {
			previous.thread.interrupt();
			previous = null;
			reminders[key] = null;
		}
		API.say(channel, "Cleared reminders for " + nick);
	} else {
		if (previous) {
			var timeleft = parseInt((new Date().getTime() - previous.start)/1000);
			API.say(channel, "Already have reminder for \"" + previous.reminder + "\" in " + timeleft + " sec");
		} else {
	
			var deltatime = parsetime(param);
	
			API.info('reminder delta time: ' + deltatime);
			if (deltatime > 48 * 60 * 60 || deltatime < 2) {
				API.say(channel, "Sorry, I don't do reminders over 48h or less than 10s.");
			} else {
				var text = param.replace(/^\d+\s?(mi?n?|se?c?)(ond)?s?\s?/, '');
				var reminder = new Reminder(deltatime, nick, channel, text, key);
				/*
				var runnable = new java.lang.Runnable({
					rem: reminder,
					run: function() {
					try {
						java.lang.Thread.sleep(deltatime * 1000);
						API.say(rem.channel, 'Oi!! Reminder for ' + rem.nick + ': ' + rem.reminder);
						reminders[rem.key] = null;
					} catch(ex) {
					}
				} });
				*/	
				var tt = new java.util.TimerTask(new java.lang.Runnable({
					mynick: nick,
					myrem: text,
					mykey: key,
					run: function() {
						API.say(rem.channel, 'Oi!! Reminder for ' + mynick + ': ' + myrem);
						reminders[mykey] = null;
						}
					})
				);
				
				//API.info('tt: ' + tt);
				var timer = new java.util.Timer();
				timer.schedule(tt, deltatime * 1000);
				API.say(channel, 'Reminding you in ' + deltatime + ' sec about ' + reminder.reminder + '. You can use !remind -clear to cancel.');
				//var thread = java.lang.Thread(runnable);
				//reminder.thread = thread;
				//thread.start();
				reminders[key] = reminder;
			}
		}
	}
}