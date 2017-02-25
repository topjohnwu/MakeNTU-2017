var WebSocketServer = require('websocket').server;
var http = require('http');
var express = require('express');
var bodyParser = require('body-parser');

var app = express();
var router = express.Router();

app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

router.post('/:id', (req, res, next) => {
    for (var i = list.length - 1; i >= 0; i--) {
        if (list[i] === req.params.id) {
            connections[i].sendUTF(req.query.status);
            res.send('success');
            return;
        }
    }
    res.status(400).send('fail');
})

router.get('/list', (req, res, next) => {
    res.json(list);
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

var list = [];
var connections = [];

websocket.on('request', (request) => {
    let connection = request.accept('echo-protocol', request.origin);
    console.log((new Date()) + ' Connection ' + connection.remoteAddress + ' accepted.');
    connection.on('message', (message) => {
        list.push(message.utf8Data);
        connections.push(connection);
    });
    connection.on('close', (reasonCode, description) => {
        console.log((new Date()) + ' Peer ' + connection.remoteAddress + ' disconnected.');
        for (var i = connections.length - 1; i >= 0; i--) {
            if (connections[i] === connection) {
                connections.splice(i, 1);
                list.splice(i, 1);
            }
        }
    });
})