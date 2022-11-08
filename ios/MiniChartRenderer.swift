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

  func getBandWidth(rect: CGRect, data: Matrix) {
    let count = data.qMatrix?.count ?? 1
    let width = rect.width - horizontalPadding
    let totalBandWidth =  width / CGFloat(count)
    bandWidth = totalBandWidth * 0.8
    padding = totalBandWidth * 0.1
  }

  func getScale(rect: CGRect, data: Matrix) {
    let height = rect.height  - verticalPadding
    scale =   height / yScale
  }

  func render(_ ctx: CGContext, rect: CGRect) {}
}
