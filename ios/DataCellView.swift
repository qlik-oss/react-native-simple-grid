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
class DataCellView: UICollectionViewCell, ExpandedCellProtocol {
  var border = UIBezierPath()
  var dataRow: DataRow?
  var dataColumns: [DataColumn]?
  var selectionsEngine: SelectionsEngine?
  var cellColor: UIColor?
  var numberOfLines = 1
  var isDataView  = true
  var dataRange: CountableRange = 0..<1
  var columnWidths: ColumnWidths?
  var onExpandedCellEvent: RCTDirectEventBlock?
  var menuTranslations: MenuTranslations?
  weak var selectionBand: SelectionBand?
  weak var dataCollectionView: DataCollectionView?
  weak var bottomBorder: CALayer?
  
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
  
  static let minWidth: CGFloat = 60
  
  override init(frame: CGRect) {
    super.init(frame: frame)
    let bottomBorder = CALayer()
    bottomBorder.frame = frame
    bottomBorder.backgroundColor = TableTheme.BorderColor.cgColor
    bottomBorder.masksToBounds = false
    layer.addSublayer(bottomBorder)
    self.bottomBorder = bottomBorder
  }
  
  required init?(coder: NSCoder) {
    super.init(coder: coder)
  }
  
  func setData(row: DataRow,
               dataColumns: [DataColumn],
               columnWidths: ColumnWidths,
               theme: TableTheme,
               selectionsEngine: SelectionsEngine,
               withStyle styleInfo: [StylingInfo],
               cellStyle: CellStyle?,
               withRange dataRange: CountableRange<Int>) {
    self.clipsToBounds = true
    self.dataRow = row
    self.dataRange = dataRange
    self.dataColumns = dataColumns
    self.columnWidths = columnWidths
    createCells(row: row, withColumns: dataColumns, columnWidths: columnWidths, withRange: dataRange)
    let views = contentView.subviews
    row.cells[dataRange].enumerated().forEach {(index, element) in
      let col = dataColumns[index + dataRange.lowerBound]
      if let representation = col.representation {
        if representation.type == "miniChart" && !isDataView {
          if let miniChart = views[index] as? MiniChartView {
            miniChart.menuTranslations = menuTranslations
            miniChart.cell = element
            miniChart.setChartData(data: element, representedAs: representation)
            miniChart.delegate = self
            miniChart.setNeedsDisplay()
          }
        } else if representation.type == "image" && !isDataView {
          if let imageView = views[index] as? ImageCell {
            let index = col.stylingInfo?.firstIndex(of: "imageUrl")
            if let cellBackground = element.cellBackgroundColor {
              imageView.backgroundColor = ColorParser.fromCSS(cssString: cellBackground)
            } else {
              imageView.backgroundColor = .clear
            }
            imageView.prevBackgroundColor = imageView.backgroundColor ?? .clear
            imageView.delegate = self
            imageView.selectionBand = self.selectionBand
            imageView.menuTranslations = menuTranslations
            imageView.setData(data: element, representedAs: representation, index: index)
            imageView.setNeedsDisplay()
          }
        } else {
          if let label = views[index] as? PaddedLabel {
            label.textAlignment = getTextAlignment(element, col: col)// element.qNum == nil ? .left : .right
            label.center = CGPoint(x: floor(label.center.x), y: floor(label.center.y))
            let backgroundColor = getBackgroundColor(col: col, element: element, withStyle: styleInfo[index])
            label.backgroundColor = backgroundColor
            label.numberOfLines = numberOfLines
            label.column = index
            label.cell = element
            label.checkSelected(selectionsEngine)
            label.selectionBand = self.selectionBand
            label.dataCollectionView = self.dataCollectionView
            label.menuTranslations = self.menuTranslations
            label.delegate = self
            label.font = cellStyle?.font ?? UIFont.systemFont(ofSize: 14)
            label.numberOfLines = self.numberOfLines
            
            if representation.type == "indicator", let indicator = element.indicator, let uniChar = DataCellView.iconMap[indicator.icon ?? "m"] {
              label.textColor = getForegroundColor(col: col, element: element, withStyle: styleInfo[index])
              label.setAttributedText(element.qText ?? "", withIcon: uniChar, element: element)
            } else if representation.type == "url" {
              let index = col.stylingInfo?.firstIndex(of: "url")
              label.setupUrl(col, cell: element, index: index)
            } else  {
              label.text = element.qText
              label.textColor = getForegroundColor(col: col, element: element, withStyle: styleInfo[index])
            }
          }
        }
      }
    }
  }
  
