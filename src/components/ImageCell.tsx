/* eslint-disable react-native/no-inline-styles */
import React, { useMemo } from 'react';
import { View, Image, StyleSheet } from 'react-native';
import type { StyleProp, ViewStyle } from 'react-native';
import { SvgXml } from 'react-native-svg';

export type ImageCellProps = {
  rowData: any;
  colData: any;
  style?: StyleProp<ViewStyle>;
};

const ImageCell: React.FC<ImageCellProps> = ({ rowData, colData, style }) => {
  console.log('row data', rowData);
  const imageUrl = useMemo(() => {
    if (colData?.representation?.imageSetting === 'label') {
      return rowData.qText;
    }
    const imageIndex = colData?.stylingInfo?.indexOf('imageUrl');
    if (imageIndex !== -1 && rowData.qAttrExps) {
      return rowData.qAttrExps.qValues[imageIndex].qText;
    }
    return rowData.qText;
  }, [colData, rowData]);

  console.log(rowData);

  const xmlSVG = useMemo(() => {
    if (rowData?.qText?.startsWith('data:image/svg+xml,')) {
      return rowData.qText.substring('data:image/svg+xml,'.length);
    }
    return null;
  }, [rowData]);

  console.log(xmlSVG);
  return (
    <View style={style}>
      {xmlSVG !== null ? (
        <View style={styles.svg}>
          <SvgXml xml={xmlSVG} width="100%" height="100%" />
        </View>
      ) : (
        <Image
          style={{ flex: 1 }}
          resizeMode="contain"
          source={{ uri: imageUrl }}
        />
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  svg: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
});

export default ImageCell;
