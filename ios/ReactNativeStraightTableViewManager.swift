//
//  ReactNativeStraightTableView.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-14.
//

import Foundation
@objc(ReactNativeStraightTableViewManager)
class ReactnativeStraightTableViewManager: RCTViewManager {

  override static func requiresMainQueueSetup() -> Bool {
    return true
  }

  override func view() -> ContainerView {
    return ContainerView()
  }
}
