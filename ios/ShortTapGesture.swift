//
//  ShortTapGesture.swift
//  qlik-trial-react-native-text-grid
//
//  Created by Vittorio Cellucci on 2022-01-23.
//

import Foundation
class ShortTapGesture: UITapGestureRecognizer {
  var maximumTapLength: Double = 0.25

  override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent) {
    super.touchesBegan(touches, with: event)
    delay(delay: maximumTapLength) {
      // Enough time has passed and the gesture was not recognized -> It has failed.
      if  self.state != .ended {
        self.state = .failed
      }
    }
  }

  func delay(delay: Double, closure:@escaping () -> Void) {
    DispatchQueue.main.asyncAfter(deadline: .now() + delay, execute: closure)
  }
}
