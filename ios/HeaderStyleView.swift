//
//  HeaderStyleView.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-09-28.
//

import Foundation
class HeaderStyleView: UIView {
  let labelsFactory = LabelsFactory()
  var dataRange: CountableRange<Int> = 0..<1

  func updateSize(_ translation: CGPoint, withColumn column: Int) {
    if column < subviews.count {
      let headerCell = subviews[column] as! HeaderCell
      headerCell.dynamicWidth.constant = headerCell.dynamicWidth.constant + translation.x
      headerCell.layoutIfNeeded()
     
    }
  }
  
}
