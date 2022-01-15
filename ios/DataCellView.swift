//
//  DataCellView.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-15.
//

import Foundation
import UIKit
class DataCellView : UICollectionViewCell {
  
  override init(frame: CGRect) {
    super.init(frame: frame)
    let border = CALayer()
    border.frame = CGRect(x: 0.0, y: frame.size.height-1, width: frame.width, height: 1.0)
    border.backgroundColor = UIColor.black.withAlphaComponent(0.1).cgColor
    self.layer.addSublayer(border)
  }
  
  required init?(coder: NSCoder) {
    super.init(coder: coder)
  }
  
  func setData(row: DataRow, withColumns cols: [DataColumn]) {
    var views = contentView.subviews
    if views.count < row.cells.count {
      var x = 0
      for col in cols {
        let label = UILabel(frame: .zero)
        contentView.addSubview(label)
        x += Int(col.width!)
      }
      views = contentView.subviews
    }
    var x = 0
    row.cells.enumerated().forEach{(index, element) in
      let col = cols[index]
      let label = views[index] as! UILabel
      let newFrame = CGRect(x: x, y: 0, width: Int(col.width!), height: 40)
      label.textAlignment = element.qNum == nil ? .left : .right
      x += Int(col.width!)
      label.frame = newFrame
      label.text = element.qText
    }
  }
  
  func updateSize(_ translation: CGPoint, forColumn index: Int) {
    let view = contentView.subviews[index]
    resizeLabel(view: view, width: view.frame.width + translation.x)
    let next = index + 1
    if next < contentView.subviews.count {
      let v = contentView.subviews[next];
      let old = v.frame
      let new = CGRect(x: old.origin.x + translation.x, y: 0, width: old.width - translation.x, height: 40)
      v.frame = new
    }
  }
  
  fileprivate func resizeLabel(view: UIView, width: CGFloat) {
    if(view.frame.width != width) {
      let oldFrame = view.frame
      let newFrame = CGRect(x: oldFrame.origin.x, y: 0, width: width, height: oldFrame.height)
      view.frame = newFrame
    }
  }
}
