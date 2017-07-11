var ws = new WebSocket("ws://192.168.0.100:8892/");
ws.binaryType = "arraybuffer";

ws.onopen = function() {
    alert("Opened");
    ws.send("I'm client");
};

ws.onmessage = function (evt) { 
    var bytes = new Uint8Array(evt.data);
    var data = "";
    var len = bytes.byteLength;
    for (var i = 0; i < len; ++i) {
    	data += String.fromCharCode(bytes[i]);
    }
    var img = document.getElementById("image");
    img.src = "data:image/png;base64,"+window.btoa(data);
};

var button = document.getElementById("button");
button.onclick = function() {
	ws.send("0");
	ws.send("image");
};

var button = document.getElementById("button1");
button.onclick = function() {
	ws.send("0");
	ws.send("1");
};

var button = document.getElementById("button2");
button.onclick = function() {
	ws.send("0");
	ws.send("2");
};

var button = document.getElementById("button3");
button.onclick = function() {
	ws.send("0");
	ws.send("3");
};

var button = document.getElementById("button4");
button.onclick = function() {
	ws.send("0");
	ws.send("4");
};