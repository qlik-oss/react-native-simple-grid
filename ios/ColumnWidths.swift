//
//  ColumnWidts.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-08-31.
//

import Foundation
import Combine

class ColumnWidths {
  var columnWidths = [Double]();
  var key: String?
  
  func resetColumnWidths(widths: [Double]) {
    columnWidths = widths
  }
  
  func loadDefaultWidths(_ frame: CGRect, columnCount: Int, dataRows: [DataRow]) {
    if(!loadFromStorage()) {
      let defaultWidth = frame.width / Double(columnCount)
      let widths = [Double](repeating: defaultWidth, count: columnCount)
      resetColumnWidths(widths: widths)
      calculateDefaultColWidth(dataRows: dataRows, defaultWidth: defaultWidth, columnCount: columnCount, frame: frame)
    }
  }
  
  fileprivate func loadFromStorage() -> Bool {
    let storageKey = getStorageKey()
    let defaults = UserDefaults.standard
    if let data = defaults.array(forKey: storageKey)  as? [Double] {
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
    let prefix = UIDevice.current.orientation.isLandscape ? "landscape." : "portrait.";
    let storageKey = prefix + key
    return storageKey
  }
  
  func calculateDefaultColWidth(dataRows: [DataRow], defaultWidth: Double, columnCount: Int, frame: CGRect) {
    // get max width
    var widths = [Double](repeating: defaultWidth, count: columnCount)
    var totalWidth = 0.0
    columnWidths.enumerated().forEach{ (index, value) in
      let maxWidth = getMaxWidthFrom(dataRows: dataRows, index: index)
      widths[index] = maxWidth
      totalWidth += maxWidth
    }
    if(totalWidth > frame.width){
      resetColumnWidths(widths: widths)
    }
    
  }
  
  fileprivate func getMaxWidthFrom(dataRows : [DataRow], index: Int) -> Double {
    var maxWidth = 0.0
    for row in dataRows {
      let fontAttribute = [NSAttributedString.Key.font: UIFont.systemFont(ofSize: 16.0)]
      let text = row.cells[index].qText ?? ""
      let width = text.size(withAttributes: fontAttribute).width + (Double(PaddedLabel.PaddingSize) * 2)
      maxWidth = max(maxWidth, max(width, DataCellView.minWidth))
    }
    return maxWidth
  }
  
  func getTotalWidth() -> Double {
    return columnWidths.reduce(0, { $0 + $1 })
  }
  
  func resize(index: Int, by: CGPoint) {
    columnWidths[index] += by.x
    if index + 1 < columnWidths.count {
      columnWidths[index + 1] -= by.x
    }
  }
  
  
}