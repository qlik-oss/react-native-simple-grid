//
//  TableTheme.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-14.
//

import Foundation
enum HorizontalScrollValues: Double {
  case RightScrollContentPadding = 25
  case HorizontalPadding = 6
}

struct TableTheme: Decodable {
  let rowHeight: Int?
  var headerHeight: Int?
  let borderRadius: Int?
  let headerBackgroundColor: String?
  let borderBackgroundColor: String?
  let borderSelectedColor: String?
  let selectedBackground: String?
  let headerTextColor: String?
}
