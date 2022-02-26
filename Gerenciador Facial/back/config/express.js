const express    = require('express');
const bodyParser = require('body-parser');
const config     = require('config');
var timeout = require('connect-timeout')

module.exports = () => {
  const app = express();

  // SETANDO VARIÁVEIS DA APLICAÇÃO
  app.set('port', process.env.PORT || config.get('server.port'));
  app.set('host', process.env.PORT || config.get('server.host'));

  // MIDDLEWARES
  app.use(bodyParser.json());
  app.use(timeout(100000));
  app.use(function (req, res, next) {
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Access-Control-Allow-Methods', '*');
    res.setHeader('Access-Control-Allow-Headers', '*');
    next();
});

  require('../api/routes/Facial')(app);

  return app;
};