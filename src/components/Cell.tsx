import React, { useCallback } from 'react';
import { StyleSheet, View } from 'react-native';
import type { StyleProp, ViewStyle } from 'react-native';
import MiniChart from './MiniChart';
import TextCell from './TextCell';
import ImageCell from './ImageCell';
import UrlCell from './UrlCell';

export type CellProps = {
  rowData: any;
  colData: any;
  style?: StyleProp<ViewStyle>;
};

const Cell: React.FC<CellProps> = ({ rowData, colData, style }) => {
  const getCell = useCallback(() => {
    if (colData.representation.type === 'miniChart') {
      return (
        <MiniChart
          style={styles.miniChart}
          colData={colData}
          rowData={rowData}
        />
      );
    }
    if (colData.representation.type === 'image') {
      return (
        <ImageCell
          style={styles.imageCell}
          rowData={rowData}
          colData={colData}
        />
      );
    }
    if (colData.representation.type === 'url') {
      return <UrlCell rowData={rowData} colData={colData} />;
    }
    return <TextCell rowData={rowData} colData={colData} />;
  }, [rowData, colData]);

  return <View style={style}>{getCell()}</View>;
};

const styles = StyleSheet.create({
  textCol2: {
    fontWeight: 'bold',
  },
  miniChart: {
    minHeight: 96,
    borderRadius: 4,
    borderWidth: 1,
    borderColor: '#CCCCCC',
    overflow: 'hidden',
    padding: 4,
  },
  imageCell: {
    minHeight: 96,
    borderRadius: 4,
    padding: 4,
    borderWidth: 1,
    borderColor: '#CCCCCC',
    overflow: 'hidden',
  },
  icon: {
    marginLeft: 8,
  },
});

export default Cell;
