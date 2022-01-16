//
//  SelectionsEngine.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-16.
//

import Foundation
extension Notification.Name {
  static var CellSelectedToggle = Notification.Name("CellSelectedToggle")
  static var CellSelectedClear = Notification.Name("CellSelectedClear")
}

class SelectionsEngine : NSObject {
  var selections: Set = [""]
  
  override init() {
    super.init()
    NotificationCenter.default.addObserver(self, selector: #selector(toggleSelected(_:)), name: Notification.Name.CellSelectedToggle, object: nil)
  }
  
  @objc func toggleSelected(_ notification: Notification) {
    dump(notification)
    if let data = notification.object as? String {
      if (selections.contains(data)) {
        selections.remove(data)
      } else {
        selections.insert(data)
      }
    }
  }
  
  func clear() {
    selections = [""]
    NotificationCenter.default.post(name: Notification.Name.CellSelectedClear, object: nil)
  }
}
