//
//  HorizontalScrolViewDelegate.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-09-29.
//

import Foundation
class HorizontalScrollViewDelegate: NSObject, UIScrollViewDelegate {
  weak var tableView: TableView?
  weak var multiColumnTable: TableView?
  weak var totalsView: UIView?
  weak var headersView: UIView?
  weak var columnWidths: ColumnWidths?
  weak var grabber: ColumnResizerView?
  var translateion = CGPoint.zero
  var width = 0.0
  var freezeFirstCol = false

  func captureFirstColumnWidth() {
    guard let columnWidths = self.columnWidths else { return }
    width = columnWidths.columnWidths[0]
  }
   

  func scrollViewDidScroll(_ scrollView: UIScrollView) {
    guard let columnWidths = self.columnWidths else { return }
    guard let tableView = self.tableView else { return }
    guard let grabber = self.grabber else { return }
    
    let totalWidth = columnWidths.getTotalWidth()
    let shadowOffsetX = clampScrollPos(Float(scrollView.contentOffset.x))
    let rawX = scrollView.contentOffset.x
    let right = max(abs(scrollView.frame.width  -  totalWidth) - rawX, 0)
    
    
   
    if(rawX >= 0) {
      multiColumnTable?.dataCollectionView?.childCollectionView?.scrollIndicatorInsets = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: right)
    }
    if (freezeFirstCol) {
      if rawX <= 0 {
        multiColumnTable?.dataCollectionView?.childCollectionView?.showsVerticalScrollIndicator = false
        let x = width - rawX
        let translation = CGPoint(x: x, y: 0)
        columnWidths.columnWidths[0] = x
        tableView.setWidth(x)
        _ = tableView.dataCollectionView?.updateSize(translation, withColumn: 0)
        tableView.dataCollectionView?.childCollectionView?.collectionViewLayout.invalidateLayout()
        grabber.setPosition(x)
      }
      
      let offset = shadowOffsetX/100.0
      updateShadow(offset: offset)
    }
    
  }

  func clampScrollPos(_ x: Float) -> Float {
    return min(100.0, x)
  }

  func updateShadow(offset: Float) {
    tableView?.setShadow(offset: min(offset, 0.4))
    tableView?.setNeedsDisplay()
  }
  
  func updateVScroll() {
    
  }
}
