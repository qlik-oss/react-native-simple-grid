//
//  DataRow.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-14.
//

import Foundation
struct DataCell : Decodable {
  var qText: String?
  var qNum: Double?
  var qElemNumber: Double?
  var qState: String?
  var rowIdx: Double?
  var colIdx: Double?
  var isDim: Bool?
  var rawRowIdx: Double?
  var rawColIdx: Double?
}

struct DataRow: Decodable {
  var cells: [DataCell]
  private struct DynamicCodingKeys: CodingKey {
    var intValue: Int?
    init?(intValue: Int) {
      return nil
    }
    var stringValue: String
    init?(stringValue: String) {
      self.stringValue = stringValue
    }
  }
  init(from decoder: Decoder) throws {
    let container = try decoder.container(keyedBy: DynamicCodingKeys.self)
    var tempArray = [DataCell]()
    for key in container.allKeys {
      if( key.stringValue != "key") {
        let decodedCell = try container.decode(DataCell.self, forKey: DynamicCodingKeys(stringValue: key.stringValue)!)
        tempArray.append(decodedCell)
      }
    }
    cells = tempArray
  }
}



struct RowsObject : Decodable {
  var reset: Bool?
  var rows: [DataRow]?
}
