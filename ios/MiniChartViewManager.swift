//
//  MiniChartViewManager.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-10-22.
//

import Foundation
@available(iOS 13.0, *)
@objc(MiniChartViewManager)
class MiniChartViewManager: RCTViewManager {

  override static func requiresMainQueueSetup() -> Bool {
    return true
  }

  override func view() -> ReactNativeMiniChartView {
    return ReactNativeMiniChartView()
  }

}
