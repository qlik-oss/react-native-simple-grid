//
//  ColumnResizerView.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-11-03.
//

import Foundation
class ColumnResizerView: UIView {
  var pressed = false
  var columnWidths: ColumnWidths
  var borderColor = UIColor.gray
  var centerConstraint = NSLayoutConstraint()
  var index = 0
  var linePath = UIBezierPath()
  weak var tableView: TableView?
  weak var adjacentTable: TableView?
  weak var button: ResizerButtonView?
  weak var horizontalScrollView: UIScrollView?
  weak var containerView: ContainerView?

  init( _ columnWidths: ColumnWidths, index: Int, bindTo bindedTableView: TableView) {
    self.columnWidths = columnWidths
    self.tableView = bindedTableView
    self.index = index
    super.init(frame: CGRect.zero)
    self.backgroundColor = .white.withAlphaComponent(0.0)
    self.isUserInteractionEnabled = true
    createButton()
  }

  fileprivate func createButton() {
    let button = ResizerButtonView()
    button.translatesAutoresizingMaskIntoConstraints = false
    addSubview(button)
    button.heightConstraint = button.heightAnchor.constraint(equalToConstant: TableTheme.DefaultCellHeight)
    let constraints = [
      button.topAnchor.constraint(equalTo: self.topAnchor),
      button.widthAnchor.constraint(equalToConstant: TableTheme.DefaultResizerWidth),
      button.leadingAnchor.constraint(equalTo: self.leadingAnchor),
      button.heightConstraint
    ]
    NSLayoutConstraint.activate(constraints)
    self.addConstraints(constraints)

    let panGesture = UIPanGestureRecognizer(target: self, action: #selector(self.handleGesture(_:)))
    panGesture.minimumNumberOfTouches = 1
    panGesture.maximumNumberOfTouches = 1
    button.isUserInteractionEnabled = true
    button.addGestureRecognizer(panGesture)
    self.button = button
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
      self.setNeedsDisplay()
    case .changed:
      let point = sender.translation(in: self)
      didPan(point)
      sender.setTranslation(.zero, in: self)
    case.ended:
      didEndPand()
      pressed = false
      self.setNeedsDisplay()
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

  func didPan(_ translation: CGPoint) {
    guard let tableView = self.tableView else { return }
    guard let data = tableView.dataCollectionView else { return }
    if  data.updateSize(translation, withColumn: index) {
      columnWidths.resize(index: index, by: translation)
      self.centerConstraint.constant  = self.centerConstraint.constant + translation.x

      tableView.grow(by: translation.x)
      if let adjacentTable = self.adjacentTable {
        adjacentTable.dymaniceLeadingAnchor.constant = columnWidths.columnWidths[0]
        adjacentTable.layoutIfNeeded()
      }
      data.childCollectionView?.collectionViewLayout.invalidateLayout()
      containerView?.testTruncation()
      tableView.layoutIfNeeded()
    }
  }

  func didEndPand() {
    columnWidths.saveToStorage()
    if let scrollView = self.horizontalScrollView {
      scrollView.contentSize = CGSize(width: columnWidths.getTotalWidth(), height: 0)
      if let d = scrollView.delegate as? HorizontalScrollViewDelegate {
        d.captureFirstColumnWidth()
      }
    }
  }

  func setPosition(_ x: Double) {
    self.centerConstraint.constant = x
  }

  override func draw(_ rect: CGRect) {
    let x = rect.origin.x + rect.width / 2
    linePath.removeAllPoints()
    linePath.move(to: CGPoint(x: x, y: 0))
    linePath.addLine(to: CGPoint(x: x, y: rect.height))
    linePath.lineWidth = 1

    let color = pressed ? .black : borderColor
    color.setStroke()
    linePath.stroke()
  }

  func setHeight(_ newVal: Double) {
    guard let button = button else { return }
    button.heightConstraint.constant = newVal
    button.layoutIfNeeded()
  }

  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

}
