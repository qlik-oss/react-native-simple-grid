import React from 'react';
import type { StyleProp, ViewStyle } from 'react-native';
import MiniChartViewManager from './MiniChartViewManager';

export type MiniChartViewManagerProps = {
  colData: any;
  rowData: any;
  style?: StyleProp<ViewStyle>;
};

const MiniChart: React.FC<MiniChartViewManagerProps> = ({
  colData,
  rowData,
  style,
}) => {
  return (
    <MiniChartViewManager
      // eslint-disable-next-line react-native/no-inline-styles
      style={[{ flex: 1 }, style]}
      colData={colData}
      rowData={rowData}
    />
  );
};

export default MiniChart;
