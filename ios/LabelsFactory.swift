//
//  LabelsFactory.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-17.
//

import Foundation

class LabelsFactory {
  func updateSize(view: UIView, translation: CGPoint, withColumn column: Int) {
    let label = view.subviews[column]
    resizeLabel(view: label, deltaWidth: translation.x, translatingX: 0)
    let next = column + 1
    if next < view.subviews.count {
      let nextView = view.subviews[next]
      resizeLabel(view: nextView, deltaWidth: -translation.x, translatingX: translation.x)
    }
  }

  fileprivate func resizeLabel(view: UIView, deltaWidth: CGFloat, translatingX x: CGFloat) {
    let oldFrame = view.frame
    let newFrame = CGRect(x: oldFrame.origin.x + x, y: 0, width: oldFrame.width + deltaWidth, height: oldFrame.height)
    view.frame = newFrame
  }

  func updateFirstCell(view: UIView, translation: CGPoint) {
    let label = view.subviews[0]
    resizeLabel(view: label, deltaWidth: translation.x, translatingX: 0)
    for index in 1..<view.subviews.count {
      let nextView = view.subviews[index]
      let oldFrame = nextView.frame
      nextView.frame = CGRect(x: oldFrame.origin.x + translation.x, y: oldFrame.origin.y, width: oldFrame.width, height: oldFrame.height)
    }
  }
}
