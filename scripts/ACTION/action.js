var lnick = ""+nick.toLowerCase();

API.setValue("seen."+lnick + ".time", Math.floor(new Date().getTime()/1000));
API.setValue("seen."+lnick + ".msg", message);

if (message.indexOf(' snuggles ') > 0) {
	if (message.toLowerCase().indexOf('luva') >= 0) {

	} else {
		response = 'Ohh, I want that too!';
		response_to = channel;
	}
}