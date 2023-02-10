import {useAtomValue} from 'jotai';
import React, {useCallback} from 'react';
import {FlatList, StyleSheet} from 'react-native';
import {ActivityIndicator, Divider} from 'react-native-paper';
import {SafeAreaView} from 'react-native-safe-area-context';
import {loadableAppListAtom} from '../atoms';
import AppCard from './AppCard';

const AppList = () => {
  const appList = useAtomValue<any>(loadableAppListAtom);

  const renderItem = useCallback(({item}: any) => {
    return <AppCard appId={item.resourceId} title={item.name} />;
  }, []);
  return (
    <SafeAreaView style={styles.body} edges={['left', 'right', 'bottom']}>
      {appList.state === 'loading' ? (
        <ActivityIndicator />
      ) : (
        <FlatList
          data={appList.data.data}
          renderItem={renderItem}
          ItemSeparatorComponent={Divider}
          windowSize={3}
        />
      )}
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  body: {
    flex: 1,
  },
  addFab: {
    position: 'absolute',
    right: 0,
    bottom: 0,
    margin: 16,
  },
});

export default AppList;
