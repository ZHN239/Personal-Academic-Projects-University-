const axios = require('axios');

// Google Books APIを呼び出して書籍データを取得するユーティリティ関数
async function searchBooks(query) {
    const responseTitle = await axios.get(`https://www.googleapis.com/books/v1/volumes?q=intitle:${encodeURIComponent(query)}&maxResults=20`);
    const responseAuthor = await axios.get(`https://www.googleapis.com/books/v1/volumes?q=inauthor:${encodeURIComponent(query)}&maxResults=20`);
    const responsePublisher = await axios.get(`https://www.googleapis.com/books/v1/volumes?q=inpublisher:${encodeURIComponent(query)}&maxResults=20`);
    const responseISBN = await axios.get(`https://www.googleapis.com/books/v1/volumes?q=isbn:${encodeURIComponent(query)}&maxResults=20`);

    let rawList = [
    ...(responseTitle.data.items || []),
    ...(responseAuthor.data.items || []),
    ...(responsePublisher.data.items || []),
    ...(responseISBN.data.items || [])
    ];

    rawList = rawList.filter((item, index, self) =>
    index === self.findIndex(obj => JSON.stringify(obj) === JSON.stringify(item))
    );

    return rawList.map(item => {
        let tempId;
        const object = {
        title: item.volumeInfo.title,
        author: item.volumeInfo.authors || ['unknown'],
        publisher: item.volumeInfo.publisher || 'unknown',
        imgLink: item.volumeInfo.imageLinks?.thumbnail || 'unknown',
        id: 'unknown'
    };

    // ISBNやその他IDを処理
    item.volumeInfo.industryIdentifiers?.forEach(subItem => {
      if (subItem.type === 'ISBN_10') object.id = 'ISBN：' + subItem.identifier;
      if (subItem.type === 'OTHER') tempId = subItem.identifier;
    });

    if (object.id === 'unknown' && tempId) object.id = tempId;

    return object;
    });
}

module.exports = { searchBooks };
