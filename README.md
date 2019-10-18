# React Native Album List

A library for getting all the titles of photo albums and photos.
This library was taken https://github.com/shimohq/react-native-albums and modified for further work.

# Installation

Install the package from npm:

`yarn add --save react-native-album-list` or `npm i --save react-native-album-list`

and

`react-native link` < 0.60
`cd ios && pod install` >= 0.60

# Example

`import AlbumsList from 'react-native-album-list'`

Get a list of albums

```js
AlbumsList.getAlbumList({
  count: true,
  thumbnail: false,
  thumbnailDimensions: false
}).then(list => console.log(list));
```

### getAlbumList options

| Attribute             | Values             |
| --------------------- | ------------------ |
| `count`               | `'true'`/`'false'` |
| `thumbnail`           | `'true'`/`'false'` |
| `thumbnailDimensions` | `'true'`/`'false'` |

Get a list of photos

```js
AlbumsList.getImageList().then(list => console.log(list));
```

