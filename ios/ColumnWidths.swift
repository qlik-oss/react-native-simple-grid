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

  func loadDefaultWidths(_ frame: CGRect, columnCount: Int, dataRows: [DataRow], dataCols: [DataColumn]) {
    if !loadFromStorage(columnCount) {
      // 0.75 looks ugly with single column
      let defaultWidth = columnCount == 1 ? frame.width * 0.9 : frame.width / Double(columnCount)
      let widths = [Double](repeating: defaultWidth, count: columnCount)
      resetColumnWidths(widths: widths)
      calculateDefaultColWidth(dataRows: dataRows, dataCols: dataCols, defaultWidth: defaultWidth, columnCount: columnCount, frame: frame)
    }
    cleanUpValues()
  }

  fileprivate func cleanUpValues() {
    columnWidths = columnWidths.map { (element) -> Double in
      if element < DataCellView.minWidth {
        return DataCellView.minWidth
      }
      return element
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
    let prefix = UIDevice.current.orientation.isLandscape ? "landscape.4." : "portrait.4."
    let storageKey = prefix + key
    return storageKey
  }

  func calculateDefaultColWidth(dataRows: [DataRow], dataCols: [DataColumn], defaultWidth: Double, columnCount: Int, frame: CGRect) {
    // get max width
    var widths = [Double](repeating: defaultWidth, count: columnCount)
    var totalWidth = 0.0
    columnWidths.enumerated().forEach { (index, _) in
      let averageWidth = getAverageWidth(frame, dataRows: dataRows, dataCols: dataCols, index: index)
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

  fileprivate func getAverageWidth(_ frame: CGRect, dataRows: [DataRow], dataCols: [DataColumn], index: Int) -> Double {
    if dataRows.count == 0 {
      return DataCellView.minWidth
    }
    let dataCol = dataCols[index]
    if dataCol.representation?.type != "text" && dataCol.representation?.type != "url" {
      return DataCellView.minWidth * 1.5
    }
    let totalCount = dataRows.reduce(0) { partialResult, row in
      var text = ""
      if dataCol.representation?.type == "url" {
        text = getTextFromUrl(dataCol, row.cells[index])
      } else {
        text = row.cells[index].qText ?? ""
      }
      return partialResult + text.count
    }
    let average = totalCount / (dataRows.count)
    let tempLabel = UILabel()
    tempLabel.font = tempLabel.font.withSize(16)
    tempLabel.text = String(repeating: "M", count: average)
    tempLabel.sizeToFit()
    let newWidth = max(tempLabel.frame.width + (Double(PaddedLabel.PaddingSize) * 2.5), DataCellView.minWidth)
    return min(newWidth, frame.width * 0.75)
  }

  func getTotalWidth() -> Double {
    return columnWidths.reduce(0, { $0 + $1 })
  }

  func getTotalWidth(range: CountableRange<Int>) -> Double {
    return columnWidths[range].reduce(0, { $0 + $1 })
  }

  func resize(index: Int, by: CGPoint) {
    columnWidths[index] += by.x
  }

  func getTextFromUrl(_ col: DataColumn, _ cell: DataCell) -> String {
    guard let stylingInfo = col.stylingInfo else { return cell.qText ?? "" }
    if col.representation?.urlPosition == "dimension" {
      if let index = stylingInfo.firstIndex(of: "urlLabel") {
        return cell.qAttrExps?.qValues?[index].qText ?? cell.qText ?? ""
      }
    }
    return cell.qText ?? ""
  }
}
