//
//  TotalCellsView.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-07-29.
//

import Foundation

class TotalCellsView: UIView {
  weak var textView: UILabel?
  var totalRows = 0
  var withShadow = true
  let borderColor = UIColor.lightGray
  init(frame: CGRect, withShadow: Bool) {
    super.init(frame: frame)
    self.withShadow = withShadow
    if withShadow {
      self.layer.shadowColor = UIColor.black.cgColor
      self.layer.shadowOpacity = 0.1
      self.layer.shadowOffset = CGSize(width: 0, height: -1)
      self.layer.shadowRadius = 2
    }
  }

  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  func addBorder() {
    if !withShadow {
      let topBorder = UIView(frame: CGRect(x: 0, y: 0, width: self.bounds.width, height: 1))
      topBorder.backgroundColor = borderColor
      topBorder.autoresizingMask = [.flexibleWidth]
      addSubview(topBorder)
    }
  }

  func createTextView() {
    if let textView = textView {
      textView.removeFromSuperview()
    }
    let view = UILabel(frame: CGRect(x: 0, y: 1, width: self.frame.width - 8, height: self.frame.height - 2))
    view.text = "NA"
    view.textAlignment = .right
    view.font = UIFont.systemFont(ofSize: 14)
    addSubview(view)

    view.autoresizingMask = [.flexibleWidth, .flexibleHeight]

    self.textView = view

    addBorder()
  }

  func updateTotals(first: IndexPath, last: IndexPath) {
    if let textView = textView {
      if let f = first.last, let l = last.last {
        textView.text = "\(f + 1) - \(l + 1) of \(totalRows)"
      }
    }
  }
}
