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
  enum CodingKeys: String, CodingKey {
      case qText
      case qNum
      case qElemNumber
      case qState
      case rowIdx
      case colIdx
      case isDim
      case rawRowIdx
      case rawColIdx
  }
  
  init(from decoder: Decoder) throws {
    let container = try decoder.container(keyedBy: CodingKeys.self)
    self.qText = try container.decode(String.self, forKey: .qText)
    self.qState = try container.decode(String.self, forKey: .qState)
    self.qElemNumber = try container.decode(Double.self, forKey: .qElemNumber)
    self.rowIdx = try container.decode(Double.self, forKey: .rowIdx)
    self.colIdx = try container.decode(Double.self, forKey: .colIdx)
    self.rawRowIdx = try container.decode(Double.self, forKey: .rawRowIdx)
    self.rawColIdx = try container.decode(Double.self, forKey: .rawColIdx)
    self.isDim = try container.decode(Bool.self, forKey: .isDim)
    
    if let temp = try? container.decode(Double.self, forKey: .qNum) {
      self.qNum = temp
    } else {
      self.qNum = nil
    }
  }
  
  
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
    cells = tempArray.sorted{
      $0.rawColIdx! < $1.rawColIdx!
    }
  }
}



struct RowsObject : Decodable {
  var reset: Bool?
  var rows: [DataRow]?
}
