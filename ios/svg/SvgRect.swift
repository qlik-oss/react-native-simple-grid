//
//  SvgRect.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2023-02-16.
//

import Foundation
public class SvgRect : SvgShape {
  var position = CGPoint.zero
  var size = CGSize.zero
  override func draw(_ context: CGContext, rect: CGRect) {
    // get percentage
    let widthP = (self.size.width/self.viewPortSize.width)
    let heightP = (self.size.height/self.viewPortSize.height)
    let x = (self.position.x / self.viewPortSize.width)
    let y = (self.position.y / self.viewPortSize.height)
    let rw = CGFloat(rect.width) - padding * 2.0
    let rh = CGFloat(rect.height) - padding * 2.0
    
    let path = UIBezierPath(rect: CGRect(x: x * rw + padding,
                                         y: y * rh + padding,
                                         width: widthP * rw,
                                         height: heightP * rh))
    context.addPath(path.cgPath)
    context.setFillColor(self.fill)
    context.fillPath()
  }
}
