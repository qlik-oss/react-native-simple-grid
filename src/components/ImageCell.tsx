/* eslint-disable react-native/no-inline-styles */
import React, { useMemo } from 'react';
import { StyleProp, View, ViewStyle, Image } from 'react-native';

export type ImageCellProps = {
  rowData: any;
  colData: any;
  style?: StyleProp<ViewStyle>;
};

const ImageCell: React.FC<ImageCellProps> = ({ rowData, colData, style }) => {
  const imageUrl = useMemo(() => {
    if(colData?.representation?.imageSetting === 'label') {
      return rowData.qText;
    }
    const imageIndex = colData?.stylingInfo?.indexOf('imageUrl');
    if (imageIndex !== -1 && rowData.qAttrExps) {
      return rowData.qAttrExps.qValues[imageIndex].qText;
    }
    return rowData.qText;
  }, [colData, rowData]);
  return (
    <View style={style}>
      <Image
        style={{ flex: 1 }}
        resizeMode="contain"
        source={{ uri: imageUrl }}
      />
    </View>
  );
};

export default ImageCell;
