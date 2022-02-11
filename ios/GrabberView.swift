//
//  GrabberView.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-15.
//

import Foundation

class GrabberView: UIView {

  var tableTheme: TableTheme?
  var borderColor = UIColor.gray
  weak var collectionView: DataCollectionView?
  weak var containerView: ContainerView?
  weak var headerView: HeaderView?
  weak var overlayView: OverlayView?
  weak var button: UIView?
  weak var footerView: FooterView?
  var pressed = false

  var linePath = UIBezierPath()
  var colIdx = 0
  init(frame: CGRect, index i: Double, theme: TableTheme) {
    super.init(frame: frame)
    self.tableTheme = theme
    colIdx = Int(i)
    borderColor = ColorParser().fromCSS(cssString: tableTheme?.borderBackgroundColor ?? "gray")
    self.layer.zPosition = 2
    self.backgroundColor = UIColor.white.withAlphaComponent(0)
    createButton()
  }

  required init?(coder: NSCoder) {
    super.init(coder: coder)
  }

  func createButton() {
    let buttonFrame = CGRect(x: 0, y: 0, width: Int(self.frame.width), height: tableTheme!.headerHeight!)
    let button = UIView(frame: buttonFrame)
    addSubview(button)
    self.button = button
    let panGesture = UIPanGestureRecognizer(target: self, action: #selector(self.handleGesture(_:)))
    button.addGestureRecognizer(panGesture)
  }

  override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
    super.touchesBegan(touches, with: event)
    if let touch = touches.first {
      if touch.view == self.button {
        pressed = true
        setNeedsDisplay()
        if let overlayView = overlayView {
          overlayView.pressed(isPressed: true)
        }
      }
    }
  }

  override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
    super.touchesEnded(touches, with: event)
    if let touch = touches.first {
      if touch.view == self.button {
        pressed = false
        setNeedsDisplay()
        if let overlayView = overlayView {
          overlayView.pressed(isPressed: false)
        }
      }
    }
  }

  override func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
    let view = super.hitTest(point, with: event)
    if view == button {
      return view
    }
    if view == self {
      return nil
    }
    return view
  }

  @objc func handleGesture(_ sender: UIPanGestureRecognizer) {
    switch sender.state {
    case .began:
      pressed = true
      self.setNeedsDisplay()
    case .changed:
      let point = sender.translation(in: self)
      onPan(translation: point)
      sender.setTranslation(.zero, in: self)
    case.ended:
      onEndPan()
    case .possible:
      pressed = true
      self.setNeedsDisplay()
    case .cancelled:
      pressed = false
      self.setNeedsDisplay()
    case .failed:
      pressed = false
      self.setNeedsDisplay()
    @unknown default:
      break
    }
  }

  fileprivate func onPan(translation: CGPoint) {
    let point =  CGPoint(x: self.center.x + translation.x, y: self.center.y)
    if let cv = collectionView, let container = containerView, let headerView = headerView {
      if !cv.updateSize(translation, withColumn: colIdx) {
        return
      }
      headerView.updateSize(translation, withColumn: colIdx)
      container.updateSize(colIdx)
    }

    if let footerView = footerView {
      footerView.updateSize(translation, withColumn: colIdx)
    }

    self.center = point

  }

  fileprivate func onEndPan() {
    if let cv = collectionView {
      cv.onEndDrag(colIdx)
    }

    if let container = containerView {
      container.onEndDragged(colIdx)
    }

    pressed = false
    self.setNeedsDisplay()

    if let overlayView = overlayView {
      overlayView.pressed(isPressed: false)
    }
  }

  override func draw(_ rect: CGRect) {
    super.draw(rect)
    let x = rect.origin.x + rect.width / 2
    linePath.move(to: CGPoint(x: x, y: 0))
    linePath.addLine(to: CGPoint(x: x, y: rect.height))
    linePath.lineWidth = 1

    let color = pressed ? .black : borderColor
    color.setStroke()
    linePath.stroke()
  }
}
