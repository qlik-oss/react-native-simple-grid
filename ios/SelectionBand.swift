//
//  SelectionBand.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-10-11.
//

import Foundation

class SelectionBandEnvelope {
  var frame = CGRect.zero
  var sender: UIView
  var colIdx = -1.0
  init(_ f: CGRect, sender: UIView) {
    self.sender = sender
    self.frame = f
  }

  init(_ f: CGRect, sender: UIView, colIdx: Double) {
    self.sender = sender
    self.frame = f
    self.colIdx = colIdx
  }
}

class SelectionBand: UIView {
  var touchDownPoint = CGPoint.zero
  var currentPoint = CGPoint.zero
  var translation = CGPoint.zero
  var acticationFrame = CGRect.zero
  var drawing = false
  var activeColIdx = -1.0
  var topDragger = false
  let path = UIBezierPath()
  let borderColor = UIColor.black
  let dragBoxSize = CGSize(width: 12, height: 12)
  let grabberColor = UIColor(red: 0.00, green: 0.36, blue: 0.73, alpha: 1.00)
  weak var selectionBandResizer: UIView?
  weak var parentCollectionView: DataCollectionView?
  weak var dragBox: UIView?

  override init(frame: CGRect) {
    super.init(frame: frame)
    let panGesture = UIPanGestureRecognizer(target: self, action: #selector(self.handleGesture(_:)))
    self.addGestureRecognizer(panGesture)
    self.backgroundColor = UIColor(white: 1, alpha: 0)
    self.layer.borderColor = UIColor.red.cgColor

    let selectionBandResizer = UIView(frame: CGRect(origin: CGPoint.zero, size: CGSize.zero))
    selectionBandResizer.backgroundColor = UIColor.black.withAlphaComponent(0.0)
    selectionBandResizer.layer.borderWidth = 1.0
    selectionBandResizer.layer.borderColor = grabberColor.cgColor

    addSubview(selectionBandResizer)
    self.selectionBandResizer = selectionBandResizer

    createDragBoxes()

    NotificationCenter.default.addObserver(self, selector: #selector(handleClearSelection), name: Notification.Name.onClearSelectionBand, object: nil)
  }

  fileprivate func createDragBoxes() {
    let dragBox = createDragBox()
    self.dragBox = dragBox
  }

  fileprivate func createDragBox() -> UIView {
    let view = DragBox(frame: CGRect.zero)
    view.backgroundColor = grabberColor

    view.layer.masksToBounds = false
    view.layer.shadowColor = UIColor.black.cgColor
    view.layer.shadowOpacity = 0.5
    view.layer.shadowOffset = .zero
    view.layer.shadowRadius = 2
    view.layer.cornerRadius = 2

    addSubview(view)
    return view
  }

  override func point(inside point: CGPoint, with event: UIEvent?) -> Bool {
    guard let dragBox = self.dragBox else { return false }

    if dragBox.hitTest(convert(point, to: dragBox), with: event) != nil {
      return true
    }
    return false
  }

  required init?(coder: NSCoder) {
    super.init(coder: coder)
  }

  func handleActivation(_ envelope: SelectionBandEnvelope) {
    guard let selectionBandResizer = self.selectionBandResizer else { return }
    topDragger = false
    selectionBandResizer.frame = envelope.frame
    touchDownPoint = envelope.frame.origin
    activeColIdx = envelope.colIdx
    updateDragBox(selectionBandResizer: selectionBandResizer)
    setNeedsDisplay()
  }

  fileprivate func updateDragBox(selectionBandResizer: UIView) {
    guard let dragBox = self.dragBox else { return }
    let y = topDragger ? selectionBandResizer.frame.origin.y - dragBoxSize.height / 2 :
                         selectionBandResizer.frame.origin.y + selectionBandResizer.frame.height - dragBoxSize.height / 2
    let bottomRightPoint = CGPoint(x: selectionBandResizer.frame.origin.x, y: y)

    dragBox.frame = CGRect(origin: bottomRightPoint, size: CGSize(width: selectionBandResizer.frame.width, height: dragBoxSize.height))
    dragBox.setNeedsDisplay()
  }

  @objc func handleClearSelection(notification: Notification) {
    clearRect()
  }

  @objc func selectionBandResizerTapped(_ sender: UITapGestureRecognizer) {
    let point = sender.location(in: self)
    NotificationCenter.default.post(name: Notification.Name.onTappedSelectionBand, object: point)
  }

  @objc func handleGesture(_ sender: UIPanGestureRecognizer) {
    switch sender.state {
    case .began:
      self.setNeedsDisplay()
    case .changed:
      currentPoint = sender.location(in: self)
      translation = sender.translation(in: self)
      handleDrag()
    case.ended:
      NotificationCenter.default.post(name: Notification.Name.onDragSelectDone, object: nil)
      clearRect()
      self.setNeedsDisplay()
    case .cancelled:
      clearRect()
      self.setNeedsDisplay()
    case .failed:
      clearRect()
      self.setNeedsDisplay()
    case .possible:
      self.setNeedsDisplay()
    @unknown default:
      break
    }
  }

  fileprivate func handleDrag() {
    // get height
    guard let selectionBandResizer = self.selectionBandResizer else { return }
    let height = (currentPoint.y - touchDownPoint.y)
    var newFrame = CGRect.zero
    if height < 0 {
      topDragger = true
      let x = selectionBandResizer.frame.origin.x
      let y = touchDownPoint.y + height + acticationFrame.height
      newFrame = CGRect(origin: CGPoint(x: x, y: y), size: CGSize(width: selectionBandResizer.frame.width, height: -height))
    } else {
      topDragger = false
      newFrame = CGRect(origin: selectionBandResizer.frame.origin, size: CGSize(width: selectionBandResizer.frame.width, height: height))
    }
    selectionBandResizer.frame = newFrame
    updateDragBox(selectionBandResizer: selectionBandResizer)
    let envelope = SelectionBandEnvelope(newFrame, sender: self)
    NotificationCenter.default.post(name: Notification.Name.onSelectionDragged, object: envelope)
  }

  func clearRect () {
    guard let selectionBandResizer = self.selectionBandResizer else { return }
    selectionBandResizer.frame = CGRect.zero
    translation = CGPoint.zero
    clearDraggers()
    self.setNeedsDisplay()
  }

  func clearDraggers() {
    guard let dragBox = self.dragBox else { return }
    dragBox.frame = CGRect.zero
  }

  func updateSize(_ translation: CGPoint, withColumn col: Int) {
    guard let selectionBandResizer = self.selectionBandResizer else { return }
    if Double(col) == activeColIdx && selectionBandResizer.frame.width > 0 {
      let newFrame = CGRect(origin: selectionBandResizer.frame.origin,
                            size: CGSize(width: selectionBandResizer.frame.width + translation.x,
                                         height: selectionBandResizer.frame.height))

      selectionBandResizer.frame = newFrame
      updateDragBox(selectionBandResizer: selectionBandResizer)
      selectionBandResizer.setNeedsDisplay()
    }
  }

}
