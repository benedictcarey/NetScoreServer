# NetScoreServer
An image server and client for displaying MaxScore generated scores in web browsers
NetCanvas is a Server/Client based system for displaying music notation
generated with Georg Hajdu’s MaxScore on anything running a modern web
browser. It consists of a server (built in Java and based on code by
Desmond Shaw of Dynamsoft) which pushes images generated in MaxScore
over websockets to any connected browser clients, and continues to
update as the scores are changed. The client file is HTML5 and JS and
can be hosted on a webserver. Synchronisation is achieved with
Serverdate.js
