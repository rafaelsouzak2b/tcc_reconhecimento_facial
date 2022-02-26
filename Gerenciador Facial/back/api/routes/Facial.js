module.exports = app => {
  const controller = require('../controllers/Facial')();

  app.route('/')
    .get((req, res) => res.status(200).json("OK"));

  app.route('/api/v1/faces')
    .get(controller.getFaces);

  app.route('/api/v1/face')
  .delete(controller.deleteFace);
}