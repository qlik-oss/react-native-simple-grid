import {useUpdateAtom} from 'jotai/utils';
import React, {useCallback, useState} from 'react';
import {StyleSheet} from 'react-native';
import {Button, TextInput} from 'react-native-paper';
import {SafeAreaView} from 'react-native-safe-area-context';
import {storedConnectionListAtom} from '../atoms';
import {ConnectionConfig} from '../atoms/connectionAtom';

const Connection = () => {
  const addConnection = useUpdateAtom(storedConnectionListAtom);
  const [tenant, setTenant] = useState('');
  const [apiKey, setApiKey] = useState('');

  const onChangeTenant = useCallback((val: string) => setTenant(val), []);
  const onChangeApiKey = useCallback((val: string) => setApiKey(val), []);

  const onSave = useCallback(() => {
    addConnection((prv: Array<ConnectionConfig>) => {
      return [...prv, {tenant, apiKey}];
    });
  }, [addConnection, apiKey, tenant]);

  return (
    <SafeAreaView style={styles.body} edges={['left', 'bottom', 'right']}>
      <TextInput
        label="Tenant"
        placeholder="Tenant without protocol (me.us.qlikcloud.com)"
        value={tenant}
        onChangeText={onChangeTenant}
        autoCapitalize="none"
      />
      <TextInput
        label="API Key"
        placeholder="Paste your key here"
        value={apiKey}
        onChangeText={onChangeApiKey}
        autoCapitalize="none"
      />
      <Button onPress={onSave} uppercase={false}>
        Add
      </Button>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  body: {
    flex: 1,
  },
  addFab: {
    position: 'absolute',
    bottom: 0,
    right: 0,
  },
});

export default Connection;
