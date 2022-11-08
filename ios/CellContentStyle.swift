//
//  CellContentStyle.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-07-05.
//

import Foundation
struct CellContentStyle: Decodable {
  var borderColor: String?
  var borderStyle: String?
  var color: String?
  var fontFamily: String?
  var fontSize: Int?
  var rowHeight: Int?
  var wrap: Bool?
}


class CellStyle {
  var cellContentStyle: CellContentStyle?
  var lineHeight = 0.0
  var font: UIFont?

  init(cellContentStyle: CellContentStyle?) {
    self.cellContentStyle = cellContentStyle
    if let cellContentStyle = self.cellContentStyle {
      let fontSize = cellContentStyle.fontSize ?? 14
      let sizedFont = UIFont.systemFont(ofSize: CGFloat(fontSize))
      lineHeight =  TableTheme.getLineHeight(sizedFont: sizedFont)
      font = sizedFont
    }
  }
}
