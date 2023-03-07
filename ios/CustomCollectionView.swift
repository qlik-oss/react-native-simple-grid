//
//  CustomCollectionView.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2023-02-28.
//

import Foundation

class CustomCollectionView: UICollectionView {

  private var scrollableArea = CGRect.zero
  private var x =  0.0
  public weak var firstColumnTable: TableView?

  override func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
    if gestureRecognizer == self.panGestureRecognizer {
      let location = gestureRecognizer.location(in: self)
      if getScrollableArea().contains(location) {
        return super.gestureRecognizerShouldBegin(gestureRecognizer)
      }
      return false
    }
    return super.gestureRecognizerShouldBegin(gestureRecognizer)
  }

  func setScrollableArea(_ rect: CGRect) {
    self.scrollableArea = rect.insetBy(dx: 25, dy: 0)
  }

  fileprivate func getScrollableArea() -> CGRect {
    guard let scrollView = collectionViewLayout.collectionView else { return CGRect.zero }
    let yOffset = scrollView.contentOffset.y
    var xOffset = x

    if let fct = firstColumnTable {
      xOffset -= fct.frame.size.width
    }
    return scrollableArea.offsetBy(dx: xOffset, dy: yOffset)
  }

  func horizontalScrollOffset(_ x: CGFloat) {
    self.x = x
  }
}
