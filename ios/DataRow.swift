//
//  DataRow.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-14.
//

import Foundation

struct StylingInfo {
  var backgroundColorIdx = -1
  var foregroundColorIdx = -1
}

struct Indicator: Codable {
  var color: String?
  var icon: String?
  var applySegmentColors: Bool?
  var position: String?
  var showTextValues: Bool?
}

struct MatrixCell: Codable {
  let qNum: Double?
  let qText: String?
  enum CodingKeys: String, CodingKey {
    case qNum, qText
  }

  init(from decoder: Decoder) throws {
    let container = try decoder.container(keyedBy: CodingKeys.self)
    self.qText = try container.decodeIfPresent(String.self, forKey: .qText) ?? nil
    if let temp = try? container.decode(Double.self, forKey: .qNum) {
      self.qNum = temp
    } else {
      self.qNum = nil
    }
  }
}

struct Matrix: Codable {
  let qMatrix: [[MatrixCell]]?
  let qMax: Double?
  let qMin: Double?
}

struct AttriExpr: Codable {
  let qValues: [MatrixCell]?
}

struct DataCell: Codable {
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
  var qAttrExps: AttriExpr?
  var indicator: Indicator?
  var cellBackgroundColor: String?
  var cellForegroundColor: String?

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
    case qAttrExps
    case indicator
    case cellBackgroundColor
    case cellForegroundColor
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
    self.qAttrExps = try container.decodeIfPresent(AttriExpr.self, forKey: .qAttrExps) ?? nil
    self.indicator = try container.decodeIfPresent(Indicator.self, forKey: .indicator) ?? nil
    self.cellBackgroundColor = try container.decodeIfPresent(String.self, forKey: .cellBackgroundColor) ?? nil
    self.cellForegroundColor = try container.decodeIfPresent(String.self, forKey: .cellForegroundColor) ?? nil

    if let temp = try? container.decode(Double.self, forKey: .qNum) {
      self.qNum = temp
    } else {
      self.qNum = nil
    }
  }

  public func encode(to encoder: Encoder) throws {
    var container = encoder.container(keyedBy: CodingKeys.self)
    try container.encode(self.qText, forKey: .qText)
    try container.encode(self.qNum, forKey: .qNum)
    try container.encode(self.qElemNumber, forKey: .qElemNumber)
    try container.encode(self.qState, forKey: .qState)
    try container.encode(self.rowIdx, forKey: .rowIdx)
    try container.encode(self.colIdx, forKey: .colIdx)
    try container.encode(self.isDim, forKey: .isDim)
    try container.encode(self.rawRowIdx, forKey: .rawRowIdx)
    try container.encode(self.rawColIdx, forKey: .rawColIdx)
    try container.encode(self.cellBackgroundColor, forKey: .cellBackgroundColor)
    try container.encode(self.cellForegroundColor, forKey: .cellForegroundColor)
    try container.encodeIfPresent(self.qMiniChart, forKey: .qMiniChart)

    if let qAttrExps = self.qAttrExps {
      try container.encode(qAttrExps, forKey: .qAttrExps)
    }
    if let indicator = self.indicator {
      try container.encode(indicator, forKey: .indicator)
    }
  }
}

struct DataRow: Codable {
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