  fileprivate func getTextAlignment(_ element: DataCell, col: DataColumn) -> NSTextAlignment {
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
  
  fileprivate func getBackgroundColor(col: DataColumn, element: DataCell, withStyle styleInfo: StylingInfo) -> UIColor {
    if isDataView {
      return .clear
    }
    if styleInfo.backgroundColorIdx == -1 {
      return .clear
    }
    
    if let bgColor = element.cellBackgroundColor {
      return ColorParser.fromCSS(cssString: bgColor)
    }
    return .clear
  }
  
  fileprivate func getForegroundColor(col: DataColumn, element: DataCell, withStyle styleInfo: StylingInfo) -> UIColor {
    if isDataView {
      return cellColor!
    }
    if let fgColor = element.cellForegroundColor {
      return ColorParser.fromCSS(cssString: fgColor)
    }
    return cellColor!
  }
  
  fileprivate func createCells(row: DataRow, withColumns cols: [DataColumn], columnWidths: ColumnWidths, withRange: CountableRange<Int>) {
    let views = contentView.subviews
    if views.count < row.cells[withRange].count {
      clearAllCells()
      var view: UIView?
      var prev: UIView?
      cols[withRange].enumerated().forEach {(index, col) in
        if let representation = col.representation {
          if representation.type == "miniChart" && !isDataView {
            let miniChartView = MiniChartView(frame: .zero)
            view = miniChartView
            self.contentView.addSubview(miniChartView)
          } else if representation.type == "image" && !isDataView {
            let imageCell = ImageCell(selectionBand: selectionBand)
            view = imageCell
            if col.isDim == true {
              if let selectionsEngine = selectionsEngine {
                imageCell.makeSelectable(selectionsEngine: selectionsEngine)
              }
            }
            self.contentView.addSubview(imageCell)
          } else {
            let label = PaddedLabel(frame: .zero, selectionBand: self.selectionBand)
            view = label
            if col.isDim == true {
              if let selectionsEngine = self.selectionsEngine {
                label.makeSelectable(selectionsEngine: selectionsEngine)
              }
            }
            label.adjustsFontForContentSizeCategory = true
            label.showMenus()
            self.contentView.addSubview(label)
          }
        }
        self.setupConstraints(view, prev: prev, width: columnWidths.columnWidths[index + withRange.lowerBound], isLast: index == withRange.count - 1)
        prev = view
      }
    }
  }
  
  fileprivate func setupConstraints(_ view: UIView?, prev: UIView?, width: Double, isLast: Bool) {
    guard let v = view else { return }
    let p = v as! ConstraintCellProtocol
    
    var constraints = [NSLayoutConstraint]()
    
    v.translatesAutoresizingMaskIntoConstraints = false
    p.setDynamicWidth(v.widthAnchor.constraint(equalToConstant: width), value: width)
    if let previous = prev {
      constraints = [
        v.leadingAnchor.constraint(equalTo: previous.trailingAnchor),
        v.topAnchor.constraint(equalTo: self.topAnchor),
        v.bottomAnchor.constraint(equalTo: self.bottomAnchor)
      ]
    } else {
      constraints = [
        v.leadingAnchor.constraint(equalTo: self.leadingAnchor),
        v.topAnchor.constraint(equalTo: self.topAnchor),
        v.bottomAnchor.constraint(equalTo: self.bottomAnchor)
      ]
    }
    
    
    constraints.append(p.getDynamicWidth())
    
    NSLayoutConstraint.activate(constraints)
    self.addConstraints(constraints)
    
  }
  
  fileprivate func clearAllCells() {
    for view in contentView.subviews {
      view.removeFromSuperview()
    }
  }
  
  func updateSize(_ translation: CGPoint, forColumn index: Int) -> Bool {
    guard let columnWidths = columnWidths else { return  false }
    let view = contentView.subviews[index]
    let newWidth = columnWidths.columnWidths[index + dataRange.lowerBound] + translation.x
    if newWidth < DataCellView.minWidth && translation.x < 0 {
      return false
    }
    
    resizeContentView(view: view, width: newWidth)
    
    return true
  }
  
  func resizeCells(_ columnWidths: ColumnWidths, withRange range: CountableRange<Int>) {
    columnWidths.columnWidths[range].enumerated().forEach { (index, width) in
      let view = contentView.subviews[index]
      resizeContentView(view: view, width: width)
    }
  }
  
  fileprivate func resizeContentView(view: UIView, width: CGFloat) {
    // this constraints is only active if it's not the last cell or only cell
    let p = view as! ConstraintCellProtocol
    let widthConstraint = p.getDynamicWidth()
    widthConstraint.constant = width
    view.layoutIfNeeded()
  }
  
  
  
  func onExpandedCell(cell: DataCell) {
    guard let dataRow = self.dataRow else { return }
    guard let dataCols = self.dataColumns else { return }
    guard let expandedCellEvent = self.onExpandedCellEvent else { return }
    do {
      let jsonEncoder = JSONEncoder()
      let jsonData = try jsonEncoder.encode(dataRow)
      let jsonCol = try jsonEncoder.encode(dataCols)
      let row = String(data: jsonData, encoding: String.Encoding.utf8)
      let col = String(data: jsonCol, encoding: String.Encoding.utf8)
      expandedCellEvent(["row": row ?? "", "col": col ?? ""])
    } catch {
      print(error)
    }
  }
  
  func getMaxLineCount() -> Int {
    var maxLineCount = 1
    guard let dataColumns = dataColumns else { return maxLineCount }
    guard let columnWidths = columnWidths else { return maxLineCount }
    contentView.subviews.enumerated().forEach {(index, view) in
      if let constraint = view as? ConstraintCellProtocol {
        let dataColumn = dataColumns[index + dataRange.lowerBound]
        if dataColumn.isDim {
          let columnWidth = columnWidths.columnWidths[index + dataRange.lowerBound]
          maxLineCount = max(constraint.getLineCount(columnWidth: columnWidth), maxLineCount)
        }
      }
    }
    return maxLineCount
  }
  
  override func layoutSubviews() {
    super.layoutSubviews()
    guard let bottomBorder = bottomBorder else { return }
    CATransaction.begin()
    CATransaction.setDisableActions(true)
    bottomBorder.frame = CGRect(origin: CGPoint(x: 0, y: bounds.height - 1), size: CGSize(width: bounds.width, height: 1))
    CATransaction.commit()
  }
  
}
