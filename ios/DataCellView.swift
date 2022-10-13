//
//  DataCellView.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-15.
//

import Foundation
import UIKit
// tick \u{59451}

func isDarkColor(color: UIColor) -> Bool {
  if color == .clear {
    return false
  }
  var r, g, b, a: CGFloat
  (r, g, b, a) = (0, 0, 0, 0)
  color.getRed(&r, green: &g, blue: &b, alpha: &a)
  let lum = 0.2126 * r + 0.7152 * g + 0.0722 * b
  return  lum < 0.50
}
// icon  String?  "ï"  some
// these are from sense-client
/*
 { key: 'm', value: 'lui-icon--tick' },
  { key: 'ï', value: 'lui-icon--star' },
  { key: 'R', value: 'lui-icon--triangle-top' },
  { key: 'S', value: 'lui-icon--triangle-bottom' },
  { key: 'T', value: 'lui-icon--triangle-left' },
  { key: 'U', value: 'lui-icon--triangle-right' },
  { key: 'P', value: 'lui-icon--plus' },
  { key: 'Q', value: 'lui-icon--minus' },
  { key: 'è', value: 'lui-icon--warning-triangle' },
  { key: '¢', value: 'lui-icon--hand' },
  { key: '©', value: 'lui-icon--flag' },
  { key: '23F4', value: 'lui-icon--lightbulb' },
  { key: '2013', value: 'lui-icon--stop' },
  { key: '&', value: 'lui-icon--pie-chart' },
  { key: 'add', value: 'lui-icon--add' },
  { key: 'minus-2', value: 'lui-icon--minus-2' },
  { key: 'dot', value: 'lui-icon--dot' },
 */
class DataCellView: UICollectionViewCell {
  var border = UIBezierPath()
  var dataRow: DataRow?
  var borderColor = UIColor.black.withAlphaComponent(0.1)
  var selectionsEngine: SelectionsEngine?
  var cellColor: UIColor?
  var numberOfLines = 1
  var isDataView  = true
  weak var selectionBand: SelectionBand?
  weak var dataCollectionView: DataCollectionView?
  
  static let iconMap: [String: UniChar] =  ["m": 0xe96c,
                                            "è": 0xe997,
                                            "ï": 0xe951,
                                            "R": 0xe97f,
                                            "S": 0xe97c,
                                            "T": 0xe97d,
                                            "U": 0xe97e,
                                            "P": 0xe906,
                                            "Q": 0xe8e4,
                                            "¢": 0xe8a8,
                                            "©": 0xe894,
                                            "23F4": 0xe8c7,
                                            "2013": 0xe954,
                                            "&": 0xe8ff,
                                            "add": 0xe802,
                                            "minus-2": 0xe8e3,
                                            "dot": 0xe878
                                            ]

  static let minWidth: CGFloat = 40

  override init(frame: CGRect) {
    super.init(frame: frame)
  }

  required init?(coder: NSCoder) {
    super.init(coder: coder)
  }

  func setData(row: DataRow,
               withColumns cols: ArraySlice<DataColumn>,
               columnWidths: [Double],
               theme: TableTheme,
               selectionsEngine: SelectionsEngine,
               withStyle styleInfo: StylingInfo,
               withRange dataRange: CountableRange<Int>) {
    dataRow = row
    borderColor = ColorParser().fromCSS(cssString: theme.borderBackgroundColor ?? "#F0F0F0")
    createCells(row: row, withColumns: cols, columnWidths: columnWidths, withRange: dataRange)
    var x = 0.0
    let views = contentView.subviews

    row.cells[dataRange].enumerated().forEach {(index, element) in
      let col = cols[index + dataRange.lowerBound]
      let newFrame = CGRect(x: x, y: 0.0, width: (columnWidths[index + dataRange.lowerBound]), height: Double(theme.rowHeight!) * Double(numberOfLines))
      if let representation = col.representation {
        if representation.type == "miniChart" && !isDataView {
          if let miniChart = views[index] as? MiniChartView {
            miniChart.frame = newFrame.integral
            miniChart.setChartData(data: element, representedAs: representation)
            miniChart.setNeedsDisplay()
          }
        } else if representation.type == "image" && !isDataView {
          if let imageView = views[index] as? ImageCell {
            imageView.frame = newFrame.integral
            imageView.setData(data: element, representedAs: representation)
            imageView.setNeedsDisplay()
          }
        } else {
          if let label = views[index] as? PaddedLabel {
            label.textAlignment = element.qNum == nil ? .left : .right
            label.frame = newFrame.integral
            label.center = CGPoint(x: floor(label.center.x), y: floor(label.center.y))
            let backgroundColor = getBackgroundColor(col: col, element: element, withStyle: styleInfo)
            label.backgroundColor = backgroundColor
            label.numberOfLines = numberOfLines
            label.column = index
            label.cell = element
            label.checkSelected(selectionsEngine)
            label.selectionBand = self.selectionBand
            label.dataCollectionView = self.dataCollectionView

            label.checkForUrls()
            if representation.type == "indicator", let indicator = element.indicator, let uniChar = DataCellView.iconMap[indicator.icon ?? "m"] {
              label.setAttributedText(element.qText ?? "", withIcon: uniChar, element: element)
            } else {
              label.text = element.qText
              label.textColor = isDarkColor(color: backgroundColor) ? .white : getForgroundColor(col: col, element: element, withStyle: styleInfo)
            }
          }
        }
      }
      x += columnWidths[index + dataRange.lowerBound]
    }
  }

