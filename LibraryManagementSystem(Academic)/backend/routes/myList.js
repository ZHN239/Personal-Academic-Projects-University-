const express = require('express');
const router = express.Router();
const Book = require('../models/book');

// 書籍をMyListに追加
router.get('/add', async (req, res) => {
  try {
    const data = { ...req.query };
    if (typeof data.author === 'string') {
      data.author = data.author.split(',').map(a => a.trim());
    }

    await Book.insertOne(data)
    
    res.send('added successfully');
  } catch (e) {
    if (e.code === 11000) return res.send('already exists');
    console.error(e.message);
    res.status(500).send('add failed');
  }
});

// MyListから書籍を削除
router.get('/delete', async (req, res) => {
  try {
    await Book.deleteOne({ title: req.query.title });
    res.send('deleted successfully');
  } catch (e) {
    console.error(e.message);
    res.status(500).send('delete failed');
  }
});

// MyListの書籍を表示
router.get('/display', async (req, res) => {
    console.log('display')
    try {
        const data = await Book.find();
        res.json(data);
    } catch (e) {
        console.error(e.message);
        res.status(500).send('display failed');
    }
});

module.exports = router;
