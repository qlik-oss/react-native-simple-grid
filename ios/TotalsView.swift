//
//  FooterView.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-17.
//

import Foundation

class TotalsView: HeaderStyleView {
  var totals: Totals?
  var theme: TableTheme?
  var cellStyle: CellContentStyle?
  var dataIndex = [Int]()
  weak var columnWidths: ColumnWidths?
  weak var borderLayer: CALayer?

  init(frame: CGRect,
       withTotals totals: Totals,
       dataColumns: [DataColumn],
       theme: TableTheme,
       cellStyle: CellContentStyle,
       columnWidths: ColumnWidths,
       withRange range: CountableRange<Int>) {
    super.init(frame: frame)
    self.columnWidths = columnWidths
    self.totals = totals
    self.theme = theme
    self.cellStyle = cellStyle
    self.dataRange = range
    self.backgroundColor = .white
    addLabels(dataColumns)
    addBorder()
  }

  func addBorder() {

    if let borderLayer = borderLayer {
      borderLayer.removeFromSuperlayer()
    }
    let border = CALayer()
    border.backgroundColor = UIColor.lightGray.cgColor
    self.layer.addSublayer(border)
    self.borderLayer = border
    updateLayer()
  }

  override func updateLayer() {
    guard let borderLayer = borderLayer else {   return  }
    guard let totals = totals else { return   }
    guard let columnWidths = columnWidths else { return }

    let width = columnWidths.getTotalWidth(range: dataRange)

    if totals.position == "bottom" {
      let topBorder = CGRect(x: 0.0, y: 0.0, width: width, height: 1.0)
      borderLayer.frame = topBorder
    } else {
      let bottomBorder = CGRect(x: 0.0, y: self.frame.height - 1.0, width: width, height: 1.0)
      borderLayer.frame = bottomBorder
    }
  }

  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  fileprivate func addLabels(_ dataColumns: [DataColumn]) {
    guard let columnWidths = columnWidths else {
      return
    }

    guard let totals = totals else {
      return
    }

    guard let rows = totals.rows else {
      return
    }

    var currentX = 0
    var currentTotalsIdx = 0
    dataColumns[dataRange].enumerated().forEach { (index, _) in
      let col = dataColumns[index + dataRange.lowerBound]
      let width = columnWidths.columnWidths[index + dataRange.lowerBound]
      let frame = CGRect(x: currentX, y: 0, width: Int(width), height: theme!.headerHeight!)
      let label = PaddedLabel(frame: frame)
      label.textColor = ColorParser().fromCSS(cssString: cellStyle?.color ?? "black")
      label.font = UIFont.boldSystemFont(ofSize: 14)
      if col.isDim == true && index == 0 {
        label.text = "Totals"
      } else if col.isDim == false {
        label.text = rows[currentTotalsIdx].qText ?? "NA"
        label.textAlignment = .right
        dataIndex.append(index)
        currentTotalsIdx += 1
      }
      currentX += Int(width)
      addSubview(label)
    }
  }

  func resetTotals(_ newTotals: Totals?) {
    if let nt = newTotals {
      totals = nt
      guard let rows = nt.rows else {return}
      if rows.count != dataIndex.count {
        return
      }
      rows.enumerated().forEach { (index, element) in
        let labelIndex = dataIndex[index]
        if let label = subviews[labelIndex] as? PaddedLabel {
          label.text = element.qText
        }
      }
    }
  }

  func resizeLabels(withFrame: CGRect) {
    guard let columnWidths = columnWidths else {
      return
    }

    guard let totals = totals else {
      return
    }

    if subviews.count > columnWidths.columnWidths.count {
      return
    }
    var currentX = 0.0
    subviews.enumerated().forEach { (index, value) in
      let width = columnWidths.columnWidths[index + dataRange.lowerBound]
      let newFrame = CGRect(x: currentX, y: 0, width: width, height: value.frame.height)
      value.frame = newFrame
      currentX += width
    }

    let y = totals.position == "bottom" ? withFrame.height - self.frame.height * 2 : self.frame.origin.y
    self.frame = CGRect(origin: CGPoint(x: self.frame.origin.x, y: y), size: CGSize(width: currentX, height: self.frame.height))
  }

}
