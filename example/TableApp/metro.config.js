/**
 * Metro configuration for React Native
 * https://github.com/facebook/react-native
 *
 * @format
 */

const path = require('path');
const escape = require('escape-string-regexp');
const exclusionList = require('metro-config/src/defaults/exclusionList');
const pack = require('../../package.json');
const appPack = require('./package.json');
const modules = Object.keys(pack.peerDependencies);
const appModules = Object.keys(appPack.dependencies);

const reactNativeCarbonDir = path.resolve(__dirname, '../../');

module.exports = {
  watchFolders: [reactNativeCarbonDir],
  resolver: {
    sourceExts: ['jsx', 'js', 'ts', 'tsx'], //add here
    blacklistRE: exclusionList(
      modules.map(
        m =>
          new RegExp(
            `^${escape(
              path.join(reactNativeCarbonDir, 'node_modules', m),
            )}\\/.*$`,
          ),
      ),
    ),
    extraNodeModules: appModules.reduce((acc, name) => {
      acc[name] = path.join(__dirname, 'node_modules', name);
      return acc;
    }, {}),
  },
  transformer: {
    getTransformOptions: async () => ({
      transform: {
        experimentalImportSupport: false,
        inlineRequires: true,
      },
    }),
  },
};
