//
//  DataColumn.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-14.
//

import Foundation
struct DataColumn : Decodable {
  let isDim : Bool?
  var width : Double?
  let label: String?
  let id: String?
  let align: String?
  let sortDirection: String?
  let dataColIdx: Double?
}

struct Cols : Decodable {
  let header: [DataColumn]?
}
