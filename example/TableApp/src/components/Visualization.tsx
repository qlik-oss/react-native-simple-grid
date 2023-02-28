import React, {useEffect} from 'react';
import {ScrollView, StyleSheet, View} from 'react-native';
import {Supernova} from '@qlik/react-native-carbon';
import theme from './theme.json';
import {useAtomValue} from 'jotai';
import {currentModelAtom, loadableOpenAppAtom} from '../atoms';
import {SafeAreaView} from 'react-native-safe-area-context';
import supernova from '@nebula.js/sn-table';
import {expandCellAtom} from '@qlik/react-native-simple-grid/src/atoms';
import {useNavigation} from '@react-navigation/native';

const properties = {
  imageOrigins: [
    'maps.qlikcloud.com',
    'ibasemaps-api.arcgis.com',
    'cdn.pendo.io',
    'app.pendo.io',
    'pendo-static-5763789454311424.storage.googleapis.com',
    'data.pendo.io',
    'gravatar.com',
    'wp.com',
    'googleusercontent.com',
    'cdn.qlik-stage.com',
    'cdn.qlikcloud.com',
    'picsum.photos',
    'placebear.com',
    'i.ytimg.com',
    'placekitten.com',
  ],
};

const Visualization = () => {
  const model = useAtomValue(currentModelAtom);
  const openedApp = useAtomValue<any>(loadableOpenAppAtom);
  const expandCell = useAtomValue(expandCellAtom);
  const navigation = useNavigation();

  useEffect(() => {
    if (expandCell.expand) {
      navigation.push('ExpandedTableCell');
    }
  }, [expandCell, navigation]);

  return (
    <SafeAreaView style={styles.body} edges={['bottom', 'left', 'right']}>
      <ScrollView style={{flex: 1}}>
        <View style={styles.snContainer}>
          <Supernova
            sn={supernova}
            theme={theme}
            object={model}
            app={openedApp.data.app}
            appLayout={openedApp.data.appLayout}
            jsxComponent={true}
            properties={properties}
          />
        </View>
      </ScrollView>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  body: {
    flex: 1,
    borderRadius: 8,
    backgroundColor: 'white',
    overflow: 'hidden',
    padding: 8,
    margin: 8,
  },
  snContainer: {
    width: '100%',
    height: 1000,
  },
});

export default Visualization;
