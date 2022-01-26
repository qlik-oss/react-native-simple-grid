//
//  DataColumn.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-14.
//

import Foundation
struct DataColumn: Codable {
  var isDim: Bool = false
  var active: Bool = false
  var width: Double?
  let label: String?
  let id: String?
  let align: String?
  let sortDirection: String?
  let dataColIdx: Double?
}

struct TotalsCell: Decodable {
  let qText: String?
}

struct Cols: Decodable {
  let header: [DataColumn]?
  let footer: [TotalsCell]?
}
