const app = require('./app');

// サーバー起動
app.listen(3000, '0.0.0.0', () => {
  console.log('Server running at http://localhost:3000');
});
