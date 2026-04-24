const express = require('express');
const mongoose = require('mongoose');
const searchRouter = require('./routes/search');
const myListRouter = require('./routes/myList');

const app = express();

// MongoDB接続
mongoose.connect('mongodb://127.0.0.1:27017/SAprogramming');
mongoose.connection.on('open', () => console.log('MongoDB接続成功'));
mongoose.connection.on('error', () => console.log('MongoDB接続エラー'));

// CORS設定ミドルウェア
app.use((req, res, next) => {
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type');
  res.setHeader('Access-Control-Allow-Methods', 'GET,POST,PUT,DELETE,OPTIONS');
  if (req.method === 'OPTIONS') return res.sendStatus(204);
  next();
});

// ルートのマウント
app.use('/search', searchRouter);
app.use('/myList', myListRouter);

// エラーハンドリングミドルウェア
app.use((err, req, res, next) => {
  console.error('未処理エラー:', err.message);
  res.status(500).send('something went wrong');
});

module.exports = app;
