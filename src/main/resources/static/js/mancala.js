const HOST = "http://localhost:9090/";

var playerId;
var gameId;
var stompClient;

var playerName = promptForPlayerName();
sendCreatePlayerRequest(playerName);
setupMovement();

function promptForPlayerName() {
    return prompt('Please enter your name', '');
}

function setupMovement() {
    var pitDivs = document.getElementById('row-player-one').children;
    console.log(pitDivs.length);
    var i;
    for (var i = 0; i < pitDivs.length; i++) {
        (function(index) {
            pitDivs[index].onclick = function() {
                sendMovement(index)
            };
        })(i)
    }
}

function sendCreatePlayerRequest(playerName) {
    var requestPostPlayer = new XMLHttpRequest();
    requestPostPlayer.open('POST', HOST + '/players', true);
    requestPostPlayer.setRequestHeader('Content-Type', 'application/json');
    requestPostPlayer.onload = function() {
        if (this.status == 200) {
            var player = JSON.parse(requestPostPlayer.responseText)
            playerId = player.id;
            requestBoardForPlayer(player);
        }
    };
    requestPostPlayer.send(JSON.stringify({
        "name": playerName
    }));
}


function requestBoardForPlayer(player) {
    var requestGetGame = new XMLHttpRequest();
    requestGetGame.open('GET', HOST + '/game/' + player.id, true);
    requestGetGame.setRequestHeader('Content-Type', 'application/json');
    requestGetGame.onload = function() {
        if (this.status == 200) {
            var game = JSON.parse(requestGetGame.responseText);
            gameId = game.id;
            updateBoard(game);
            setupWebsockets(player.id);
        } else if (this.status == 202) {
            document.getElementById('status').innerHTML = 'Please wait for an opponent.';
            setupWebsockets(player.id);
        }
    };
    requestGetGame.send();
}

function sendMovement(pitId) {
    var requestGetGame = new XMLHttpRequest();
    requestGetGame.open('POST', HOST + '/game', true);
    requestGetGame.setRequestHeader('Content-Type', 'application/json');
    requestGetGame.send(JSON.stringify({
        "gameId": gameId,
        "pitId": pitId,
        "playerId": playerId
    }));
}

function setupWebsockets(playerId) {
    var socket = new SockJS('/websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/queue/board' + playerId, function(message) {
            var game = JSON.parse(message.body);
            gameId = game.id;
            updateBoard(game);
        });
    });
}

function updateBoard(game) {

    var myPlayer;
    var opponent;

    if (game.secondPlayer.id == playerId) {
        myPlayer = game.secondPlayer;
        opponent = game.firstPlayer;
    } else {
        myPlayer = game.firstPlayer;
        opponent = game.secondPlayer;
    }

    var myBigPit = myPlayer.pits.pop();
    var myPits = myPlayer.pits;
    var opponentBigPit = opponent.pits.pop();
    var opponentPits = opponent.pits;

    var myPitsElement = document.getElementById('row-player-one').children;
    var opponentPitsElement = document.getElementById('row-player-two').children;
    var myBigPitElement = document.getElementById('player-one-store');
    var opponentBigPitElement = document.getElementById('player-two-store');

    myBigPitElement.innerHTML = myBigPit;
    opponentBigPitElement.innerHTML = opponentBigPit;

    var i;
    for (i = 0; i < myPits.length; i++) {
        myPitsElement[i].innerHTML = myPits[i];
    }

    for (i = 0; i < opponentPits.length; i++) {
        opponentPitsElement[opponentPits.length - i - 1].innerHTML = opponentPits[i];
    }

    if (game.turnOfWithId == playerId) {
        document.getElementById('status').innerHTML = 'Your turn!'
    } else {
        document.getElementById('status').innerHTML = 'Opponents turn.'
    }

    if (game.over) {
        if (game.winner == playerId) {
            document.getElementById('status').innerHTML = 'You won!'
        } else {
            document.getElementById('status').innerHTML = 'You lost.'
        }
    }


}

