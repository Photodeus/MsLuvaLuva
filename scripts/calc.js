// @description Calculates given expression. You can only use <code>+ - * / ( ) 0-9</code> and hexadecimal <code>0xA-F</code>
// @example  !calc 4.5*(12-5/8)
//var filtered = ""+param.match( /[0-9.\/\+\-\*\(\) \^\&\|\<\>xa-fA-F]+/ );
param = param.replace( /,/g, '' );
var filtered = ""+param.match( /[0-9.a-fA-F\/\+\-\*\(\) \^e%x]+/ );
if (filtered.indexOf('^') > 0) {
	filtered = filtered.replace( /(.+?)\^(.+?)/g, 'Math.pow($1, $2)' );
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
