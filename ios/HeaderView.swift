//
//  HeaderView.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-14.
//

import Foundation
class HeaderView : UIView {
  init(columns: [DataColumn], withTheme theme: TableTheme) {
    // calculate intial total width
    let width = columns.reduce(0, {$0 + $1.width!})
    let frame = CGRect(x: 0, y: 0, width: width, height: Double(theme.headerHeight!))
    super.init(frame: frame)
    addLabels(columns: columns, withTheme: theme)
  }
  
  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }
  
  func addLabels(columns: [DataColumn], withTheme theme: TableTheme) {
    var currentX = 0
    for column in columns {
      let frame = CGRect(x: currentX, y: 0, width: Int(column.width!), height: theme.headerHeight!)
      let label = UILabel(frame: frame)
      label.text = column.label ?? ""
      label.font = UIFont.boldSystemFont(ofSize: label.font.pointSize)
      
      currentX += Int(column.width!)
      addSubview(label)
    }
  }
}
