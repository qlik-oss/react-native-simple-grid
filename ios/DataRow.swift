//
//  DataRow.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-14.
//

import Foundation
struct MatrixCell: Decodable {
  let qNum: Double?
}

struct Matrix: Decodable {
  let qMatrix: [[MatrixCell]]?
  let qMax: Double?
  let qMin: Double?
}

struct DataCell: Decodable {
  var qText: String?
  var qNum: Double?
  var qElemNumber: Double?
  var qState: String?
  var rowIdx: Double?
  var colIdx: Double?
  var isDim: Bool?
  var rawRowIdx: Double?
  var rawColIdx: Double?
  var qMiniChart: Matrix?
  
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
      case qMiniChart
  }

  init(from decoder: Decoder) throws {
    let container = try decoder.container(keyedBy: CodingKeys.self)
    self.qText = try container.decodeIfPresent(String.self, forKey: .qText) ?? ""
    self.qState = try container.decodeIfPresent(String.self, forKey: .qState) ?? ""
    self.qElemNumber = try container.decodeIfPresent(Double.self, forKey: .qElemNumber) ?? -1
    self.rowIdx = try container.decodeIfPresent(Double.self, forKey: .rowIdx) ?? -1
    self.colIdx = try container.decodeIfPresent(Double.self, forKey: .colIdx) ?? -1
    self.rawRowIdx = try container.decodeIfPresent(Double.self, forKey: .rawRowIdx) ?? -1
    self.rawColIdx = try container.decodeIfPresent(Double.self, forKey: .rawColIdx) ?? -1
    self.isDim = try container.decodeIfPresent(Bool.self, forKey: .isDim) ?? false
    self.qMiniChart = try container.decodeIfPresent(Matrix.self, forKey: .qMiniChart) ?? nil

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

    for key in container.allKeys where key.stringValue != "key" {
      let decodedCell = try container.decode(DataCell.self, forKey: DynamicCodingKeys(stringValue: key.stringValue)!)
      tempArray.append(decodedCell)
    }
    cells = tempArray.sorted {
      $0.rawColIdx! < $1.rawColIdx!
    }
  }
}

struct RowsObject: Decodable {
  var reset: Bool?
  var rows: [DataRow]?
}
