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
      let label = PaddedLabel(frame: frame)
      label.text = column.label ?? ""
      label.font = UIFont.boldSystemFont(ofSize: label.font.pointSize)
      
      currentX += Int(column.width!)
      addSubview(label)
    }
  }
  
  func updateSize(_ translation: CGPoint, withColumn column: Int) {
    let view = subviews[column]
    resizeLabel(view: view, deltaWidth: translation.x, translatingX: 0)
    let next = column + 1
    if next < subviews.count {
      let nextView = subviews[next]
      resizeLabel(view: nextView, deltaWidth: -translation.x, translatingX: translation.x)
    }
  }
  
  fileprivate func resizeLabel(view: UIView, deltaWidth: CGFloat, translatingX x: CGFloat) {
    let oldFrame = view.frame
    let newFrame = CGRect(x: oldFrame.origin.x + x, y: 0, width: oldFrame.width + deltaWidth, height: oldFrame.height)
    view.frame = newFrame
  }
}
