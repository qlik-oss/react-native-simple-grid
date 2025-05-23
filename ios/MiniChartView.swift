//
//  MiniChartView.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-08-03.
//

import Foundation
import QuartzCore

class MiniChartView: UIView, ConstraintCellProtocol {
  var miniChart = MiniChartRenderer()
  var dynamicWidth = NSLayoutConstraint()
  let contextMenu = ContextMenu()
  var fillColor = UIColor.clear
  var cell: DataCell?
  var menuTranslations: MenuTranslations?
  weak var delegate: ExpandedCellProtocol?

  override init(frame: CGRect) {
    super.init(frame: frame)
    showMenus()
  }

  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  func getDynamicWidth() -> NSLayoutConstraint {
    return dynamicWidth
  }

  func setDynamicWidth(_ newVal: NSLayoutConstraint, value: Double) {
    dynamicWidth = newVal
  }

  func getLineCount(columnWidth: Double) -> Int {
    return 1
  }

  override var canBecomeFirstResponder: Bool {
    return true
  }

  func showMenus() {
    isUserInteractionEnabled = true
    let longPress = UILongPressGestureRecognizer(target: self, action: #selector(showMenu))
    self.addGestureRecognizer(longPress)
  }

  @objc func showMenu(_ sender: UILongPressGestureRecognizer) {
    self.becomeFirstResponder()
    contextMenu.menuTranslations = self.menuTranslations
    contextMenu.cell = self.cell
    contextMenu.showMenu(sender, view: self)
  }

  func setChartData(data: DataCell, representedAs rep: Representation) {
    self.backgroundColor = UIColor.clear
    self.layer.contentsScale = UIScreen.main.scale
    if rep.miniChart?.type == "bars" {
      miniChart = MiniBarChart(rep: rep)
    } else if rep.miniChart?.type == "sparkline" {
      miniChart = MiniSparkLineChart(rep: rep)
    } else if rep.miniChart?.type == "dots" {
      miniChart = MiniDotGraph(rep: rep)
    } else if rep.miniChart?.type == "posNeg" {
      miniChart = PositiveNegativeChart(rep: rep)
    }
    miniChart.data = data.qMiniChart
    miniChart.maxValue = miniChart.data?.qMax ?? Double.infinity
    miniChart.minValue = miniChart.data?.qMin ?? -Double.infinity
    miniChart.globalMaxValue = rep.globalMax ?? Double.infinity
    miniChart.globalMinValue = rep.globalMin ?? -Double.infinity
    miniChart.yScale = miniChart.maxValue
    miniChart.yAxis = rep.miniChart?.yAxis

  }

  override func layoutSubviews() {
    super.layoutSubviews()
    setNeedsDisplay()
  }

  override func draw(_ rect: CGRect) {
    guard let ctx = UIGraphicsGetCurrentContext() else { return }
    ctx.clear(rect)
    ctx.setFillColor(fillColor.cgColor)
    ctx.fill(rect)
    miniChart.render(ctx, rect: rect)
  }

  @objc func handleCopy(_ controller: UIMenuController) {

    let format = UIGraphicsImageRendererFormat()
    format.scale = UIScreen.main.scale

    let newSize = CGSize(width: frame.size.width * format.scale, height: frame.size.height *  format.scale)
    let rect = CGRect(x: 0, y: 0, width: newSize.width, height: newSize.height)
    let renderer = UIGraphicsImageRenderer(size: newSize, format: format)
    let img = renderer.image { ctx in
      // awesome drawing code
      miniChart.render(ctx.cgContext, rect: rect)
    }

    let board = UIPasteboard.general
    board.image = img
    controller.setMenuVisible(false, animated: true)
    self.resignFirstResponder()
  }

  @objc func handleExpand(_ controller: UIMenuController) {
    guard let cell = self.cell else { return }
    guard let delegate = self.delegate else { return }
    delegate.onExpandedCell(cell: cell)
    self.resignFirstResponder()
  }
}
