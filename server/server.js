var WebSocketServer = require('websocket').server;
var http = require('http');
var express = require('express');

var app = express();
var router = express.Router();

router.post('/', (req, res, next) => {
	theConnection.sendUTF(req.query.status);
	res.send('success');
})

app.use('/', router);
app.listen(3000, () => {
	console.log((new Date()) + ' Request server is listening on port 3000');
});

var server = http.createServer((request, response) => {
    console.log((new Date()) + ' Received request for ' + request.url);
    response.writeHead(404);
    response.end();
});

server.listen(3001, () => {
    console.log((new Date()) + ' Socket server is listening on port 3001');
});

var websocket = new WebSocketServer({
	httpServer: server
})

let theConnection;

websocket.on('request', (request) => {
	theConnection = request.accept('echo-protocol', request.origin);
    console.log((new Date()) + ' Connection accepted.');
    // theConnection.on('message', (message) => {
    //     if (message.type === 'utf8') {
    //         console.log('Received Message: ' + message.utf8Data);
    //         theConnection.sendUTF(message.utf8Data);
    //     }
    //     else if (message.type === 'binary') {
    //         console.log('Received Binary Message of ' + message.binaryData.length + ' bytes');
    //         theConnection.sendBytes(message.binaryData);
    //     }
    // });
    theConnection.on('close', (reasonCode, description) => {
        console.log((new Date()) + ' Peer ' + theConnection.remoteAddress + ' disconnected.');
		theConnection = null;
    });
})