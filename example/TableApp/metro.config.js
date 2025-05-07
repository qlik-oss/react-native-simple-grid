/**
 * Metro configuration for React Native
 * https://github.com/facebook/react-native
 *
 * @format
 */
const path = require('path');
const watchFolders = [path.resolve(__dirname, '../../node_modules')];

module.exports = {
  projectRoot: path.resolve(__dirname),
  resolver: {
    sourceExts: ['jsx', 'js', 'ts', 'tsx', 'json'], //add here
  },
  transformer: {
    getTransformOptions: async () => ({
      transform: {
        experimentalImportSupport: false,
        inlineRequires: true,
      },
    }),
  },
  maxWorkers: 2,
  watchFolders,
};
