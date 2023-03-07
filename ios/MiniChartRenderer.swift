//
//  MiniChart.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-08-03.
//

import Foundation
class MiniChartRenderer {
  var data: Matrix?
  var representation: Representation?
  var firstColor = UIColor.systemBlue
  var lastColor = UIColor.systemBlue
  var mainColor = UIColor.systemBlue
  var positiveColor = UIColor.systemBlue
  var negativeColor = UIColor.systemBlue
  var maxColor = UIColor.systemBlue
  var minColor = UIColor.systemBlue
  var bandWidth = 0.0
  var padding = 0.0
  var maxValue = Double.infinity
  var minValue = -Double.infinity
  var globalMaxValue = Double.infinity
  var globalMinValue = -Double.infinity
  var yScale = Double.infinity
  var scale = 0.0
  var verticalPadding = 8.0
  var horizontalPadding = 8.0
  var showDots = false
  var zeroLine = 0.0
  var yPosition = "auto"
  var posScale = 0.0
  var negScale = 0.0
  var halfScale = 0.0
  var isHalfScale = false
  var yAxis: YAxis?
  var totalBandWidth = 0.0
  var DEFAULT_HORIONTAL_PADDING = 8.0

  init() {

  }

  init(rep: Representation) {
    self.representation = rep
    mainColor = ColorParser.fromCSS(cssString: rep.miniChart?.colors?.main?.color ?? "black")
    firstColor = ColorParser.fromCSS(cssString: rep.miniChart?.colors?.first?.color ?? "black")
    lastColor = ColorParser.fromCSS(cssString: rep.miniChart?.colors?.last?.color ?? "black")
    positiveColor = ColorParser.fromCSS(cssString: rep.miniChart?.colors?.positive?.color ?? "black")
    negativeColor = ColorParser.fromCSS(cssString: rep.miniChart?.colors?.negative?.color ?? "black")
    maxColor = ColorParser.fromCSS(cssString: rep.miniChart?.colors?.max?.color ?? "black")
    minColor = ColorParser.fromCSS(cssString: rep.miniChart?.colors?.min?.color ?? "black")
    showDots = rep.miniChart?.showDots ?? false
  }

  func setColor(_ index: Int, value: Double, count: Int) {
    if value == maxValue && maxColor != .clear {
      maxColor.set()
      return
    }

    if value == minValue && minColor != .clear {
      minColor.set()
      return
    }

    if index == 0 && firstColor != .clear {
      firstColor.set()
      return
    }

    if index == count - 1 && lastColor != .clear {
      lastColor.set()
      return
    }

    mainColor.set()
  }

  func resetScales(_ rect: CGRect) {
    guard let yAxis = yAxis else {return}
    if yAxis.scale == "global" {
      if yAxis.position == "zeroBaseline" {
        let min = min(globalMinValue, 0.0)
        yScale = globalMaxValue - min
      } else {
        yScale = globalMaxValue
      }
    } else {
      if yAxis.position == "zeroBaseline" {
        let min = min(minValue, 0.0)
        yScale = maxValue - min
      } else {
        yScale = maxValue
      }
    }
    setScales(rect)
  }

  func setScales(_ rect: CGRect) {
    var height = rect.height - verticalPadding
    scale = height / yScale
    zeroLine = minValue < 0.0 ? height + minValue * scale : height
    if  yAxis?.position == "zeroCenter" {
      height = rect.height/2.0 - verticalPadding
      scale = height / yScale
      zeroLine = rect.height/2.0 - verticalPadding
    }
  }

  func getBandWidth(rect: CGRect, data: Matrix) {
    let count = data.qMatrix?.count ?? 1
    let width = rect.width - DEFAULT_HORIONTAL_PADDING
    totalBandWidth = min(width * 0.1, width / CGFloat(count))
    bandWidth = totalBandWidth * 0.8
    padding = totalBandWidth * 0.1

    let threshHold = rect.width * 0.4
    let totalWidth = bandWidth * Double(data.qMatrix?.count ?? 1)
    if totalWidth < threshHold {
      horizontalPadding = (rect.width - totalWidth)
    }
  }

  func render(_ ctx: CGContext, rect: CGRect) {}
}
