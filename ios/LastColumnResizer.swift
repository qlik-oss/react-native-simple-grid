//
//  LastColumnResizer.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-11-04.
//

import Foundation
class LastColumnResizer: MultiColumnResizer {
  override init(_ columnWidths: ColumnWidths, index: Int, bindTo bindedTableView: TableView) {
    super.init(columnWidths, index: index, bindTo: bindedTableView)
  }
  
  override func didPan(_ translation: CGPoint) {
    guard let tableView = self.tableView else { return }
    guard let data = tableView.dataCollectionView else { return }
    guard let scrollView = self.horizontalScrollView else { return }
    if  data.updateSize(translation, withColumn: index ) {
      columnWidths.resize(index: index + 1, by: translation)
      self.centerConstraint.constant  = self.centerConstraint.constant + translation.x
      
      if translation.x > 0 {
        scrollView.contentSize = CGSize(width: scrollView.contentSize.width + translation.x, height: 0)
        scrollView.layoutIfNeeded()
      }
      tableView.grow(by: translation.x)
      tableView.layoutIfNeeded()
      data.childCollectionView?.collectionViewLayout.invalidateLayout()
      updateHeader(translation)
      updateTotals(translation)
      containerView?.testTruncation()
    }
  }
  
  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }
}
