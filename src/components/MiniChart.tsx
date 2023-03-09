import React from 'react';
import { View } from 'react-native';
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
    // eslint-disable-next-line react-native/no-inline-styles
    <View style={[{ flex: 1 }, style]}>
      <MiniChartViewManager
        // eslint-disable-next-line react-native/no-inline-styles
        style={[{ flex: 1 }]}
        colData={colData}
        rowData={rowData}
      />
    </View>
  );
};

export default MiniChart;
