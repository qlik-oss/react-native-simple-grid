//
//  TableTheme.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-14.
//

import Foundation

struct TableTheme: Decodable {
  static let DefaultCellHeight = 36.0
  static let DefaultResizerWidth = 30.0
  static let HorizontalScrollPadding = 50.0
  static var HeaderLineHeight = 36.0
  let rowHeight: Int?
  var headerHeight: Int?
  let borderRadius: Int?
  let headerBackgroundColor: String?
  let borderBackgroundColor: String?
  let borderSelectedColor: String?
  let selectedBackground: String?
  let headerTextColor: String?

}
