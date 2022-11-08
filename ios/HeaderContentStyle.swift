//
//  HeaderContentStyle.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-07-05.
//

import Foundation
struct HeaderContentStyle: Decodable {
  var backgroundColor: String?
  var borderColor: String?
  var borderStyle: String?
  var color: String?
  var fontFamily: String?
  var fontSize: Int?
  var sortLabelColor: String?
  var wrap: Bool?
}

class HeaderStyle {
  var headerContentStyle: HeaderContentStyle?
  var lineHeight = 0.0
  var font: UIFont?

  init(headerContentSyle: HeaderContentStyle?) {
    self.headerContentStyle = headerContentSyle
    if let headerContentSyle = self.headerContentStyle {
      let fontSize = headerContentSyle.fontSize ?? 14
      let sizedFont = UIFont.systemFont(ofSize: CGFloat(fontSize))
      lineHeight =  TableTheme.getLineHeight(sizedFont: sizedFont)
      font = sizedFont
    }
  }
}


