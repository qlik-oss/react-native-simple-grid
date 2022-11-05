//
//  HeaderView.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-14.
//

import Foundation

class HeaderView: HeaderStyleView {
  var hasShadow = false
  var dynamicHeightAnchor = NSLayoutConstraint()
  var onHeaderPressed: RCTDirectEventBlock?
  var onSearchColumn: RCTDirectEventBlock?
  weak var columnWidths: ColumnWidths?
  weak var bottomBorder: CALayer?

  init(columnWidths: ColumnWidths,
       withRange dataRange: CountableRange<Int>,
       onHeaderPressed: RCTDirectEventBlock?,
       onSearchColumn: RCTDirectEventBlock?) {
    super.init(frame: CGRect.zero)
    self.columnWidths = columnWidths
    self.dataRange = dataRange
    self.onHeaderPressed = onHeaderPressed
    self.onSearchColumn = onSearchColumn
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
  
  func addLabels(columns: [DataColumn],  headerStyle incomingHeaderStyle: HeaderContentStyle?) {
    guard let headerStyle = incomingHeaderStyle else {return}
    guard let columnWidths = self.columnWidths else {return}
    var prev: HeaderCell?
    columns[dataRange].enumerated().forEach{(index, column) in
      let label = HeaderCell(dataColumn: column, onHeaderPressed: onHeaderPressed, onSearchColumn: onSearchColumn)
      let fontSize = 14
      label.setText(column.label ?? "", textColor: ColorParser.fromCSS(cssString: headerStyle.color ?? "black"), align: getTextAlignment(column), fontSize: Double(fontSize))
      addSubview(label)
      setupConstraints(label, width: columnWidths.columnWidths[index + dataRange.lowerBound], prev: prev, index: index)
      prev = label
    }
  }
  
  func setupConstraints(_ label: HeaderCell, width: Double,  prev: HeaderCell?, index: Int) {
    let isLast = index == dataRange.count - 1
    label.translatesAutoresizingMaskIntoConstraints = false
    label.dynamicWidth = label.widthAnchor.constraint(equalToConstant: width)
    var constraints = [NSLayoutConstraint]()
    if let previous = prev {
      constraints = [
        label.leadingAnchor.constraint(equalTo: previous.trailingAnchor),
        label.topAnchor.constraint(equalTo: self.topAnchor),
        label.bottomAnchor.constraint(equalTo: self.bottomAnchor),
      ]
    } else {
      constraints = [
        label.leadingAnchor.constraint(equalTo: self.leadingAnchor),
        label.topAnchor.constraint(equalTo: self.topAnchor),
        label.bottomAnchor.constraint(equalTo: self.bottomAnchor),
      ]
    }
    
    if(isLast) {
      constraints.append(label.trailingAnchor.constraint(equalTo: self.trailingAnchor))
    } else {
      constraints.append(label.dynamicWidth)
    }
    
    NSLayoutConstraint.activate(constraints)
    self.addConstraints(constraints)
  }
  
  fileprivate func getTextAlignment(_ col: DataColumn) -> NSTextAlignment {
    if let align = col.align {
      if align == "right" {
        return .right
      }
      if align == "left" {
        return .left
      }
      if align == "center" {
        return .center
      }
    }
    return .right
  }
  
  func updateColumns(_ dataColumns: [DataColumn]) {
    dataColumns[dataRange].enumerated().forEach { (index, element) in
      if let label = subviews[index] as? HeaderCell {
        label.dataColumn = element
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

    columnWidths.columnWidths[dataRange].enumerated().forEach{(index, width) in
      let headerCell = subviews[index] as! HeaderCell
      headerCell.dynamicWidth.constant = width
      headerCell.layoutIfNeeded()
    }
  }
  
  override func layoutSubviews() {
    super.layoutSubviews()
    if(hasShadow) {
      addBottomShadow()
    }
  }
  
  func getMaxLineCount() -> Int {
    var lineCount = 1
    for view in subviews {
      if let headerCell = view as? HeaderCell {
        lineCount = max(headerCell.getLineCount(), lineCount)
      }
    }
    return lineCount
  }

}