  fileprivate func getBackgroundColor(col: DataColumn, element: DataCell, withStyle styleInfo: StylingInfo) -> UIColor {
    if isDataView {
      return .clear
    }
    guard let attributes = element.qAttrExps else {return .clear}
    guard let values = attributes.qValues else {return .clear}
    let colorString = values[styleInfo.backgroundColorIdx].qText ?? "none"
    let colorValue = ColorParser().fromCSS(cssString: colorString.lowercased())
    return colorValue
  }

  fileprivate func getForgroundColor(col: DataColumn, element: DataCell, withStyle styleInfo: StylingInfo) -> UIColor {
    if isDataView {
      return cellColor!
    }
    guard let attributes = element.qAttrExps else {return cellColor!}
    guard let values = attributes.qValues else {return cellColor!}
    if let qText = values[styleInfo.foregroundColorIdx].qText {
      let colorValue = ColorParser().fromCSS(cssString: qText.lowercased())
      return colorValue
    }
    return cellColor!
  }

  fileprivate func createCells(row: DataRow, withColumns cols: ArraySlice<DataColumn>, columnWidths: [Double], withRange: CountableRange<Int>) {
    let views = contentView.subviews
    if views.count < row.cells[withRange].count {
      var x = 0
      clearAllCells()
      var index = 0
      for col in cols {
        if let representation = col.representation {
          if representation.type == "miniChart" {
            let miniChartView = MiniChartView(frame: .zero)
            contentView.addSubview(miniChartView)
          } else if representation.type == "image" {
            let imageCell = ImageCell(frame: .zero)
            contentView.addSubview(imageCell)
          } else {
            let label = PaddedLabel(frame: .zero)
            if col.isDim == true {
              if let selectionsEngine = selectionsEngine {
                label.makeSelectable(selectionsEngine: selectionsEngine)
              }
            }
            let sizedFont = UIFont.systemFont(ofSize: 14)
            label.font = UIFontMetrics(forTextStyle: .body).scaledFont(for: sizedFont)
            label.adjustsFontForContentSizeCategory = true
            contentView.addSubview(label)
          }
        }

        x += Int(columnWidths[index + withRange.lowerBound])
        index += 1
      }
    }

  }

  fileprivate func clearAllCells() {
    for view in contentView.subviews {
      view.removeFromSuperview()
    }
  }

  func updateSize(_ translation: CGPoint, forColumn index: Int) -> Bool {
    let view = contentView.subviews[index]
    let newWidth = view.frame.width + translation.x
    if newWidth < DataCellView.minWidth {
      return false
    }
    let next = index + 1
    if next < contentView.subviews.count {
      let v = contentView.subviews[next]
      let old = v.frame
      let newNeighbourWidth = old.width - translation.x
      if newNeighbourWidth < DataCellView.minWidth {
        return false
      }
      let new = CGRect(x: old.origin.x + translation.x, y: 0, width: newNeighbourWidth, height: old.height)
      v.frame = new
      v.setNeedsDisplay()
    }

    resizeContentView(view: view, width: newWidth)

    return true
  }

  func updateFirstCell(_ translation: CGPoint) -> Bool {
    // this only moves the neighbour cell, it does not resize
    let view = contentView.subviews[0]
    let newWidth = view.frame.width + translation.x
    if newWidth < DataCellView.minWidth {
      return false
    }

    for index in 1..<contentView.subviews.count {
      let v = contentView.subviews[index]
      let old = v.frame
      let new = CGRect(x: old.origin.x + translation.x, y: 0, width: old.width, height: old.height)
      v.frame = new
      v.setNeedsDisplay()
    }

    resizeContentView(view: view, width: newWidth)

    return true
  }

  func updateLayout(withColumns cols: [DataColumn], columnWidths: [Double]) {
    var x = 0.0
    contentView.subviews.enumerated().forEach { (index, view) in
      let width = Double(columnWidths[index])
      let newFrame = CGRect(x: x, y: self.frame.origin.y, width: Double(width), height: self.frame.size.height)
      view.frame = newFrame
      x += width
      view.setNeedsDisplay()
    }
  }

  fileprivate func resizeContentView(view: UIView, width: CGFloat) {
    if view.frame.width != width {
      let oldFrame = view.frame
      let newFrame = CGRect(x: oldFrame.origin.x, y: 0, width: width, height: oldFrame.height)
      view.frame = newFrame

      view.setNeedsDisplay()
    }

  }

  override func draw(_ rect: CGRect) {
    super.draw(rect)

    border.move(to: CGPoint(x: 0, y: rect.height))
    border.addLine(to: CGPoint(x: rect.width, y: rect.height))
    border.close()

    border.lineWidth = 1
    borderColor.set()
    border.stroke()

  }
}
