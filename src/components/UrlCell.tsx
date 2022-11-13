/* eslint-disable react-native/no-inline-styles */
import React, { useMemo } from 'react';
import { StyleSheet, View, Linking } from 'react-native';
import { Text } from 'react-native-paper';

export type UrlCellProps = {
  rowData: any;
  colData: any;
};

const UrlCell: React.FC<UrlCellProps> = ({ rowData, colData }) => {
  const url = useMemo(() => {
    const index = colData?.stylingInfo?.indexOf('url');
    let urlLabelIndex = colData?.stylingInfo?.indexOf('urlLabel');
    let urlLink = '';
    let label = rowData.qText;
    if (index !== -1) {
      urlLink = rowData.qAttrExps.qValues[index].qText;
    }
    if (colData.isDim) {
      label = colData.label;
      if (colData?.representation?.urlPosition === 'dimension') {
        label = rowData.qText;
      }
    } else {
      label = rowData.qText;
    }
    if (urlLabelIndex !== -1) {
      label = rowData.qAttrExps.qValues[urlLabelIndex].qText;
    }

    return { urlLink, label };
  }, [rowData, colData]);

  const openLink = () => {
    Linking.openURL(url.urlLink);
  };

  return (
    <View style={styles.textRow}>
      <Text style={{ color: 'blue' }} onPress={openLink}>
        {url.label}
      </Text>
    </View>
  );
};

const styles = StyleSheet.create({
  textCol2: {
    fontWeight: 'bold',
  },
  miniChart: {
    minHeight: 96,
    borderRadius: 4,
    borderWidth: 1,
    borderColor: '#CCC',
    padding: 4,
  },
  textRow: {
    flexDirection: 'row',
    alignItems: 'flex-start',
  },
  icon: {
    marginLeft: 8,
  },
});

export default UrlCell;
