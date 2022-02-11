//
//  HeaderCell.swift
//  qlik-trial-react-native-text-grid
//
//  Created by Vittorio Cellucci on 2022-01-19.
//

import Foundation
class HeaderCell: PaddedLabel {
  var dataColumn: DataColumn?
  var onHeaderPressed: RCTDirectEventBlock?

  init(frame: CGRect, dataColumn: DataColumn) {
    super.init(frame: frame)
    self.dataColumn = dataColumn
    makePressable()
  }

  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  fileprivate func makePressable() {
    isUserInteractionEnabled = true
    let tapGesture = UITapGestureRecognizer(target: self, action: #selector(onPressedHeader(_:)))
    addGestureRecognizer(tapGesture)
  }

  @objc func onPressedHeader(_ sender: UITapGestureRecognizer) {
    setNeedsDisplay()
    if sender.state == .ended, let dataColumn = dataColumn, let onHeaderPressed = onHeaderPressed {
      self.layer.backgroundColor = UIColor.systemGray.cgColor
      setNeedsDisplay()
      do {
        let jsonEncoder = JSONEncoder()
        let jsonData = try jsonEncoder.encode(dataColumn)
        let column = String(data: jsonData, encoding: String.Encoding.utf8)
        onHeaderPressed(["column": column ?? ""])
      } catch {
        print(error)
      }
    }
  }
}
