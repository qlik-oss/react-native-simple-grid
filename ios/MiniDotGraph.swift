//
//  MiniDotGraph.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-08-03.
//

import Foundation
//
//  MiniSparkLineChart.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-08-03.
//

class MiniDotGraph: MiniSparkLineChart {

  override func render(_ ctx: CGContext, rect: CGRect) {
    guard let data = data else {return}
    guard let rows = data.qMatrix else {return}
    horizontalPadding = 16
    clearDots()
    if rect.size.height == 0 {return}
    getBandWidth(rect: rect, data: data)
    resetScales(rect)

    var x = padding + horizontalPadding / 2
    var index = 1
    let halfLine = rect.height / 2
    startPath(rows, ctx, x, rect, zeroLine: zeroLine)
    x += padding * 2 + bandWidth
    for row in rows.dropFirst() {
      let value = row[1].qNum ?? 1.0
      let height = value * scale
      let y = rect.height - height
      let x2 = x + padding * 2 + bandWidth
      drawDot(index, value: value, count: rows.count, x: x2, y: y + getVerticalPadding(y, halfLine: halfLine))
      x += padding * 2 + bandWidth
      index += 1
    }
    drawDots(ctx)
  }

}
