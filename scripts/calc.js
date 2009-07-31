// @description Calculates given expression
// @example  !calc 4.5*(12-5/8)
//var filtered = ""+param.match( /[0-9.\/\+\-\*\(\) \^\&\|\<\>xa-fA-F]+/ );
var filtered = ""+param.match( /[0-9.\/\+\-\*\(\) \^!e%]+/ );
if (filtered.indexOf('^') > 0) {
	filtered = filtered.replace( /(.+)\^(.+)/, 'Math.pow($1, $2)' );
}
//filtered = param;
var r = null;
try {
	r = eval(""+filtered)
	//r = eval(""+param)
} catch (ex) {
	r = ex;
}
var BOLD = String.fromCharCode(2);
response = filtered + " = " + BOLD + r + BOLD; 
response_to = channel;
