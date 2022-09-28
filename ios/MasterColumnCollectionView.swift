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
  
  override func updateSize(_ translation: CGPoint, withColumn index: Int) -> Bool {
    guard let columnWidths = columnWidths else { return true }
    guard let childCollectionView = childCollectionView else { return true }
    
    if(!updateCellSize(translation, withColumn: index)) {
      return false
    }
       
    let oldFrame = self.frame
    let width = columnWidths.getTotalWidth(range: dataRange)
    let newFrame = CGRect(x: oldFrame.origin.x, y: oldFrame.origin.y, width: width, height: oldFrame.height)
    let delta = newFrame.width - oldFrame.width
    
    if let slave = slave {
      if(freezeFirstColumn) {
        if(!slave.resizeFrozenFirstCell(width)) {
          let inverse = CGPoint(x: -translation.x, y: 0)
          let _ = updateCellSize(inverse, withColumn: index)
          return false
        }
      } else {
        if(!slave.resizeFirstCell(delta)) {
          let inverse = CGPoint(x: -translation.x, y: 0)
          let _ = updateCellSize(inverse, withColumn: index)
          return false
        }
      }
      self.frame = newFrame
      
      childCollectionView.collectionViewLayout.invalidateLayout()
    }
    
    if let headerView = headerView {
      resizeHeaderStyleView(headerView, width: newFrame.width)
      headerView.resizeLabels()
    }
    
    if let totalsView = totalsView {
      resizeHeaderStyleView(totalsView, width: newFrame.width)
    }
        
    return true
  }
  
  func resizeHeaderStyleView(_ view: HeaderStyleView, width: Double) {
    view.frame = CGRect(origin: view.frame.origin, size: CGSize(width: width, height: view.frame.height))
  }
  
}
