//
//  MulitColumnResizer.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-11-04.
//

import Foundation
class MultiColumnResizer : ColumnResizerView {
  
  var adjacentGrabber: MultiColumnResizer?
  var headerView: HeaderView?
  var totalsView: TotalsView?
 
  override init( _ columnWidths: ColumnWidths, index: Int, bindTo bindedTableView: TableView) {
    super.init(columnWidths, index: index, bindTo: bindedTableView)
  }
  
  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }
  
  override func didPan(_ translation: CGPoint) {
    guard let tableView = self.tableView else { return }
    guard let data = tableView.dataCollectionView else { return }
    columnWidths.resize(index: index + 1, by: translation)
    self.centerConstraint.constant  = self.centerConstraint.constant + translation.x
    
    tableView.grow(by: translation.x)
    tableView.layoutIfNeeded()
    let _ = data.updateSize(translation, withColumn: index)
    data.childCollectionView?.collectionViewLayout.invalidateLayout()
    
    updateHeader(translation)
    updateTotals(translation)
    updateAdjacent(by: translation.x)
  }
  
  func updateHeader(_ translation: CGPoint) {
    if let headerView = self.headerView {
      headerView.updateSize(translation, withColumn: index)
    }
    
  }
  func updateTotals(_ translation: CGPoint) {
    if let totals = self.totalsView {
      totals.updateSize(translation, withColumn: index)
    }
  }
  
  func updateAdjacent(by x: Double) {
    if let adjacentGrabber = self.adjacentGrabber {
      adjacentGrabber.centerConstraint.constant = adjacentGrabber.centerConstraint.constant + x
      adjacentGrabber.updateAdjacent(by: x)
    }
    self.layoutIfNeeded()
  }
  
}
