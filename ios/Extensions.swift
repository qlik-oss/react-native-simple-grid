//
//  Extensions.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-09-19.
//

import Foundation
extension UIView {
  func addBottomShadow() {
    layer.shadowRadius = 2
    layer.shadowOpacity = 0.15
    layer.shadowColor = UIColor.gray.cgColor
    layer.shadowOffset = CGSize(width: 0, height: 1)
    layer.shadowPath = UIBezierPath(rect: CGRect(x: 0,
                                                 y: bounds.maxY - layer.shadowRadius,
                                                 width: bounds.width,
                                                 height: layer.shadowRadius)).cgPath

  }

  func addLeftShadow(radius: CGFloat, opacity: CGFloat, offset: CGFloat) {

    layer.shadowRadius = radius
    layer.shadowOpacity = Float(opacity)
    layer.shadowColor = UIColor.gray.cgColor
    layer.shadowOffset = CGSize(width: offset, height: offset)
    layer.shadowPath = UIBezierPath(rect: CGRect(x: bounds.maxX - layer.shadowRadius,
                                                 y: 0,
                                                 width: radius,
                                                 height: bounds.height)).cgPath

  }
}

extension Notification.Name {
  static let onTappedSelection = Notification.Name("onTappedSelection")
  static let onTappedSelectionBand = Notification.Name("onTappedSelectionBand")
  static let onClearSelectionBand = Notification.Name("onClearSelectionBand")
  static let onSelectionDragged = Notification.Name("onSelectionDragged")
  static let onDragSelectDone = Notification.Name("onDragSelectDone")
}
