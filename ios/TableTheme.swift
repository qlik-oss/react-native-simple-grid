//
//  TableTheme.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-14.
//

import Foundation

struct TableTheme: Decodable {
  static let CellContentHeight = 36.0
  static let TotalRowViewHeight = 36.0
  static let DefaultResizerWidth = 30.0
  static let HorizontalScrollPadding = 50.0
  static let BorderColor = UIColor.black.withAlphaComponent(0.1)
  
  let rowHeight: Int?
  var headerHeight: Int?
  let borderRadius: Int?
  let headerBackgroundColor: String?
  let borderBackgroundColor: String?
  let borderSelectedColor: String?
  let selectedBackground: String?
  let headerTextColor: String?

}

extension TableTheme {
  static func getLineHeight(sizedFont: UIFont) -> CGFloat {
    return UIFontMetrics(forTextStyle: .headline).scaledFont(for: sizedFont).lineHeight 
  }
}
