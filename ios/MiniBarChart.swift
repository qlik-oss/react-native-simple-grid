//
//  MiniBarChart.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-08-03.
//

import Foundation
class MiniBarChart : MiniChartRenderer {
  
  override func render(_ ctx: CGContext, rect: CGRect) {
    guard let data = data else {return}
    guard let rows = data.qMatrix else {return}
    if (rect.size.height == 0) {return}
    ctx.clear(rect)
    getBandWidth(rect: rect, data: data)
    getScale(rect:rect, data:data)
    var x = padding + horizontalPadding / 2;
    var index = 0
    for row in rows {
      mainColor.set()
      let value = row[1].qNum ?? 1.0
      let height = value * scale;
      let y = rect.height - height - verticalPadding / 2
      let rect = CGRect(x: x, y: y, width: bandWidth, height:height)
      setColor(index, value: value, count: rows.count)
      ctx.fill(rect)
      x += padding * 2 + bandWidth
      index += 1
    }
  }
  
}
