//
//  TableView.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-11-03.
//

import Foundation
class TableView : UIView {
  weak var headerView : HeaderView?
  weak var totalView: TotalsView?
  weak var totalRowsView: TotalCellsView?
  weak var dataCollectionView: DataCollectionView?
  weak var horizontalScrolLView: UIScrollView?
  weak var lastGrabber: LastColumnResizer?
  weak var firstGrabber: ColumnResizerView?
  weak var adjacentTable: TableView?
  var dynamicWidth = NSLayoutConstraint()
  var dymaniceLeadingAnchor = NSLayoutConstraint()
  var columnWidths: ColumnWidths?
  var grabbers = [() -> MultiColumnResizer?]()
 
  func grow(by delta: Double) {
    dynamicWidth.constant = self.frame.width + delta;
  }
  
  func setWidth(_ width: Double) {
    dynamicWidth.constant = width
    self.layoutIfNeeded()
  }
  
  func resizeCells() {
    guard let columnWidths = columnWidths else { return }
    guard let dataCollectionView = dataCollectionView else { return }
   
    let width = columnWidths.getTotalWidth(range: dataCollectionView.dataRange)
    setWidth(width)
          
    if let headerView = headerView {
      headerView.resizeLabels()
    }
    
    if let totalView = totalView {
      totalView.resizeLabels()
    }
    
    dataCollectionView.resizeCells()
    updateScrollContentSize(columnWidths)
    repositionGrabbers(columnWidths);
    
    if let adjacentTable = adjacentTable {
      adjacentTable.dymaniceLeadingAnchor.constant = columnWidths.columnWidths[0]
      adjacentTable.setNeedsLayout()
    }
  }
  
  func updateScrollContentSize(_ columnWidths: ColumnWidths) {
    if let horizontalScrollView = horizontalScrolLView {
      let totalWidth = columnWidths.getTotalWidth()
      horizontalScrollView.contentSize = CGSize(width: totalWidth, height: 0)
      horizontalScrollView.contentOffset.x = 0
    }
  }
  
  func repositionGrabbers(_ columnWidths: ColumnWidths) {
    if columnWidths.count() > 1 && !grabbers.isEmpty {
      let range = 1..<columnWidths.count() - 1
      var x = 0.0
      columnWidths.columnWidths[range].enumerated().forEach{(index, width) in
        x += width
        let grabber = grabbers[index]()
        grabber?.centerConstraint.constant = x
        grabber?.setNeedsLayout()
      }
    }
    
    if let firstGrabber = firstGrabber {
      let width = columnWidths.getTotalWidth(range: 0..<1)
      firstGrabber.centerConstraint.constant = width
      firstGrabber.layoutIfNeeded()
    }
    
    if let lastGrabber = lastGrabber {
      let width = columnWidths.getTotalWidth(range: 1..<columnWidths.count())
      
      lastGrabber.centerConstraint.constant = width
      lastGrabber.layoutIfNeeded()
      
    }
    
    
  }
  
}
