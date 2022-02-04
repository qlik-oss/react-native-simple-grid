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

class SelectionsEngine: NSObject {
  var selections = [String: String]()
  var onSelectionsChanged: RCTDirectEventBlock?

  override init() {
    super.init()
    NotificationCenter.default.addObserver(self, selector: #selector(toggleSelected(_:)), name: Notification.Name.CellSelectedToggle, object: nil)
  }

  @objc func toggleSelected(_ notification: Notification) {
    if let data = notification.object as? String {
      let components = SelectionsEngine.splitSignature(from: data)
      if selections[components[0]] != nil {
        selections.removeValue(forKey: components[0])
      } else {
        selections[components[0]] = components[1]
      }
    }

    if let onSelectionsChanged = onSelectionsChanged {
      var event = [String]()
      for (key, value) in selections {
        let sel = key + value
        event.append(sel)
      }
      onSelectionsChanged(["selections": event])
    }
  }

  func contains( _ cell: DataCell) -> Bool {
    let key = SelectionsEngine.signatureKey(from: cell)
    return selections[key] != nil
  }

  func clear() {
    selections = [String: String]()
    NotificationCenter.default.post(name: Notification.Name.CellSelectedClear, object: nil)
  }

  static func buildSelectionSignator(from: DataCell) -> String {
    return String(format: "/%d/%d/%d", Int(from.qElemNumber ?? -1), Int(from.colIdx ?? -1), Int(from.rowIdx ?? -1))
  }

  static func splitSignature(from: String) -> [String] {
    let components = from.components(separatedBy: "/")
    let key = "/" + components[1] + "/" + components[2]
    let value = "/" + components[3]
    return [key, value]
  }

  static func signatureKey(from: String) -> String {
    if let index = from.lastIndex(of: "/") {
      let sub = from[..<index]
      return String(sub)
    }
    return ""
  }

  static func signatureKey(from: DataCell) -> String {
    return String(format: "/%d/%d", Int(from.qElemNumber ?? -1), Int(from.colIdx ?? -1))
  }
}
