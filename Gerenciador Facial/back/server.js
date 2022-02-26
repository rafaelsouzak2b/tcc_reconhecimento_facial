const app = require('./config/express')();
const port = app.get('port');
const host = app.get('host');

// RODANDO NOSSA APLICAÇÃO NA PORTA SETADA
app.listen(port, host,() => {
  console.log(`Servidor ${host} rodando na porta ${port}`)
});