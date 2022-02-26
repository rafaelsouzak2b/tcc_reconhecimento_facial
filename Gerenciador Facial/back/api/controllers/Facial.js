module.exports = () => {
  const facialDb = require('../data/Facial');
  const controller = {};
  controller.getFaces = (req, res) => facialDb.getFaces().then(result => res.status(200).json({result:result}))
                                                         .catch(error => res.status(500).json({ erro: "Erro ao buscar as faces" }));

  controller.deleteFace = (req, res) => facialDb.deleteFace(req.body.id).then(result => res.status(200).json({retorno:"Face excluida com sucesso"}))
                                                         .catch(error => res.status(500).json({ erro: "Erro ao excluir a faces" }));

  return controller;
}