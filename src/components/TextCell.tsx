import React, { useMemo } from 'react';
import { StyleSheet, View } from 'react-native';
import { Text } from 'react-native-paper';
import MaterialIcons from 'react-native-vector-icons/MaterialIcons';

export type TextCellProps = {
  rowData: any;
  colData: any;
};

const iconMap = new Map([
  ['m', 'check'],
  ['ï', 'star'],
  ['R', 'arrow-drop-up'],
  ['S', 'arrow-drop-down'],
  ['T', 'arrow-left'],
  ['U', 'arrow-right'],
  ['P', 'add'],
  ['Q', 'remove'],
  ['è', 'warning'],
  ['¢', 'clean-hands'],
  ['©', 'lag'],
  ['23F4', 'lightbulb'],
  ['2013', 'stop'],
  ['&', 'pie-chart'],
  ['add', 'add'],
  ['minus-2', 'remove'],
  ['dot', 'circle'],
]);

const TextCell: React.FC<TextCellProps> = ({ rowData, colData }) => {
  const extendedTextStyle = useMemo(() => {
    let color;
    if (rowData?.indicator?.applySegmentColors) {
      color = rowData?.indicator?.color;
    } else if (rowData.cellForegroundColor) {
      color = rowData.cellForegroundColor;
    }
    return { color, backgroundColor: rowData };
  }, [rowData]);

  const iconStyle = useMemo(() => {
    let name;
    let color;
    if (colData?.representation.type === 'text') {
      return;
    }
    if (rowData?.indicator?.icon) {
      name = iconMap.get(rowData.indicator.icon);
      color = rowData?.indicator?.color;
    }
    return { name, color };
  }, [rowData, colData]);

  return (
    <View style={styles.textRow}>
      <Text style={[styles.textCol2, extendedTextStyle]}>{rowData.qText}</Text>
      {iconStyle?.name ? (
        <MaterialIcons
          style={[styles.icon]}
          name={iconStyle.name}
          color={iconStyle.color}
          size={16}
        />
      ) : null}
    </View>
  );
};

const styles = StyleSheet.create({
  textCol2: {
    fontWeight: 'bold',
    padding: 4,
  },
  miniChart: {
    minHeight: 96,
    borderRadius: 4,
    borderWidth: 1,
    borderColor: '#CCC',
    padding: 4,
  },
  textRow: {
    flexDirection: 'row',
    alignItems: 'flex-start',
  },
  icon: {
    marginLeft: 8,
  },
});

export default TextCell;