//
//  DataCellView.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-15.
//

import Foundation
import UIKit

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

class DataCellView: UICollectionViewCell {
  var border = UIBezierPath()
  var dataRow: DataRow?
  var borderColor = UIColor.black.withAlphaComponent(0.1)
  var selectionsEngine: SelectionsEngine?
  var cellColor: UIColor?
  var numberOfLines = 1;
  
  static let minWidth: CGFloat = 40
  
  override init(frame: CGRect) {
    super.init(frame: frame)
  }
  
  required init?(coder: NSCoder) {
    super.init(coder: coder)
  }
  
  func setData(row: DataRow, withColumns cols: [DataColumn], theme: TableTheme, selectionsEngine: SelectionsEngine, withStyle styleInfo: StylingInfo) {
    dataRow = row
    borderColor = ColorParser().fromCSS(cssString: theme.borderBackgroundColor ?? "#F0F0F0")
    createCells(row: row, withColumns: cols)
    
    var x = 0
    let views = contentView.subviews
    row.cells.enumerated().forEach {(index, element) in
      let col = cols[index]
      let newFrame = CGRect(x: x, y: 0, width: Int(col.width!), height: theme.rowHeight! * numberOfLines)
      if let representation = col.representation {
        if representation.type == "miniChart" {
          if let miniChart = views[index] as? MiniChartView {
            miniChart.frame = newFrame.integral
            miniChart.setChartData(data: element, representedAs: representation)
            miniChart.setNeedsDisplay()
          }
        } else if(representation.type == "image") {
          if let imageView = views[index] as? ImageCell {
            imageView.frame = newFrame.integral
            imageView.setData(data: element, representedAs: representation)
            imageView.setNeedsDisplay()
          }
        }
        else {
          if let label = views[index] as? PaddedLabel {
            
            label.textAlignment = element.qNum == nil ? .left : .right
            label.frame = newFrame.integral
            label.center = CGPoint(x: floor(label.center.x), y: floor(label.center.y))
            label.text = element.qText
            label.column = index
            label.cell = element
            label.checkSelected(selectionsEngine)
            let backgroundColor = getBackgroundColor(col: col, element: element, withStyle: styleInfo)
            label.backgroundColor = backgroundColor
            label.textColor = isDarkColor(color: backgroundColor) ? .white : getForgroundColor(col: col, element: element, withStyle: styleInfo)
            label.numberOfLines = numberOfLines
            label.checkForUrls()
          }
        }
      }
      x += Int(col.width!)
      
    }
  }
  
 
  
  fileprivate func getBackgroundColor(col: DataColumn, element: DataCell, withStyle styleInfo: StylingInfo) -> UIColor {
    guard let attributes = element.qAttrExps else {return .clear}
    guard let values = attributes.qValues else {return .clear}
    let colorString = values[styleInfo.backgroundColorIdx].qText ?? "none"
    let colorValue = ColorParser().fromCSS(cssString: colorString.lowercased())
    return colorValue
  }
  
  fileprivate func getForgroundColor(col: DataColumn, element: DataCell, withStyle styleInfo: StylingInfo) -> UIColor {
    if let indicator = element.indicator {
      if let color = indicator.color {
        return ColorParser().fromCSS(cssString: color.lowercased())
      }
    }
    guard let attributes = element.qAttrExps else {return cellColor!}
    guard let values = attributes.qValues else {return cellColor!}
    if let qText = values[styleInfo.foregroundColorIdx].qText {
      let colorValue = ColorParser().fromCSS(cssString: qText.lowercased())
      return colorValue
    }
    return cellColor!
  }
  
  fileprivate func createCells(row: DataRow, withColumns cols: [DataColumn]) {
    let views = contentView.subviews
    if views.count < row.cells.count {
      var x = 0
      clearAllCells()
      for col in cols {
        
        if let representation = col.representation {
          if(representation.type == "miniChart") {
            let miniChartView = MiniChartView(frame: .zero)
            contentView.addSubview(miniChartView)
          } else if (representation.type == "image") {
            let imageCell = ImageCell(frame: .zero)
            contentView.addSubview(imageCell)
          }
          else {
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
        
        x += Int(col.width!)
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
