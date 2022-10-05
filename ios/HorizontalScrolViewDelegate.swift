//
//  HorizontalScrolViewDelegate.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-09-29.
//

import Foundation
class HorizontalScrollViewDelegate: NSObject, UIScrollViewDelegate {

  weak var collectionView: DataCollectionView?
  weak var totalsView: UIView?
  weak var headersView: UIView?

  func scrollViewDidScroll(_ scrollView: UIScrollView) {
    guard let collectionView = collectionView else {return}
    let x = clampScrollPos(scrollView.contentOffset.x)
    let offset = x/100.0
    updateShadow(collectionView, offset: offset)

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
