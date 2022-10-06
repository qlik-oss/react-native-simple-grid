//
//  ColumnWidts.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-08-31.
//

import Foundation
import Combine

class ColumnWidths {
  var columnWidths = [Double]()
  var key: String?

  func resetColumnWidths(widths: [Double]) {
    columnWidths = widths
  }

  func count() -> Int {
    return columnWidths.count
  }

  func loadDefaultWidths(_ frame: CGRect, columnCount: Int, dataRows: [DataRow]) {
    if !loadFromStorage(columnCount) {
      let defaultWidth = frame.width / Double(columnCount)
      let widths = [Double](repeating: defaultWidth, count: columnCount)
      resetColumnWidths(widths: widths)
      calculateDefaultColWidth(dataRows: dataRows, defaultWidth: defaultWidth, columnCount: columnCount, frame: frame)
    }
  }

  fileprivate func loadFromStorage(_ columnCount: Int) -> Bool {
    let storageKey = getStorageKey()
    let defaults = UserDefaults.standard
    if let data = defaults.array(forKey: storageKey)  as? [Double] {
      if data.count != columnCount {
        return false
      }
      resetColumnWidths(widths: data)
      return true
    }
    return false
  }

  func saveToStorage() {
    let storageKey = getStorageKey()
    let defaults = UserDefaults.standard
    defaults.set(columnWidths, forKey: storageKey)
  }

  fileprivate func getStorageKey() -> String {
    guard let key = key else {return ""}
    let prefix = UIDevice.current.orientation.isLandscape ? "landscape." : "portrait."
    let storageKey = prefix + key
    return storageKey
  }

  func calculateDefaultColWidth(dataRows: [DataRow], defaultWidth: Double, columnCount: Int, frame: CGRect) {
    // get max width
    var widths = [Double](repeating: defaultWidth, count: columnCount)
    var totalWidth = 0.0
    columnWidths.enumerated().forEach { (index, _) in
      let averageWidth = getAverageWidth(dataRows: dataRows, index: index)
      widths[index] = averageWidth
      totalWidth += averageWidth
    }
    if totalWidth < frame.width {
      columnWidths.enumerated().forEach { (index, _) in
        widths[index] = defaultWidth
      }
    }
    resetColumnWidths(widths: widths)

  }

  fileprivate func getAverageWidth(dataRows: [DataRow], index: Int) -> Double {
    let totalCount = dataRows.reduce(0) { partialResult, row in
      return partialResult + row.cells[index].qText!.count
    }
    let average = totalCount / dataRows.count
    let tempLabel = UILabel()
    tempLabel.font = tempLabel.font.withSize(16)
    tempLabel.text = String(repeating: "M", count: average)
    tempLabel.sizeToFit()
    let newWidth = max(tempLabel.frame.width + (Double(PaddedLabel.PaddingSize) * 2.5), DataCellView.minWidth)
    return newWidth
  }

  func getTotalWidth() -> Double {
    return columnWidths.reduce(0, { $0 + $1 })
  }

  func getTotalWidth(range: CountableRange<Int>) -> Double {
    return columnWidths[range].reduce(0, { $0 + $1 })
  }

  func resize(index: Int, by: CGPoint) {
    columnWidths[index] += by.x
    if index + 1 < columnWidths.count {
      columnWidths[index + 1] -= by.x
    }
  }

}
