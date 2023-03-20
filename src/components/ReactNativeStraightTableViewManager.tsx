import { requireNativeComponent } from 'react-native';
import type { ViewStyle } from 'react-native';

type ReactNativeStraightTableProps = {
  style: ViewStyle;
  theme: any;
  cols: any;
  rows: any;
  size: any;
  onEndReached: any;
  containerWidth: number;
  onSelectionsChanged: any;
  onConfirmSelections: any;
  clearSelections: any;
  onHeaderPressed: any;
  freezeFirstColumn: any;
  cellContentStyle: any;
  headerContentStyle: any;
  name: string;
  isDataView: boolean;
  translations: any;
  onExpandCell: any;
  onSearchColumn: any;
  onDragBox: any;
};

export const ReactNativeStraightTableViewManager =
  requireNativeComponent<ReactNativeStraightTableProps>(
    'ReactNativeStraightTableView'
  );

export default ReactNativeStraightTableViewManager;
