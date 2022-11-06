//
//  HorizontalScrolViewDelegate.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-09-29.
//

import Foundation
class HorizontalScrollViewDelegate: NSObject, UIScrollViewDelegate {

  weak var tableView: TableView?
  weak var totalsView: UIView?
  weak var headersView: UIView?
  weak var columnWidths: ColumnWidths?
  weak var grabber: ColumnResizerView?
  var translateion = CGPoint.zero
  var width = 0.0

  func captureFirstColumnWidth() {
    guard let columnWidths = self.columnWidths else { return }
    width = columnWidths.columnWidths[0]
  }

  func scrollViewDidScroll(_ scrollView: UIScrollView) {
    guard let tableView = self.tableView else { return }
    guard let columnWidths = self.columnWidths else { return }
    guard let grabber = self.grabber else { return }
    let shadowOffsetX = clampScrollPos(scrollView.contentOffset.x)
    let rawX = scrollView.contentOffset.x

    if rawX <= 0 {
      let x = width - rawX
      let translation = CGPoint(x: x, y: 0)
      columnWidths.columnWidths[0] = x
      tableView.setWidth(x)
      _ = tableView.dataCollectionView?.updateSize(translation, withColumn: 0)
      tableView.dataCollectionView?.childCollectionView?.collectionViewLayout.invalidateLayout()
      grabber.setPosition(x)
    }

    let offset = shadowOffsetX/100.0
    updateShadow(tableView, offset: offset)

    if let totalsView = totalsView {
      updateShadow(totalsView, offset: offset)
    }

    if let headersView = headersView {
      updateShadow(headersView, offset: offset)
    }
  }

  func clampScrollPos(_ x: CGFloat) -> CGFloat {
    return min(100.0, x)
  }

  func updateShadow(_ view: UIView, offset: CGFloat) {
    view.addLeftShadow(radius: 2, opacity: offset, offset: offset)
  }
}
