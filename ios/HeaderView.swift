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
  var headerFrame = CGRect.zero
  weak var columnWidths: ColumnWidths?
  weak var bottomBorder: CALayer?
  
  init(columns: [DataColumn], withTheme theme: TableTheme, onHeaderPressed: RCTDirectEventBlock?, headerStyle: HeaderContentStyle, columnWidths: ColumnWidths ) {
    self.columnWidths = columnWidths
    let width = columnWidths.getTotalWidth()
    let frame = CGRect(x: 0, y: 0, width: width, height: Double(theme.headerHeight!))
    headerFrame = frame;
    super.init(frame: frame)
    self.onHeaderPressed = onHeaderPressed
    addLabels(columns: columns, withTheme: theme, andHeaderStyle: headerStyle)
    self.layer.shadowColor = UIColor.black.cgColor
    self.layer.shadowOpacity = 0.15
    self.layer.shadowOffset = CGSize(width: 0, height: 1)
    self.layer.shadowRadius = 2
    self.layer.zPosition = 1
    addBottomBorder()
   
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
  
  func addLabels(columns: [DataColumn], withTheme theme: TableTheme, andHeaderStyle headerStyle: HeaderContentStyle) {
    var currentX = 0
    for column in columns {
      let frame = CGRect(x: currentX, y: 0, width: 200, height: theme.headerHeight!)
      let label = HeaderCell(frame: frame, dataColumn: column)
      label.onHeaderPressed = onHeaderPressed
      
      label.text = column.label ?? ""
      label.textColor = ColorParser().fromCSS(cssString: headerStyle.color ?? "black")
      
      let sizedFont = UIFont.systemFont(ofSize: 14)
      label.font = UIFontMetrics(forTextStyle: .headline).scaledFont(for: sizedFont)
      label.adjustsFontForContentSizeCategory = true
      
      updateIcon(column, forLabel: label)
      
      currentX += 200//Int(column.widths[widthIndex])
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
  
  func resizeLabels() {
    guard let columnWidths = columnWidths else {
      return
    }

    var currentX = 0.0
    subviews.enumerated().forEach{ (index, value) in
      let width = columnWidths.columnWidths[index]
      let newFrame = CGRect(x: currentX, y: 0, width: width , height: value.frame.height)
      value.frame = newFrame
      currentX += width
    }
    self.frame = CGRect(origin: self.frame.origin, size: CGSize(width: currentX, height: self.frame.height))
 
  }
  
  override func layoutSubviews() {
    super.layoutSubviews()
    if let bottomBorder = bottomBorder {
      CATransaction.begin()
      CATransaction.setValue(kCFBooleanTrue, forKey: kCATransactionDisableActions)
      
      bottomBorder.frame = CGRect(origin: bottomBorder.frame.origin, size: CGSize(width: self.frame.width, height: 1))
      
      CATransaction.commit()
    }
  }
}
