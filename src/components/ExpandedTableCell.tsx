import { expandCellAtom } from '@qlik/react-native-simple-grid/src/atoms';
import { useAtom } from 'jotai';
import React, { useEffect } from 'react';
import { View, StyleSheet } from 'react-native';
import { ScrollView } from 'react-native-gesture-handler';
import { DataTable, Text } from 'react-native-paper';
import Cell from './Cell';

const DATA_TABLE_HEADER_HEIGHT = 48;

const ExpandedTableCell = () => {
  const [expandedCell, setExpandedCell] = useAtom(expandCellAtom);

  useEffect(() => {
    return () => {
      setExpandedCell({ expand: false });
    };
  }, [setExpandedCell]);

  return (
    <DataTable>
      <DataTable.Header style={styles.header}>
        <DataTable.Title style={styles.title}>Column Data</DataTable.Title>
        <DataTable.Title style={styles.title}>Row Data</DataTable.Title>
      </DataTable.Header>
        <ScrollView
          contentContainerStyle={{
            paddingBottom: DATA_TABLE_HEADER_HEIGHT * 2,
          }}
        >
          {expandedCell.data?.col.map((item: any, index: number) => (
            <DataTable.Row
              key={index}
              // eslint-disable-next-line react-native/no-inline-styles
              style={{ backgroundColor: index % 2 ? '#F0F0F0' : 'white' }}
            >
              <View style={styles.cell}>
                <Text>{item.label}</Text>
              </View>
              <Cell
                style={styles.cell}
                colData={item}
                rowData={expandedCell.data.row.cells[index]}
              />
            </DataTable.Row>
          ))}
        </ScrollView>
    </DataTable>
  );
};

const styles = StyleSheet.create({
  header: {
    height: DATA_TABLE_HEADER_HEIGHT,
  },
  title: {
    paddingHorizontal: 8,
  },
  cell: {
    flex: 1,
    padding: 8,
  },
});

export default ExpandedTableCell;
