module.exports = {
  root: true,
  extends: ['@react-native-community', 'plugin:prettier/recommended'],
  plugins: ['jest', 'prettier'],
  env: {
    'jest/globals': true,
  },
  rules: {
    'prettier/prettier': 'error',
  },
};
