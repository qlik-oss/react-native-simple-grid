//
//  ConstraintCellProtocol.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-11-04.
//

import Foundation
protocol ConstraintCellProtocol {
  func getDynamicWidth() -> NSLayoutConstraint
  func setDynamicWidth(_ newVal: NSLayoutConstraint, value: Double)
  func getLineCount(columnWidth: Double) -> Int
}
