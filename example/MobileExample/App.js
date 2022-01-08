/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React from 'react';
import {SafeAreaView, StyleSheet, View} from 'react-native';
import ReactNativeStraightTableViewManager from '@qlik-trial/react-native-straight-table';
import SampleData from './sampledata.json';

const App = () => {
  return (
    <SafeAreaView style={styles.body}>
      <View style={{flex: 1, borderRadius: 16}}>
        <ReactNativeStraightTableViewManager
          style={styles.table}
          cols={SampleData.columns}
        />
      </View>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  body: {
    flex: 1,
    margin: 32,
  },
  table: {
    flex: 1,
    borderRadius: 16,
  },
});

export default App;
