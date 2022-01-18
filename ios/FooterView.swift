//
//  FooterView.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-17.
//

import Foundation
class FooterView : UIView {
  var totals: [TotalsCell]?
  var theme: TableTheme?
  var dataIndex = [Int]()
  let labelsFactory = LabelsFactory()
  init(frame: CGRect, withTotals totals: [TotalsCell], dataColumns: [DataColumn], theme: TableTheme) {
    super.init(frame: frame)
    self.totals = totals
    self.theme = theme
    self.backgroundColor = .white
    self.layer.shadowOpacity = 0.25
    self.layer.shadowOffset = CGSize(width: 0, height: 1)
    self.layer.shadowRadius = 1
    self.layer.zPosition = 1
    addLabels(dataColumns)
  }
  
  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }
  
  fileprivate func addLabels(_ dataColumns: [DataColumn]) {
    var currentX = 0
    var currentTotalsIdx = 0
    dataColumns.enumerated().forEach{ (index, element) in
      let col = dataColumns[index]
      let width = col.width ?? 30
      let frame = CGRect(x: currentX, y: 0, width: Int(width), height: theme!.headerHeight!)
      let label = PaddedLabel(frame: frame)
      label.textColor = ColorParser().fromCSS(cssString: theme!.headerTextColor ?? "black")
      label.font = UIFont.boldSystemFont(ofSize: label.font.pointSize)
      if (col.isDim == true && index == 0) {
        label.text = "Totals"
      } else if col.isDim == false {
        label.text = totals![currentTotalsIdx].qText ?? "NA"
        label.textAlignment = .right
        dataIndex.append(index)
        currentTotalsIdx += 1
      }
      currentX += Int(width)
      addSubview(label)
    }
  }
  
  func resetTotals(_ newTotals: [TotalsCell]?) {
    if let nt = newTotals {
      totals = nt
      nt.enumerated().forEach{ (index, element) in
        let labelIndex = dataIndex[index]
        if let label = subviews[labelIndex] as? PaddedLabel {
          label.text = element.qText
        }
      }
    }
  }
  
  func updateSize(_ translation: CGPoint, withColumn column: Int) {
    labelsFactory.updateSize(view: self, translation: translation, withColumn: column)
  }
}
