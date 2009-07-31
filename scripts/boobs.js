// @description Totally useless... Why on Earth would anyone spam chat with this?
var sizes = [ 'AA', 'A', 'B', 'B', 'C', 'C', 'C', 'D', 'DD', 'E', 'F', 'G', 'H' ];
var cupsize = Math.round(5*Math.round(Math.random()*12)+60) + sizes[Math.floor(Math.random()*sizes.length)];

if (Math.random() < 0.1) {
	response = param + " has no rack at all!";
} else {
	response = param + " is equipped with a size " + cupsize + " milk dispension unit." 
}
response_to = channel;
