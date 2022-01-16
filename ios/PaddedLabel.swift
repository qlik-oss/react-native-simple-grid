//
//  PaddedLabel.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-16.
//

import Foundation
class PaddedLabel : UILabel {
  var column = 0;
  var cell: DataCell?
  let UIEI = UIEdgeInsets(top: 0, left: 8, bottom: 0, right: 8) // as desired
  let selectedBackgroundColor = ColorParser().fromCSS(cssString: "#009845")
  var selected = false

  override var intrinsicContentSize:CGSize {
      numberOfLines = 0       // don't forget!
      var s = super.intrinsicContentSize
      s.height = s.height + UIEI.top + UIEI.bottom
      s.width = s.width + UIEI.left + UIEI.right
      return s
  }

  override func drawText(in rect:CGRect) {
      let r = rect.inset(by: UIEI)
      super.drawText(in: r)
  }

  override func textRect(forBounds bounds:CGRect,
                             limitedToNumberOfLines n:Int) -> CGRect {
      let b = bounds
      let tr = b.inset(by: UIEI)
      let ctr = super.textRect(forBounds: tr, limitedToNumberOfLines: 0)
      // that line of code MUST be LAST in this function, NOT first
      return ctr
  }
  
  func makeSelectable() {
    isUserInteractionEnabled = true
    let tapGesture = UITapGestureRecognizer(target: self, action: #selector(labelClicked(_:)))
    addGestureRecognizer(tapGesture)
    
    NotificationCenter.default.addObserver(self, selector: #selector(toggleSelected(_:)), name: Notification.Name.CellSelectedToggle, object: nil)
    
    NotificationCenter.default.addObserver(self, selector: #selector(clearSelected(_:)), name: Notification.Name.CellSelectedClear, object: nil)
  }
  
  @objc func labelClicked(_ sender: UITapGestureRecognizer) {
    if sender.state == .ended {
      NotificationCenter.default.post(name: Notification.Name.CellSelectedToggle, object:buildSelectionSignature())
    }
  }
  
  fileprivate func buildSelectionSignature() -> String {
    return String(format: "%d.%d", Int(cell?.qElemNumber ?? -1), Int(cell?.colIdx ?? -1))
  }
  
  @objc func clearSelected(_ notification: Notification) {
    selected = false
    updateBackground()
  }
  
  @objc func toggleSelected(_ notification: Notification) {
    if let data = notification.object as? String {
      let sig = buildSelectionSignature()
      if sig == data {
        selected = !selected
        updateBackground()
      }
    }
  }
  
  func checkSelected(_ selectionsEngine: SelectionsEngine) {
    let sig = buildSelectionSignature()
    selected = selectionsEngine.selections.contains(sig)
    updateBackground()
  }
  
  fileprivate func updateBackground() {
    backgroundColor = selected ? selectedBackgroundColor     : .clear
    textColor = selected ? .white : .black
  }
  
}
