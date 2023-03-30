import React, { useCallback, useRef } from 'react';
import ReactNativeStraightTableViewManager from './ReactNativeStraightTableViewManager';
import { useUpdateAtom } from 'jotai/utils';
import {
  setDragBoxAtom,
  setExpandedCellAtom,
  setSearchingTableColumnAtom,
} from '../atoms';
import CarbonTheme from '@qlik/react-native-carbon/src/core/CarbonTheme';

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
  themeData: any;
  tableData: any;
  style: any;
  layout: {
    scrolling: ScrollingProps;
    isDataView: boolean;
    totals: any;
    qInfo: {
      name?: string;
    },
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
    headerValues: Array<string>;
  };
};

const transformTotals = (layout: any, table: any) => {
  let totals;
  let rowIndex = 0;
  let values = table?.columns?.map((col: any, index: number) => {
    if (col.isDim && index === 0) {
      return layout.totals.label || 'Totals';
    }
    if (!col.isDim && rowIndex < layout.qHyperCube.qGrandTotalRow.length) {
      return layout.qHyperCube.qGrandTotalRow[rowIndex++].qText;
    }
    return '';
  });

  let show = layout?.totals?.show === 'auto' ? true : layout?.totals?.show;
  if (layout.totals.show || table.totalsPosition !== 'noTotals') {
    totals = {
      ...layout.totals,
      rows: layout.qHyperCube.qGrandTotalRow,
      show,
      values,
    };
  }
  if (layout.qHyperCube?.qGrandTotalRow?.length === 0) {
    totals = null;
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
  themeData,
}) => {
  const draggingBox = useUpdateAtom(setDragBoxAtom);
  const expandCell = useUpdateAtom(setExpandedCellAtom);
  const searchColumn = useUpdateAtom(setSearchingTableColumnAtom);
  const carbonTheme = useRef<CarbonTheme>(
    new CarbonTheme({ theme: themeData })
  );

  const onExpandCell = useCallback(
    (event: any) => {
      try {
        const row = JSON.parse(event.nativeEvent.row);
        const col = JSON.parse(event.nativeEvent.col);
        expandCell({
          expand: true,
          data: { row, col },
          titles: translations.headerValues,
          viewData: layout?.isDataView,
        });
      } catch (error) {}
    },
    [expandCell, layout?.isDataView, translations.headerValues]
  );

  const signalSearch = useCallback(
    async (column: any) => {
      try {
        const props = await model.getEffectiveProperties();
        if (
          props?.qHyperCubeDef?.qDimensions[column.dataColIdx].qDef
            .qFieldDefs?.[0]
        ) {
          column.qCardinal =
            layout.qHyperCube.qDimensionInfo[column.dataColIdx].qCardinal;
          column.label =
            props?.qHyperCubeDef?.qDimensions[
              column.dataColIdx
            ].qDef.qFieldDefs?.[0];
          const fieldLabel =
            props?.qHyperCubeDef?.qDimensions[column.dataColIdx].qDef
              .qFieldLabels?.[0];
          column.display = fieldLabel.length === 0 ? column.label : fieldLabel;
          if (
            layout?.qHyperCube?.qDimensionInfo[column.dataColIdx]
              ?.qFallbackTitle?.length > 0
          ) {
            column.display =
              layout.qHyperCube.qDimensionInfo[
                column.dataColIdx
              ].qFallbackTitle;
          }
        } else {
          const qDimInfo = layout.qHyperCube.qDimensionInfo[column.dataColIdx];
          if (qDimInfo.qGroupFieldDefs?.[0].length > 0) {
            column.label = qDimInfo.qGroupFieldDefs?.[0];
          } else {
            column.label =
              layout.qHyperCube.qDimensionInfo[
                column.dataColIdx
              ].qFallbackTitle;
          }
          column.display =
            layout.qHyperCube.qDimensionInfo[column.dataColIdx].qFallbackTitle;
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
      theme={{
        ...theme,
        headerBackgroundColor: carbonTheme?.current?.getValue(
          'object.straightTable.header.backgroundColor',
          '#F0F0F0'
        ),
        backgroundColor: carbonTheme.current?.resolveBackgroundColor(
          layout,
          'white'
        ),
        even: carbonTheme?.current?.getValue(
          'object.straightTable.mobile.rows.even.backgroundColor',
          null
        ),
      }}
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
      name={layout?.qInfo?.name || name}
      isDataView={layout?.isDataView}
      translations={translations}
      onExpandCell={onExpandCell}
      onSearchColumn={onSearchColumn}
      onDragBox={onDragBox}
    />
  );
};

export default SimpleGrid;
