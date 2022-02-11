//
//  HeaderView.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-14.
//

import Foundation
class HeaderView: UIView {
  let labelsFactory = LabelsFactory()
  var onHeaderPressed: RCTDirectEventBlock?

  init(columns: [DataColumn], withTheme theme: TableTheme, onHeaderPressed: RCTDirectEventBlock? ) {
    // calculate intial total width
    let width = columns.reduce(0, {$0 + $1.width!})
    let frame = CGRect(x: 0, y: 0, width: width, height: Double(theme.headerHeight!))
    super.init(frame: frame)
    self.onHeaderPressed = onHeaderPressed
    addLabels(columns: columns, withTheme: theme)
    self.layer.shadowColor = UIColor.black.cgColor
    self.layer.shadowOpacity = 0.25
    self.layer.shadowOffset = CGSize(width: 0, height: 1)
    self.layer.shadowRadius = 1
    self.layer.zPosition = 1
  }

  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  func addLabels(columns: [DataColumn], withTheme theme: TableTheme) {
    var currentX = 0
    for column in columns {
      let frame = CGRect(x: currentX, y: 0, width: Int(column.width!), height: theme.headerHeight!)
      let label = HeaderCell(frame: frame, dataColumn: column)
      label.onHeaderPressed = onHeaderPressed

      label.text = column.label ?? ""
      label.textColor = ColorParser().fromCSS(cssString: theme.headerTextColor ?? "black")
      label.font = UIFont.preferredFont(forTextStyle: .headline)
      label.adjustsFontForContentSizeCategory = true
      updateIcon(column, forLabel: label)

      currentX += Int(column.width!)
      addSubview(label)
    }
  }

  func updateSize(_ translation: CGPoint, withColumn column: Int) {
    labelsFactory.updateSize(view: self, translation: translation, withColumn: column)
  }

  func updateColumns(_ dataColumns: [DataColumn]) {
    dataColumns.enumerated().forEach { (index, element) in
      if let label = subviews[index] as? PaddedLabel {
        updateIcon(element, forLabel: label)
        label.layer.backgroundColor = UIColor.clear.cgColor
      }
    }
  }

  fileprivate func updateIcon(_ column: DataColumn, forLabel: PaddedLabel) {
    if column.active == true {
      if column.sortDirection == "desc" {
        forLabel.addSystemImage(imageName: "arrowtriangle.down.fill")
      } else {
        forLabel.addSystemImage(imageName: "arrowtriangle.up.fill")
      }
    } else {
      forLabel.removeSystemImage()
    }
  }
}
