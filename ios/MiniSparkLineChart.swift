//
//  MiniSparkLineChart.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-08-03.
//

import Foundation
class MiniSparkLineChart : MiniChartRenderer {
  
  var maxDots = UIBezierPath()
  var firstDot = UIBezierPath()
  var lastDot = UIBezierPath()
  var minDots = UIBezierPath()
  var mainDots = UIBezierPath()
  var linePath = UIBezierPath()
  
  override func render(_ ctx: CGContext, rect: CGRect) {
    guard let data = data else {return}
    guard let rows = data.qMatrix else {return}
    linePath = UIBezierPath()
    clearDots()
    if (rect.size.height == 0) {return}
    ctx.clear(rect)
    getBandWidth(rect: rect, data: data)
    getScale(rect:rect, data:data)
    
    var x = padding + horizontalPadding / 2;
    var index = 1
    startPath(rows, ctx, x, rect)
    x += padding * 2 + bandWidth
    let halfLine = rect.height / 2
    for row in rows.dropFirst() {
      let value = row[1].qNum ?? 1.0
      let height = value * scale;
      let y = rect.height - height;
      let x2 = x + padding * 2 + bandWidth
      let vpadding = getVerticalPadding(y, halfLine: halfLine)
      linePath.addLine(to: CGPoint(x: x2, y: y + vpadding))
      drawDot(index, value: value, count: rows.count, x: x2, y: y + vpadding)
      x += padding * 2 + bandWidth
      index += 1
    }
    if(mainColor != .clear) {
      mainColor.set()
      ctx.addPath(linePath.cgPath);
      ctx.strokePath()
    }
    
    drawDots(ctx)
  }
  
  func getVerticalPadding(_ y: Double, halfLine: Double) -> Double {
    let vp = verticalPadding / 2;
    return y > halfLine ? -vp : vp
  }
  
  func clearDots() {
    
    maxDots = UIBezierPath()
    firstDot = UIBezierPath()
    lastDot = UIBezierPath()
    minDots = UIBezierPath()
    mainDots = UIBezierPath()
  }
  
  func drawDot(_ index: Int, value: Double, count: Int, x: CGFloat, y: CGFloat) {
    if(index == 0 && firstColor != .clear) {
      addArc(firstDot, x: x, y: y)
      return;
    }
    
    if(index == count - 1 && lastColor != .clear) {
      addArc(lastDot, x: x, y: y)
      return;
    }
    
    if(value == maxValue && maxColor != .clear) {
      addArc(maxDots, x: x, y: y)
      return;
    }
    
    if(value == minValue && minColor != .clear) {
      addArc(minDots, x: x, y: y)
      return;
    }
    
    if(showDots) {
      addArc(mainDots, x: x, y: y)
    }
    
  }
  
  func addArc(_ path: UIBezierPath, x: CGFloat, y: CGFloat) {
    path.move(to: CGPoint(x: x, y: y))
    path.addArc(withCenter: CGPoint(x: x, y: y), radius: 2, startAngle: 0, endAngle: CGFloat(Double.pi * 2), clockwise: true)
    path.close()
  }
  
  func startPath(_ rows: [[MatrixCell]], _ ctx: CGContext, _ x: Double, _ rect: CGRect) {
    let startValue = rows[0][1].qNum ?? 0
    let height = startValue * scale;
    let y = rect.height - height;
    let vpadding = getVerticalPadding(y, halfLine: rect.height / 2)
    linePath.move(to:  CGPoint(x: x, y: y + vpadding))
    drawDot(0, value: startValue, count: rows.count, x: x, y: y + vpadding)
  }
  
  func drawDots(_ ctx: CGContext) {
    if(maxColor != .clear) {
      maxColor.set()
      ctx.addPath(maxDots.cgPath)
      ctx.fillPath()

    }

    if(minColor != .clear) {
      minColor.set()
      ctx.addPath(minDots.cgPath)
      ctx.fillPath()
    }

    if(firstColor != .clear) {
      firstColor.set()
      ctx.addPath(firstDot.cgPath)
      ctx.fillPath()
    }

    if(lastColor != .clear) {
      lastColor.set()
      ctx.addPath(lastDot.cgPath)
      ctx.fillPath()
    }
    
    if(showDots) {
      mainColor.set()
      ctx.addPath(mainDots.cgPath)
      ctx.fillPath()
    }
  }
}