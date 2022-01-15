//
//  DataCellView.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-15.
//

import Foundation
class DataCellView : UICollectionViewCell {
  func setData(row: DataRow, withColumns cols: [DataColumn]) {
    var views = contentView.subviews
    if views.count < row.cells.count {
      var x = 0
      for col in cols {
        let frame = CGRect(x: x, y: 0, width: Int(col.width!), height: 48)
        let label = UILabel(frame: frame)
        contentView.addSubview(label)
        x += Int(col.width!)
      }
      views = contentView.subviews
    }
    
    row.cells.enumerated().forEach{(index, element) in
      let label = views[index] as! UILabel
      label.text = element.qText
    }
    
  }
}
