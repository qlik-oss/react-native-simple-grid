//
//  PositiveNegativeChart.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-08-04.
//

import Foundation
class PositiveNegativeChart : MiniSparkLineChart {
  
  override func render(_ ctx: CGContext, rect: CGRect) {
    guard let data = data else {return}
    guard let rows = data.qMatrix else {return}
    if (rect.size.height == 0) {return}
    ctx.clear(rect)
    getBandWidth(rect: rect, data: data)
    getScale(rect:rect, data:data)
    var x = padding + horizontalPadding / 2;
    var index = 0
    let barHeight = (rect.height - 8) / 2;
    for row in rows {
      let value = row[1].qNum ?? 1.0
      let height = barHeight
      var y = rect.height - height - 4
      if (value > 0) {
        y -= barHeight
      }
      let rect = CGRect(x: x, y: y, width: bandWidth, height:height)
      setColor(index, value: value, count: rows.count)
      ctx.fill(rect)
      x += padding * 2 + bandWidth
      index += 1
    }
  }
  
  override func setColor(_ index: Int, value: Double, count: Int) {
    if (value < 0) {
      negativeColor.set()
    } else {
      positiveColor.set()
    }
  }
  
}
