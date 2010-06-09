/**
 * Common routines available to all plugin scripts
 */
function pickOne(elements) {
	return elements[Math.floor(Math.random() * elements.length)];
}

function relativeTime(time, sayAgo) {
	var delta = parseInt(new Date().getTime() / 1000 - time);
	var d, h, m, s;
	var ago = sayAgo ? " ago" : "";
	if (delta <= 60) {
		datemsg = Math.floor(delta) + " seconds" + ago;
	} else if (delta < 60 * 60) {
		m = Math.floor(delta / 60);
		s = Math.floor(delta) % 60;
		datemsg = m + " min " + s + " sec" + ago;
	} else if (delta < 24 * 60 * 60) {
		h = parseInt(delta / 3600);
		m = parseInt(delta / 60) % 60;
		s = delta % 60;
		//datemsg = h + " h and " + m + " min and " + s + "s ago";
		datemsg = h + " h and " + m + " min" + ago;
	} else {
		d = Math.floor(delta / 86400);
		h = Math.floor(delta / 3600) - d * 24;
		m = Math.floor(delta / 60) - d * 1440 - h * 60;
		s = Math.floor(delta) % 60;
		datemsg = d + " days and " + h + " hours" + ago;
	}
	return datemsg;
}
