//
//  Global.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-08-29.
//

import Foundation
class Global {
  static func getColumnWidthIdx() -> Int {
    return UIDevice.current.orientation.isPortrait ? 0 : 1
  }
}
