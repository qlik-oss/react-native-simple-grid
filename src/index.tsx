import { requireNativeComponent, ViewStyle } from 'react-native';

type ReactNativeStraightTableProps = {
  color: string;
  style: ViewStyle;
};

export const ReactNativeStraightTableViewManager = requireNativeComponent<
  ReactNativeStraightTableProps
>('ReactNativeStraightTableView');

export default ReactNativeStraightTableViewManager;
