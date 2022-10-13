//
//  SelectionsEngine.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-16.
//

import Foundation

class SelectionsEngine: NSObject {
  var selections = [String: String]()
  var pendingSelections = Set<String>()
  var onSelectionsChanged: RCTDirectEventBlock?
  var onConfirmSelections: RCTDirectEventBlock?
  var selectionListners = [SelectionsListener]()
  var activeColumn = -1.0
  
  override init() {
    super.init()
    NotificationCenter.default.addObserver(self, selector: #selector(onDragSelectDone), name: Notification.Name.onDragSelectDone, object: nil)
  }

  func addListener(listener: SelectionsListener) {
    selectionListners.append(listener)
  }
  
  func canSelect(_ dataCell: DataCell) -> Bool {
    guard let colIdx = dataCell.colIdx else { return false }
    if(activeColumn == -1.0) {
      activeColumn = colIdx
      return true
    }
    if(activeColumn != colIdx) {
      activeColumn = colIdx
      NotificationCenter.default.post(name: Notification.Name.onClearSelectionBand, object: nil)
      if let onConfirmSelections = self.onConfirmSelections {
        onConfirmSelections([:]);
      }
      return false;
    }
    return true;
  }
  
  func addSelection(_ data: String) {
    pendingSelections.insert(data)
    for listner in selectionListners {
      listner.addedToSelection(data: data)
    }
  }
  
  @objc func onDragSelectDone(notification: Notification) {
    flushPendingSelections()
  }
  
  func flushPendingSelections() {
    for data in pendingSelections {
      let components = SelectionsEngine.splitSignature(from: data)
      selections[components[0]] = components[1]
    }
    
    pendingSelections.removeAll()
    
    if let onSelectionsChanged = onSelectionsChanged {
      var event = [String]()
      for (key, value) in selections {
        let sel = key + value
        event.append(sel)
      }
      onSelectionsChanged(["selections": event])
    }
    
  }

  func toggleSelected(_ data: String) {
    let components = SelectionsEngine.splitSignature(from: data)
    if selections[components[0]] != nil {
      selections.removeValue(forKey: components[0])
    } else {
      selections[components[0]] = components[1]
    }

    for listner in selectionListners {
      listner.toggleSelected(data: data)
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
    for listner in selectionListners {
      listner.clearSelected()
    }
    activeColumn = -1.0
    NotificationCenter.default.post(name: Notification.Name.onClearSelectionBand, object: nil)
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
