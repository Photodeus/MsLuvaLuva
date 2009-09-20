// @description !remind nn [min|sec] Do this and that
//java.util.Timer
API.clearCacheForScript('remind');
function Reminder(time, nick, channel, reminder, key) {
	this.start = new Date().getTime();
	this.time = time;
	this.nick = nick;
	this.channel = channel;
	this.reminder = reminder;
	this.key = key;
}
//API.say(channel, 'Reminder script v0.2');
var key = 'reminder.' + nick.toLowerCase() + '_' + ident;
var previous = API.getValue(key);
API.info('Previous reminder: ' + (previous?previous.reminder+' at ' + previous.time:'None'));
if (param.length == 0) {
	if (previous != null && previous != 'null') {
		var timeleft = Math.floor((new Date().getTime() - previous.start)/1000);
		API.say(channel, "Already have reminder for \"" + previous.reminder + "\" in " + timeleft + " sec");
	} else API.say(channel, "No active reminders");
} else {
	if (param == '-list') {

	}
	if (param.indexOf('-clear') > 0) {
		if (previous != 'null') {
			previous.thread.interrupt();
			previous = null;
		}
		API.setValue(key, 'null');
		param = param.replace(/\-clear\s?/, '');
	}

	if (previous && previous != 'null') {
		var timeleft = Math.floor((new Date().getTime() - previous.start)/1000);
		API.say(channel, "Already have reminder for \"" + previous.reminder + "\" in " + timeleft + " sec");
	} else {

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

		API.info('reminder delta time: ' + deltatime);
		if (deltatime > 48 * 60 * 60 || deltatime < 10) {
			API.say(channel, "Sorry, I don't do reminders over 48h or less than 10s.");
		} else {
			var text = param.replace(/^\d+\s?(mi?n?|se?c?)(ond)?s?\s?/, '');
			var reminder = new Reminder(deltatime, nick, channel, text, key);
			var runnable = new java.lang.Runnable({
				rem: reminder,
				run: function() {
				try {
					java.lang.Thread.sleep(deltatime * 1000);
					API.say(rem.channel, 'Reminder for ' + rem.nick + ': ' + rem.reminder);
					API.setValue(rem.key, 'null');
				} catch(ex) {
				}
			} });
			//var tt = new java.util.TimerTask();
			//{ run: runnable });
			//API.info('tt: ' + tt);
			//var timer = new java.util.Timer();
			//API.info('timer: ' + timer);
			//timer.schedule(tt, deltatime * 1000);
			var thread = java.lang.Thread(runnable);
			reminder.thread = thread;
			API.say(channel, 'Reminder for \"' + reminder.reminder + '\" in ' + deltatime + ' seconds was set.');
			API.setValue(key, reminder);
			thread.start();
		}
	}
}