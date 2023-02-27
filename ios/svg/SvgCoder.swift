//
//  SvgCoder.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2023-02-15.
//

import Foundation
import SDWebImage

public class SvgCoder {
  let dataString: String;
  var data: Data?
  var size = CGSize.zero
  var shapes = [SvgShape]()
  var viewPortsSize = CGSize.zero
  var padding = 6.0
  
  public init(_ data: String, with size: CGSize) {
    self.size = size
    self.dataString = data;
    guard let index = dataString.firstIndex(of: ",") else { return }
    let start = dataString.index(index, offsetBy: 1)
    let xmlString = dataString[start...]
    self.data = xmlString.data(using: .utf8)
    parse()
   
  }
  
  private func parse()  {
    guard let data = data else { return }
    let xmlParser = XMLParser(data: data)
    let svgParser = SvgParser()
    xmlParser.delegate = svgParser
    xmlParser.parse()
    shapes = svgParser.shapes
    viewPortsSize = svgParser.viewPortSize
  }
  
}
