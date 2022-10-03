var cors = require("cors");
var bodyParser = require('body-parser')
var express = require("express");
var app = express();
var expressWs = require("express-ws")(app);

var { WELCOMING_MESSAGE, ROUTE_TYPES } = require("./constants/constants");
const { MESSAGE_HANDLERS } = require("./utils/message-utils");
var { parseData } = require("./utils/parse-utils");
var { logNewMessage, logStateReset, logUserConnected, logState } = require('./utils/logger-utils');
const { handlePowerShortage } = require("./utils/event-utils");

let STATE = {
  network: {
    finishedJobsNo: 0,
    failedJobsNo: 0,
    totalPrice: 0,
    currPlannedJobsNo: 0,
    currActiveJobsNo: 0,
    currClientsNo: 0
  },
  agents: {
    agents: [],
    clients: [],
    connections: []
  }
}

app.use(cors())
app.use(bodyParser.urlencoded({ extended: false }))
app.use(bodyParser.json())

app.ws("/", function (ws, req) {
  ws.route = '/'
  ws.on("open", () => {
    logUserConnected()
    ws.send(JSON.stringify(WELCOMING_MESSAGE))
  })

  ws.on("message", function (msg) {
    const message = parseData(msg)
    const type = message.type
    const messageHandler = MESSAGE_HANDLERS[type]

    if (messageHandler) {
      logNewMessage(STATE, message)
      messageHandler(STATE, message)
    }
  });
});

app.get(ROUTE_TYPES.FRONT, (req, res) => {
  res.send(JSON.stringify(STATE))
})

app.get(ROUTE_TYPES.FRONT + '/reset', (req, res) => {
  STATE = {
    network: {
      finishedJobsNo: 0,
      failedJobsNo: 0,
      totalPrice: 0,
      currPlannedJobsNo: 0,
      currActiveJobsNo: 0,
      currClientsNo: 0
    },
    agents: {
      agents: [],
      clients: [],
      connections: []
    }
  }
  logStateReset()
  logState(STATE)
})

app.post(ROUTE_TYPES.FRONT + '/powerShortage', (req, res) => {
  const msg = req.body
  const dataToPass = handlePowerShortage(STATE, msg)
  expressWs.getWss().clients.forEach(client => {
    client.send(JSON.stringify(dataToPass))
  })
})

app.listen(8080);  