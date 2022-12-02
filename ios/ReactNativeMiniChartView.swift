//
//  ReactNativeMiniChart.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-10-22.
//

import Foundation

class ReactNativeMiniChartView: UIView {
  var cell: DataCell?
  var col: DataColumn?
  var fillColor = UIColor.clear
  var miniChart = MiniChartRenderer()

  @objc var rowData: NSDictionary = [:] {
    didSet {
      do {
        let json = try JSONSerialization.data(withJSONObject: rowData)
        let decodedCellData = try JSONDecoder().decode(DataCell.self, from: json)
        cell = decodedCellData
        if let cellBackground = cell?.cellBackgroundColor {
          self.fillColor = ColorParser.fromCSS(cssString: cellBackground)
        } else {
          self.fillColor = .clear
        }
      } catch {
        print(error)
      }
    }
  }

  @objc var colData: NSDictionary = [:] {
    didSet {
      do {
        let json = try JSONSerialization.data(withJSONObject: colData)
        let decodeColdata = try JSONDecoder().decode(DataColumn.self, from: json)
        col = decodeColdata
      } catch {
        print(error)
      }
    }
  }

  override var bounds: CGRect {
    didSet {
      self.backgroundColor = UIColor.clear
      self.layer.contentsScale = UIScreen.main.scale
      guard let col = self.col else { return }
      guard let cell = self.cell else { return }
      guard let rep = col.representation else { return }
      if rep.miniChart?.type == "bars" {
        miniChart = MiniBarChart(rep: rep)
      } else if rep.miniChart?.type == "sparkline" {
        miniChart = MiniSparkLineChart(rep: rep)
      } else if rep.miniChart?.type == "dots" {
        miniChart = MiniDotGraph(rep: rep)
      } else if rep.miniChart?.type == "posNeg" {
        miniChart = PositiveNegativeChart(rep: rep)
      }
      miniChart.data = cell.qMiniChart
      miniChart.maxValue = miniChart.data?.qMax ?? Double.infinity
      miniChart.minValue = miniChart.data?.qMin ?? -Double.infinity
      miniChart.globalMaxValue = rep.globalMax ?? Double.infinity
      miniChart.globalMinValue = rep.globalMin ?? -Double.infinity
      miniChart.yScale = miniChart.maxValue
      miniChart.yAxis = rep.miniChart?.yAxis
    }
  }

  override func draw(_ rect: CGRect) {
    guard let ctx = UIGraphicsGetCurrentContext() else { return }
    ctx.setFillColor(fillColor.cgColor)
    ctx.fill(rect)
    miniChart.render(ctx, rect: rect)
  }

}
