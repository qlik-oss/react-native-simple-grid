import { requireNativeComponent, ViewStyle, StyleProp } from 'react-native';

type MiniChartProps = {
  colData: any;
  rowData: any;
  style: StyleProp<ViewStyle>;
};

export const MiniChartViewManager =
  requireNativeComponent<MiniChartProps>('MiniChartView');

export default MiniChartViewManager;
