import { NativeModules } from 'react-native';

type imageListOptions = {
    title: ?boolean,
    name: ?boolean,
    size: ?boolean,
    description: ?boolean,
    location: ?boolean,
    date: ?boolean,
    orientation: ?boolean,
    type: ?boolean,
    album: ?boolean,
    dimensions: ?boolean
};

type albumListOptions = {
    count: ?boolean,
    thumbnail: ?boolean,
    thumbnailDimensions: ?boolean
};

export default {
    getImageList(options: imageListOptions = {}) {
        return NativeModules.RNAlbumsModule.getImageList(options);
    },

    getAlbumList(options: albumListOptions = {}) {
        return NativeModules.RNAlbumsModule.getAlbumList(options);
    },
    copyAssetsVideoIOS(videoUri, destPath) {
        return NativeModules.RNAlbumsModule.copyAssetsVideoIOS(videoUri, destPath);
    },
    compressVideo(videoUri, destPath) {
        return NativeModules.RNAlbumsModule.compressVideo(videoUri, destPath);
    },
    TemporaryDirectoryPath: NativeModules.RNAlbumsModule.RNFSTemporaryDirectoryPath
};
