const mongoose = require('mongoose');

// 書籍のスキーマ定義
const bookSchema = new mongoose.Schema({
  title: { type: String, required: true, unique: true },
  author: { type: [String], default: ['unknown'] },
  publisher: { type: String, default: 'unknown' },
  id: { type: String, default: 'unknown' },
  imgLink: { type: String, default: 'unknown' }
});

module.exports = mongoose.model('Book', bookSchema);
