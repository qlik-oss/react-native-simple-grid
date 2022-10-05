//
//  MiniChartView.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-08-03.
//

import Foundation
import QuartzCore

class MiniChartView: UIView {
  var miniChart = MiniChartRenderer()

  func setChartData(data: DataCell, representedAs rep: Representation) {
    self.backgroundColor = UIColor.clear
    self.layer.contentsScale = UIScreen.main.scale
    if rep.miniChart?.type == "bars" {
      miniChart = MiniBarChart(rep: rep)
    } else if rep.miniChart?.type == "sparkline" {
      miniChart = MiniSparkLineChart(rep: rep)
    } else if rep.miniChart?.type == "dots" {
      miniChart = MiniDotGraph(rep: rep)
    } else if rep.miniChart?.type == "posNeg" {
      miniChart = PositiveNegativeChart(rep: rep)
    }
    miniChart.data = data.qMiniChart
    miniChart.maxValue = miniChart.data?.qMax ?? Double.infinity
    miniChart.minValue = miniChart.data?.qMin ?? -Double.infinity
    miniChart.globalMaxValue = rep.globalMax ?? Double.infinity
    miniChart.globalMinValue = rep.globalMin ?? -Double.infinity
    miniChart.yScale = miniChart.maxValue
    if rep.miniChart?.yAxis?.scale == "global" {
      miniChart.yScale = miniChart.globalMaxValue
    }
  }

  override func draw(_ rect: CGRect) {
    guard let ctx = UIGraphicsGetCurrentContext() else { return }
    miniChart.render(ctx, rect: rect)
  }
}
