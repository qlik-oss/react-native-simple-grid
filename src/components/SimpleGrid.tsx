import React, { useCallback } from 'react';
import ReactNativeStraightTableViewManager from './ReactNativeStraightTableViewManager';
import { useUpdateAtom } from 'jotai/utils';
import {
  setDragBoxAtom,
  setExpandedCellAtom,
  setSearchingTableColumnAtom,
} from '../atoms';

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
      qDimensionInfo: Array<any>;
    };
  };
  name: string;
  model: any;
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
    misc: {
      of: string;
    };
  };
};

const transformTotals = (layout: any, table: any) => {
  let totals;
  let rowIndex = 0;
  let firstCol = true;
  let values = table?.columns?.map((col: any) => {
    if (col.isDim && firstCol) {
      firstCol = false;
      return layout.totals.label || 'Totals';
    }
    if (!col.isDim && rowIndex < layout.qHyperCube.qGrandTotalRow.length) {
      return layout.qHyperCube.qGrandTotalRow[rowIndex++].qText;
    }
    return '';
  });

  let show = layout?.totals?.show === 'auto' ? true : layout?.totals?.show;
  if (layout.totals.show && table.totalsPosition !== 'noTotals') {
    totals = {
      ...layout.totals,
      rows: layout.qHyperCube.qGrandTotalRow,
      show,
      values,
    };
  }
  return totals;
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
  model,
}) => {
  const draggingBox = useUpdateAtom(setDragBoxAtom);
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

  const signalSearch = useCallback(
    async (column: any) => {
      try {
        const props = await model.getEffectiveProperties();
        console.log(props, column);
        if (
          props?.qHyperCubeDef?.qDimensions[column.dataColIdx].qDef
            .qFieldDefs[0]
        ) {
          column.qCardinal = layout.qHyperCube.qDimensionInfo[column.dataColIdx].qCardinal;
          column.label =
            props?.qHyperCubeDef?.qDimensions[
              column.dataColIdx
            ].qDef.qFieldDefs[0];
            const fieldLabel = props?.qHyperCubeDef?.qDimensions[
              column.dataColIdx
            ].qDef.qFieldLabels[0]; 
          column.display =  fieldLabel.length === 0 ? column.label : fieldLabel;

        }
       
        searchColumn({ searching: true, column });
      } catch (error) {}
    },
    [model, searchColumn, layout]
  );

  const onSearchColumn = useCallback(
    (event: any) => {
      try {
        const column = JSON.parse(event.nativeEvent.column);
        signalSearch(column);
      } catch (error) {}
    },
    [signalSearch]
  );

  const onDragBox = useCallback(
    (event: any) => {
      try {
        draggingBox({ dragging: event.nativeEvent.dragging });
      } catch (error) {}
    },
    [draggingBox]
  );

  return (
    <ReactNativeStraightTableViewManager
      theme={theme}
      cols={{
        header: tableData?.columns,
        footer: layout?.totals.show
          ? layout.qHyperCube.qGrandTotalRow
          : undefined,
        totals: transformTotals(layout, tableData),
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
      onDragBox={onDragBox}
    />
  );
};

export default SimpleGrid;
