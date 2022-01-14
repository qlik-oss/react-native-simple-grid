//
//  TableTheme.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-14.
//

import Foundation
struct TableTheme : Decodable {
  let height: Int?
  var headerHeight: Int? = 54
  let borderRadius: Int?
  let headerBackgroundColor: String?
  let borderBackgroundColor: String?
  let borderSelectedColor: String?
}
