//
//  SVGCoderTests.swift
//  TableAppTests
//
//  Created by Vittorio Cellucci on 2023-02-15.
//

import XCTest
import react_native_simple_grid

final class SVGCoderTests: XCTestCase {

    override func setUpWithError() throws {
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }

    override func tearDownWithError() throws {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }

    func testExample() throws {
      let dataString = "data:image/svg+xml,<svg xmlns=\"http://www.w3.org/2000/svg\" height=\"10\" width=\"100\"><rect x=\"0\" y=\"0\" width=\"13.83674\" height=\"8\" style=\"fill:rgb(157,194,60)\" /></svg>"
      guard let index = dataString.firstIndex(of: ",") else { return }
      let start = dataString.index(index, offsetBy: 1)
      let xmlString = dataString[start...]
      guard let data = xmlString.data(using: .utf8) else { return }
      let xmlParser = XMLParser(data: data)
      let parser = SvgParser()
      xmlParser.delegate = parser
      xmlParser.parse()
      XCTAssertTrue(parser.didStart)
      XCTAssertTrue(parser.didEnd)
      XCTAssertEqual(1, parser.shapes.count)
      
    }

}
