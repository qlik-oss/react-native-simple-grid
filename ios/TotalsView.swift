//
//  FooterView.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-17.
//

import Foundation

class TotalsView: HeaderStyleView {
  var totals: Totals?
  var cellStyle: CellContentStyle?
  var dataIndex = [Int]()
  var isFirstColumn = false
  var topShadow = false
  var dynamicHeight = NSLayoutConstraint()
  weak var columnWidths: ColumnWidths?
  weak var borderLayer: CALayer?

  init(
    withTotals totals: Totals,
    dataColumns: [DataColumn],
    cellStyle: CellContentStyle?,
    columnWidths: ColumnWidths,
    withRange range: CountableRange<Int>) {
      super.init(frame: CGRect.zero)
      self.columnWidths = columnWidths
      self.totals = totals
      self.cellStyle = cellStyle
      self.dataRange = range
      self.backgroundColor = .white

      addLabels(dataColumns)
    }

  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  fileprivate func addLabels(_ dataColumns: [DataColumn]) {
    guard let columnWidths = columnWidths else { return }
    guard let totals = totals else { return }
    guard let values = totals.values else { return }

    topShadow = totals.position == "bottom"

    var prev: PaddedLabel?
    values[dataRange].enumerated().forEach {(index, value) in
      let label = PaddedLabel(frame: CGRect.zero, selectionBand: nil)
      let col = dataColumns[index + dataRange.lowerBound]
      label.textColor = ColorParser.fromCSS(cssString: cellStyle?.color ?? "black")
      label.font = UIFont.boldSystemFont(ofSize: 14)
      label.text = value
      label.alignText(from: col.align ?? "")
      let width = columnWidths.columnWidths[index + dataRange.lowerBound]
      self.addSubview(label)
      setupConstraints(label, prev: prev, width: width, index: index)
      prev = label
    }
  }

  func setupConstraints(_ label: PaddedLabel, prev: PaddedLabel?, width: Double, index: Int) {
    let isLast = index == dataRange.count - 1
    label.translatesAutoresizingMaskIntoConstraints = false
    label.dynamicWidth = label.widthAnchor.constraint(equalToConstant: width)
    var constraints = [NSLayoutConstraint]()
    if let previous = prev {
      constraints = [
        label.leadingAnchor.constraint(equalTo: previous.trailingAnchor),
        label.topAnchor.constraint(equalTo: self.topAnchor),
        label.bottomAnchor.constraint(equalTo: self.bottomAnchor)
      ]
    } else {
      constraints = [
        label.leadingAnchor.constraint(equalTo: self.leadingAnchor),
        label.topAnchor.constraint(equalTo: self.topAnchor),
        label.bottomAnchor.constraint(equalTo: self.bottomAnchor)
      ]
    }

   
    constraints.append(label.dynamicWidth)
    

    NSLayoutConstraint.activate(constraints)
    self.addConstraints(constraints)
  }

  func resetTotals(_ newTotals: Totals?) {
    if let nt = newTotals {
      totals = nt
      guard let values = nt.values else {return}
      values[dataRange].enumerated().forEach { (index, element) in
        if let label = subviews[index] as? PaddedLabel {
          label.text = element
        }
      }
    }
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    if topShadow {
      addTopShadow()
    } else {
      addBottomShadow()
    }
  }

  override func updateSize(_ translation: CGPoint, withColumn column: Int) {
    if column < subviews.count {
      let headerCell = subviews[column] as! PaddedLabel
      headerCell.dynamicWidth.constant = headerCell.dynamicWidth.constant + translation.x
      headerCell.layoutIfNeeded()
    }
  }

  func resizeLabels() {
    guard let columnWidths = columnWidths else { return }

    columnWidths.columnWidths[dataRange].enumerated().forEach {(index, width) in
      let headerCell = subviews[index] as! PaddedLabel
      headerCell.dynamicWidth.constant = width
      headerCell.layoutIfNeeded()
    }
  }

  func getMaxLineCount() -> Int {
    var lineCount = 1
    for view in subviews {
      if let paddedCell = view as? PaddedLabel {
        lineCount = max(paddedCell.getLineCount(true), lineCount)
      }
    }
    return lineCount
  }

}
