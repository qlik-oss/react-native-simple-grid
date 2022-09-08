//
//  GuideLineView.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-09-06.
//

import Foundation
class GuideLineView : UIView {
  var containerWidth = 0
  var linePath = UIBezierPath()
  var shapeLayer = CAShapeLayer()
  
  init(frame: CGRect, containerWidth: Int) {
    super.init(frame: frame)
    self.containerWidth = containerWidth - 2
    self.isUserInteractionEnabled = false
    linePath.move(to: CGPoint(x: containerWidth, y: 0))
    linePath.addLine(to: CGPoint(x: containerWidth, y: Int(frame.height)))
    shapeLayer.path = linePath.cgPath
    
    let  dashes: [ CGFloat ] = [ 3, 3 ]
    
    shapeLayer.frame = frame
    shapeLayer.strokeColor = UIColor(red: 176/255, green: 0, blue: 32/255, alpha: 1).cgColor
    shapeLayer.fillColor = nil
    shapeLayer.lineWidth = 2
    shapeLayer.lineDashPattern = dashes as [NSNumber]
    shapeLayer.lineCap = .butt
    shapeLayer.zPosition = 1
    shapeLayer.opacity = 0
    self.layer.addSublayer(shapeLayer)
  }
  
  func resize(withFrame frame: CGRect) {
    linePath = UIBezierPath()
    self.containerWidth = Int(frame.width - 2)
    linePath.move(to: CGPoint(x: containerWidth, y: 0))
    linePath.addLine(to: CGPoint(x: containerWidth, y: Int(frame.height)))
    shapeLayer.path = linePath.cgPath
  }
  
  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }
  
  func pressed(isPressed: Bool) {
    shapeLayer.removeAllAnimations()
    let toValue = isPressed ? 1 : 0
    let fromValue = isPressed ? 0 : 1
    let pathAnimation: CABasicAnimation = CABasicAnimation(keyPath: "opacity")
    pathAnimation.duration = 0.5
    pathAnimation.fromValue = fromValue
    pathAnimation.toValue = toValue
    pathAnimation.isRemovedOnCompletion = false
    pathAnimation.fillMode = .both
    shapeLayer.add(pathAnimation, forKey: "opacity")
  }
}
