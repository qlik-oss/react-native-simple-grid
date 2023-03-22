//
//  SvgParser.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2023-02-15.
//

import Foundation

public class SvgShape {
  public var fill = UIColor.blue.cgColor
  public var viewPortSize = CGSize.zero
  public var padding: CGFloat = 6.0

  func draw(_ context: CGContext, rect: CGRect, paddingMultiplier: CGFloat) {}
}

public class SvgParser: NSObject, XMLParserDelegate {

  public var didStart = false
  public var didEnd = false
  public var shapes = [SvgShape]()
  public var viewPortSize = CGSize.zero

  public override init() {

  }

  public func parserDidStartDocument(_ parser: XMLParser) {
    didStart = true
  }

  public func parserDidEndDocument(_ parser: XMLParser) {
    didEnd = true
  }

  public func parseStyle(_ style: String) -> [String: String] {
    let styles = style.components(separatedBy: ";")
    var dict: [String: String] = [:]
    styles.forEach { s in
      // fill:rgb(0, 0, 0)
      let component = s.components(separatedBy: ":")
      if component.count == 2 {
        let key = component[0].trimmingCharacters(in: .whitespacesAndNewlines)
        let val = component[1].trimmingCharacters(in: .whitespacesAndNewlines)
        dict[key] = val
      }
    }
    return dict
  }

  public func parser(_ parser: XMLParser, didStartElement elementName: String, namespaceURI: String?, qualifiedName qName: String?, attributes attributeDict: [String: String] = [:]) {
    if elementName == "svg" {
      let width = Float(attributeDict["width"] ?? "100")
      let height = Float(attributeDict["height"] ?? "10")
      viewPortSize = CGSize(width: CGFloat(width ?? 100.0), height: CGFloat(height ?? 10.0))
    } else if elementName == "rect" {
      let x = Float(attributeDict["x"] ?? "0")
      let y = Float(attributeDict["y"] ?? "0")
      let width = Float(attributeDict["width"] ?? "0")
      let height = Float(attributeDict["height"] ?? "0")
      let style = parseStyle(attributeDict["style"] ?? "fill:rgb(0, 0, 0)")
      let rect = SvgRect()
      rect.position = CGPoint(x: CGFloat(x ?? 0.0), y: CGFloat(y ?? 0.0))
      rect.size = CGSize(width: CGFloat(width ?? 0), height: CGFloat(height ?? 0))
      rect.viewPortSize = viewPortSize
      if let fill = style["fill"] {
        rect.fill = ColorParser.fromCSS(cssString: fill).cgColor
      }

      shapes.append(rect)
    }
  }
}
