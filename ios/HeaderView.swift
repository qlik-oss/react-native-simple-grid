//
//  HeaderView.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-14.
//

import Foundation

class HeaderView: HeaderStyleView {
  var onHeaderPressed: RCTDirectEventBlock?
  var onSearchColumn: RCTDirectEventBlock?
  var headerFrame = CGRect.zero
  weak var columnWidths: ColumnWidths?
  weak var bottomBorder: CALayer?

  init(frame: CGRect,
       columns: [DataColumn],
       withTheme theme: TableTheme,
       onHeaderPressed: RCTDirectEventBlock?,
       onSearchColumn: RCTDirectEventBlock?,
       headerStyle: HeaderContentStyle,
       columnWidths: ColumnWidths,
       withRange dataRange: CountableRange<Int> ) {
    super.init(frame: frame)
    self.columnWidths = columnWidths
    self.dataRange = dataRange
    self.onHeaderPressed = onHeaderPressed
    self.onSearchColumn = onSearchColumn
    headerFrame = frame
    addLabels(columns: columns, withTheme: theme, andHeaderStyle: headerStyle)
    addBottomBorder()

  }

  fileprivate func addBottomBorder() {
    if let existing = bottomBorder {
      existing.removeFromSuperlayer()
    }
    let bottomBorder = CALayer()
    bottomBorder.frame = CGRect(x: 0, y: frame.height - 1, width: frame.width, height: 1)
    bottomBorder.backgroundColor = UIColor.lightGray.cgColor

    self.layer.addSublayer(bottomBorder)
    self.bottomBorder = bottomBorder
  }

  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  func addLabels(columns: [DataColumn], withTheme theme: TableTheme, andHeaderStyle headerStyle: HeaderContentStyle) {
    var currentX = 0
    for column in columns[dataRange] {
      let frame = CGRect(x: currentX, y: 0, width: 200, height: theme.headerHeight!)
      let label = HeaderCell(frame: frame, dataColumn: column)
      label.onHeaderPressed = onHeaderPressed
      label.onSearchColumn = onSearchColumn
      label.setText(column.label ?? "", textColor: ColorParser().fromCSS(cssString: headerStyle.color ?? "black"))

      updateSortIndicator(column, forLabel: label)

      currentX += 200// Int(column.widths[widthIndex])
      addSubview(label)
    }
  }

  func updateColumns(_ dataColumns: [DataColumn]) {
    dataColumns[dataRange].enumerated().forEach { (index, element) in
      if let label = subviews[index] as? HeaderCell {
        updateSortIndicator(element, forLabel: label)
        label.layer.backgroundColor = UIColor.clear.cgColor
        label.setNeedsDisplay()
      }
    }
  }

  fileprivate func updateSortIndicator(_ column: DataColumn, forLabel: HeaderCell) {
    if column.active == true {
      if column.sortDirection == "desc" {
        forLabel.setBottomBorder()
      } else {
        forLabel.setTopBorder()
      }
    } else {
      forLabel.clearBorders()
    }
  }

  func resizeLabels() {
    guard let columnWidths = columnWidths else {
      return
    }

    var currentX = 0.0
    subviews.enumerated().forEach { (index, value) in
      let width = columnWidths.columnWidths[index + dataRange.lowerBound]
      let newFrame = CGRect(x: currentX, y: 0, width: width, height: value.frame.height)
      value.frame = newFrame
      currentX += width
    }
    self.frame = CGRect(origin: self.frame.origin, size: CGSize(width: currentX, height: self.frame.height))

  }

  override func updateLayer() {
    if let bottomBorder = bottomBorder {
      bottomBorder.frame = CGRect(origin: bottomBorder.frame.origin, size: CGSize(width: self.frame.width, height: 1))
    }
  }

}
