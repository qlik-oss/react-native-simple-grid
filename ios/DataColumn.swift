//
//  DataColumn.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-14.
//

import Foundation

struct MiniChartColor: Codable {
  let color: String?
  let index: Int?
}

struct ChartColors: Codable {
  let first: MiniChartColor?
  let last: MiniChartColor?
  let min: MiniChartColor?
  let max: MiniChartColor?
  let negative: MiniChartColor?
  let positive: MiniChartColor?
  let main: MiniChartColor?
}

struct YAxis: Codable {
  let position: String?
  let scale: String?
}

struct MiniChart: Codable {
  let type: String?
  let colors: ChartColors?
  let showDots: Bool?
  let yAxis: YAxis?
}

struct Representation: Codable {
  let type: String?
  let miniChart: MiniChart?
  let globalMax: Double?
  let globalMin: Double?
  let imageSize: String?
  let imageSetting: String?
  let imagePosition: String?
  let linkUrl: String?
  let urlLabel: String?
  let urlPosition: String?
}

struct DataColumn: Codable {
  var isDim: Bool = false
  var active: Bool?
  let label: String?
  let id: String?
  let align: String?
  let sortDirection: String?
  let dataColIdx: Double?
  let representation: Representation?
  let stylingInfo: [String]?
  let widthPercent: Double?
}

struct TotalsCell: Decodable {
  let qText: String?
}

struct Totals: Decodable {
  var label: String?
  var position: String?
  var show: Bool?
  var rows: [TotalsCell]?
  var values: [String]?
}

struct Cols: Decodable {
  let header: [DataColumn]?
  let totals: Totals?
}
