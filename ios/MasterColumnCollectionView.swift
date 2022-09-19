//
//  MasterColumnCollectionView.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-09-12.
//

import Foundation
class MasterColumnCollectionView : DataCollectionView {
  
  init(frame: CGRect,
       withRows rows: [DataRow],
       andColumns cols: [DataColumn],
       theme: TableTheme,
       selectionsEngine: SelectionsEngine,
       cellStyle: CellContentStyle,
       columnWidths: ColumnWidths) {
    super.init(frame: frame,
               withRows: rows,
               andColumns: cols,
               theme: theme,
               selectionsEngine: selectionsEngine,
               cellStyle: cellStyle,
               columnWidths: columnWidths,
               range: 0..<1)
    signalVisibleRows()
  }
  
  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }
  
  override func scrollViewDidScroll(_ scrollView: UIScrollView) {
    syncScrolling()
    signalVisibleRows()
  }
  
  public func scrollViewDidEndDragging(_ scrollView: UIScrollView, willDecelerate decelerate: Bool) {
    signalVisibleRows();
  }
  
  public func scrollViewDidEndDecelerating(_ scrollView: UIScrollView) {
    signalVisibleRows();
  }
  
  public func signalVisibleRows() {
    if let childCollectionView = childCollectionView {
      var min = Int.max
      var max = Int.min
      for cell in childCollectionView.visibleCells {
        let indexPath = childCollectionView.indexPath(for: cell)
        if let indexPath = indexPath {
          if let last  = indexPath.last {
            if last < min {
              min = last
            }
            if last > max {
              max = last
            }
          }
        }
      }
      
      let arrayOfVisibleItems = childCollectionView.indexPathsForVisibleItems.sorted()
      let firstItem = arrayOfVisibleItems.first;
      let lastItem = arrayOfVisibleItems.last;
      if let totalCellsView = totalCellsView, let first = firstItem, let last = lastItem {
        totalCellsView.updateTotals(first: first, last: last)
      }
    }
  }
  
  public func initialSignalVisibleRows() {
    guard let childCollectionView = childCollectionView else {
      return
    }
    
    guard let totalCellsView = totalCellsView else {
      return
    }
    
    let arrayOfVisibleItems = childCollectionView.indexPathsForVisibleItems.sorted()
    let firstItem = arrayOfVisibleItems.first;
    let lastItem = arrayOfVisibleItems.last;
    
    if let firstItem = firstItem, let lastItem = lastItem {
      totalCellsView.updateTotals(first: firstItem, last: lastItem)
    }
  }
  
}
