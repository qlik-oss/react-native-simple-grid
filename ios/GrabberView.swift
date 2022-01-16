//
//  GrabberView.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-15.
//

import Foundation
class GrabberView : UIView {

  weak var collectionView: DataCollectionView?
  weak var containerView: ContainerView?
  
  var linePath = UIBezierPath()
  var colIdx = 0;
  init(frame: CGRect, index i : Double) {
    super.init(frame: frame)
    colIdx = Int(i)
    let panGesture = UIPanGestureRecognizer(target: self, action: #selector(self.handleGesture(_:)))
    self.addGestureRecognizer(panGesture)
    self.layer.zPosition = 2
  }
  
  required init?(coder: NSCoder) {
    super.init(coder: coder)
  }
  
  @objc func handleGesture(_ sender: UIPanGestureRecognizer) {
    switch sender.state {
    case .began:
      break
    case .changed:
      let point = sender.translation(in: self)
      onPan(translation: point)
      sender.setTranslation(.zero, in: self)
      break
    case.ended:
      onEndPan()
      break
    case .possible:
      break
    case .cancelled:
      break
    case .failed:
      break
    @unknown default:
      break
    }
  }
  
  fileprivate func onPan(translation: CGPoint) {
    self.center = CGPoint(x: self.center.x + translation.x, y: self.center.y)
    if let cv = collectionView {
      cv.updateSize(translation, withColumn: colIdx)
    }
    
    if let container = containerView {
      container.updateSize(colIdx)
    }
  }
  
  fileprivate func onEndPan() {
    if let cv = collectionView {
      cv.onEndDrag(colIdx)
    }
    
    if let container = containerView {
      container.onEndDragged(colIdx)
    }
  }
  
  override func draw(_ rect: CGRect) {
    super.draw(rect)
    guard let ctx = UIGraphicsGetCurrentContext() else { return }
    ctx.setStrokeColor(UIColor.black.cgColor)
    let x = rect.origin.x + rect.width / 2
    linePath.move(to: CGPoint(x: x, y: 0))
    linePath.addLine(to: CGPoint(x: x, y: rect.height))
    linePath.lineWidth = 1
    UIColor.black.withAlphaComponent(0.1).set()
    linePath.stroke()
  }
}
