//
//  HeaderStyleView.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-09-28.
//

import Foundation
class HeaderStyleView : UIView {
  let labelsFactory = LabelsFactory()
  var dataRange:CountableRange<Int> = 0..<1
  
  func updateSize(_ translation: CGPoint, withColumn column: Int) {
    labelsFactory.updateSize(view: self, translation: translation, withColumn: column)
  }
  
  func updateLayer() {
    
  }
  
  override func layoutSubviews() {
    super.layoutSubviews()
    
    CATransaction.begin()
    CATransaction.setValue(kCFBooleanTrue, forKey: kCATransactionDisableActions)
    
    updateLayer()
    
    CATransaction.commit()
  }
  
  func updateFirstCell(_ translation: CGPoint) {
    labelsFactory.updateFirstCell(view: self, translation: translation)
  }
}
