//
//  DataCellView.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-15.
//

import Foundation
import UIKit
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

  func setData(row: DataRow, withColumns cols: [DataColumn], theme: TableTheme, selectionsEngine: SelectionsEngine) {
    dataRow = row
    borderColor = ColorParser().fromCSS(cssString: theme.borderBackgroundColor ?? "#F0F0F0")
    createCells(row: row, withColumns: cols)

    var x = 0
    let views = contentView.subviews
    row.cells.enumerated().forEach {(index, element) in
      let col = cols[index]

      if let label = views[index] as? PaddedLabel {
        let newFrame = CGRect(x: x, y: 0, width: Int(col.width!), height: theme.rowHeight! * numberOfLines)
        label.textAlignment = element.qNum == nil ? .left : .right
        x += Int(col.width!)
        label.frame = newFrame.integral
        label.center = CGPoint(x: floor(label.center.x), y: floor(label.center.y))
        label.text = element.qText
        label.column = index
        label.cell = element
        label.checkSelected(selectionsEngine)
        label.textColor = cellColor!
        label.numberOfLines = numberOfLines
      }
    }
  }

  fileprivate func createCells(row: DataRow, withColumns cols: [DataColumn]) {
    let views = contentView.subviews
    if views.count < row.cells.count {
      var x = 0
      clearAllCells()
      for col in cols {
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
    }

    resizeLabel(view: view, width: newWidth)

    return true
  }

  fileprivate func resizeLabel(view: UIView, width: CGFloat) {
    if view.frame.width != width {
      let oldFrame = view.frame
      let newFrame = CGRect(x: oldFrame.origin.x, y: 0, width: width, height: oldFrame.height)
      view.frame = newFrame
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
