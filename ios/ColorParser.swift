//
//  ColorParser.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-14.
//

import Foundation
class ColorParser {
  let SupportedColors: [String: UIColor] = [
    "red": .red,
    "blue": .blue,
    "green": .green,
    "black": .black,
    "white": .white,
    "gray": .gray,
    "cyan": .cyan,
    "magenta": .magenta,
    "yellow": .yellow,
    "lightgray": .lightGray,
    "darkgray": .darkGray,
    "grey": .gray,
    "lightgrey": .lightGray,
    "darkgrey": .darkGray,
    "purple": .purple,
    "teal": .systemTeal]

  func fromCSS(cssString: String) -> UIColor {
    if(cssString == "none") {
      return UIColor.clear
    }
    let hex = cssString.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
    if cssString.hasPrefix("#") {
      var int = UInt64()
      Scanner(string: hex).scanHexInt64(&int)
      let a, r, g, b: UInt64
      switch hex.count {
      case 3: // RGB (12-bit)
        (a, r, g, b) = (255, (int >> 8) * 17, (int >> 4 & 0xF) * 17, (int & 0xF) * 17)
      case 6: // RGB (24-bit)
        (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
      case 8: // ARGB (32-bit)
        (a, r, g, b) = (int >> 24, int >> 16 & 0xFF, int >> 8 & 0xFF, int & 0xFF)
      default:
        (a, r, g, b) = (255, 0, 0, 0)
      }
      return UIColor(red: CGFloat(r) / 255, green: CGFloat(g) / 255, blue: CGFloat(b) / 255, alpha: CGFloat(a) / 255)
    } else if hex.hasPrefix("rgb") {
      var rgbString = hex.replacingOccurrences(of: "rgb(", with: "")
      if(hex.hasPrefix("rgba")) {
        rgbString = hex.replacingOccurrences(of: "rgba(", with: "")
      } else {
        rgbString = hex.replacingOccurrences(of: "rgb(", with: "")
      }
      rgbString = rgbString.replacingOccurrences(of: ")", with: "")
      let split = rgbString.split(separator: ",")
      var values: [Double] = [Double]()
      for s in split {
        let value = (s as NSString).doubleValue
        values.append(value)
      }
      let alpha = values.count == 4 ? values[3] : 1.0

      // divide values by 255 before setting their placeholder variables up...
      let red: Double = (values[0] / 255.0)
      let green: Double = (values[1] / 255.0)
      let blue: Double = (values[2] / 255.0)

      // instantiate the colour and return it (performing casts of the Doubles to CGFloats as required)
      return UIColor(red: CGFloat(red), green: CGFloat(green), blue: CGFloat(blue), alpha: alpha)
    } else {
      if let supported = SupportedColors[hex] {
        return supported
      }

    }
    return UIColor.black
  }
}
