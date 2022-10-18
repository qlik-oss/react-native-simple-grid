import React, { useCallback } from 'react';
import ReactNativeStraightTableViewManager from './ReactNativeStraightTableViewManager';
import { useUpdateAtom } from 'jotai/utils';
import { setExpandedCellAtom, setSearchingTableColumnAtom } from '../atoms';

export type SimpleGridLayout = {
  totals: {
    show: boolean;
  };
};

export type ScrollingProps = {
  horizontal?: boolean;
  keepFirstColumnInView?: boolean;
  keepFirstColumnInViewTouch?: boolean;
};

export type SimpleGridProps = {
  theme: any;
  tableData: any;
  style: any;
  layout: {
    scrolling: ScrollingProps;
    isDataView: boolean;
    totals: any;
    qHyperCube: {
      qGrandTotalRow: number;
      qSize: number;
    };
  };
  name: string;
  clearSelections: boolean;
  rect: { width: number };
  contentStyle: { cellStyle: any; headerStyle: any };
  onEndReached: () => void;
  onSelectionsChanged: () => void;
  onConfirmSelections: () => void;
  onHeaderPressed: () => void;
  translations: {
    menu: {
      copy: string;
      expand: string;
    };
  };
};

const SimpleGrid: React.FC<SimpleGridProps> = ({
  translations,
  name,
  contentStyle,
  theme,
  tableData,
  style,
  layout,
  rect,
  onEndReached,
  onSelectionsChanged,
  onConfirmSelections,
  clearSelections,
  onHeaderPressed,
}) => {
  const expandCell = useUpdateAtom(setExpandedCellAtom);
  const searchColumn = useUpdateAtom(setSearchingTableColumnAtom);

  const onExpandCell = useCallback(
    (event: any) => {
      try {
        const row = JSON.parse(event.nativeEvent.row);
        const col = JSON.parse(event.nativeEvent.col);
        expandCell({ expand: true, data: { row, col } });
      } catch (error) {}
    },
    [expandCell]
  );

  const onSearchColumn = useCallback(
    (event: any) => {
      try {
        const column = JSON.parse(event.nativeEvent.column);
        searchColumn({ searching: true, column });
      } catch (error) {}
    },
    [searchColumn]
  );

  return (
    <ReactNativeStraightTableViewManager
      theme={theme}
      cols={{
        header: tableData?.columns,
        footer: layout?.totals.show
          ? layout.qHyperCube.qGrandTotalRow
          : undefined,
        totals:
          layout?.totals?.show || layout?.totals?.position !== 'noTotals'
            ? { ...layout.totals, rows: layout.qHyperCube.qGrandTotalRow }
            : undefined,
      }}
      rows={{ rows: tableData?.rows, reset: tableData?.reset }}
      style={style.table}
      size={layout.qHyperCube.qSize}
      onEndReached={onEndReached}
      containerWidth={rect.width}
      onSelectionsChanged={onSelectionsChanged}
      onConfirmSelections={onConfirmSelections}
      clearSelections={clearSelections}
      onHeaderPressed={onHeaderPressed}
      freezeFirstColumn={layout?.scrolling?.keepFirstColumnInView}
      cellContentStyle={contentStyle.cellStyle}
      headerContentStyle={contentStyle.headerStyle}
      name={name}
      isDataView={layout?.isDataView}
      translations={translations}
      onExpandCell={onExpandCell}
      onSearchColumn={onSearchColumn}
    />
  );
};

export default SimpleGrid;
