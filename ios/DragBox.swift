//
//  DragBox.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-10-13.
//

import Foundation

class DragBox: UIView {

  override func draw(_ rect: CGRect) {

    guard let context = UIGraphicsGetCurrentContext() else {return}

    context.setFillColor(self.backgroundColor?.cgColor ?? UIColor.black.cgColor)
    context.fill(rect)
    // draw two thin grabbers
    // center rect
    let width = rect.width * 0.5
    let x = (rect.width - width) / 2.0
    let g = CGRect(origin: CGPoint(x: x, y: rect.height - 4 ), size: CGSize(width: width, height: 1))
    context.setFillColor(UIColor.white.withAlphaComponent(0.5).cgColor)
    context.fill(g)
    context.fill(g.offsetBy(dx: 0, dy: -2))
  }
}
