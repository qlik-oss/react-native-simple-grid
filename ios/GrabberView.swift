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
  weak var button: UIView?
  weak var totalsView: TotalsView?
  weak var scrollView: UIScrollView?
  weak var guideLineView: GuideLineView?
  var pressed = false
  var trim: CGFloat = 0
  var timer: Timer?
  var currentTranslation = CGPoint.zero
  var shouldExpand = false
  var dataRange: CountableRange = 0..<2
  var linePath = UIBezierPath()
  var guidePath = UIBezierPath()
  var colIdx = 0
  var isLast = false

  init(frame: CGRect, index i: Double, theme: TableTheme, withRange range: CountableRange<Int>) {
    super.init(frame: frame)
    self.tableTheme = theme
    self.dataRange = range
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
      }
    }
  }

  override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
    super.touchesEnded(touches, with: event)
    if let touch = touches.first {
      if touch.view == self.button {
        pressed = false
        setNeedsDisplay()
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
      currentTranslation = CGPoint.zero
      shouldExpand = true
      pressed = true
      if let guideLineView = guideLineView {
        guideLineView.pressed(isPressed: true)
      }
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
      endTimer()
      self.setNeedsDisplay()
    case .failed:
      pressed = false
      endTimer()
      self.setNeedsDisplay()
    @unknown default:
      break
    }
  }

  fileprivate func broadcastTranslation(_ translation: CGPoint) -> Bool {
    let abosluteColIdx = colIdx - dataRange.lowerBound
    if let cv = collectionView, let container = containerView, let headerView = headerView {
      if !cv.updateSize(translation, withColumn: abosluteColIdx) {
        return false
      }
      headerView.updateSize(translation, withColumn: abosluteColIdx)
      container.updateSize(colIdx)
    }

    if let totalsView = totalsView {
      totalsView.updateSize(translation, withColumn: abosluteColIdx)
    }
    return true
  }

  fileprivate func onPan(translation: CGPoint) {
    let point =  CGPoint(x: self.center.x + translation.x, y: self.center.y)

    if broadcastTranslation(translation) {
      if let scrollView = scrollView {
        if isLast && translation.x > 0 {
          var currentOffset = scrollView.contentOffset
          currentOffset.x += translation.x
          scrollView.setContentOffset(currentOffset, animated: false)
          scrollView.flashScrollIndicators()
          currentTranslation = translation
          if let containerView = containerView {
            if scrollView.contentSize.width > containerView.frame.width {
              startTimer()
            } else {
              endTimer()
            }
          }
        } else if isLast && translation.x < -0.01 {
          shouldExpand = false
          endTimer()
        }
      }
      self.center = point
    }
  }

  fileprivate func onEndPan() {
    broadcastUpdate()
    endTimer()
    pressed = false
    if let guideLineView = guideLineView {
      guideLineView.pressed(isPressed: false)
    }
    self.setNeedsDisplay()
  }

  fileprivate func broadcastUpdate() {
    if let container = containerView {
      container.onEndDragged(colIdx)
    }
  }

  func repositionTo(_ x: Double) {
    let point =  CGPoint(x: x, y: self.center.y)
    self.center = point
    self.setNeedsDisplay()
  }

  override func draw(_ rect: CGRect) {
    super.draw(rect)
    let x = rect.origin.x + rect.width / 2
    linePath.move(to: CGPoint(x: x, y: 0))
    linePath.addLine(to: CGPoint(x: x, y: rect.height - trim))
    linePath.lineWidth = 1

    let color = pressed ? .black : borderColor
    color.setStroke()
    linePath.stroke()
  }

  fileprivate func startTimer() {
    if timer != nil {
      return
    }
    if let guideLineView = guideLineView {
      guideLineView.pressed(isPressed: true)
    }
    if !shouldExpand {
      return
    }
    timer = Timer.scheduledTimer(withTimeInterval: 0.0016, repeats: true) { _ in
      self.currentTranslation.x = 1
      _ = self.broadcastTranslation(self.currentTranslation)
      if self.isLast, let scrollView = self.scrollView {
        var currentOffset = scrollView.contentOffset
        currentOffset.x += self.currentTranslation.x
        scrollView.setContentOffset(currentOffset, animated: false)
        scrollView.flashScrollIndicators()
      }
      self.center = CGPoint(x: self.center.x + self.currentTranslation.x, y: self.center.y)
      self.setNeedsDisplay()
    }
  }

  fileprivate func endTimer() {
    if let timer = timer {
      timer.invalidate()
      self.timer = nil
    }
    currentTranslation = CGPoint.zero
  }
}
