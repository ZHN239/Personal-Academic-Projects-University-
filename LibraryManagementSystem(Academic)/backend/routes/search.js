const express = require('express');
const router = express.Router();
const { searchBooks } = require('../utils/googleBooks');

// Google Books API検索用のルート
router.get('/', async (req, res) => {
  try {
    const books = await searchBooks(req.query.value);
    console.log('Google Books APIデータ返却完了');
    res.json(books);
  } catch (e) {
    console.error('Google Books APIエラー:', e.message);
    res.status(500).json({ message: 'request error', error: e.message });
  }
});

module.exports = router;
