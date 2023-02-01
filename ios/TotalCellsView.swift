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
  let borderColor = UIColor.lightGray.withAlphaComponent(0.2)
  init(withShadow: Bool) {
    super.init(frame: CGRect.zero)
    self.backgroundColor = .white
    createTextView()
  }

  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  func createTextView() {
    if let textView = textView {
      textView.removeFromSuperview()
    }
    let view = UILabel()
    view.text = "NA"
    view.textAlignment = .right
    view.font = UIFont.systemFont(ofSize: 14)
    addSubview(view)

    view.translatesAutoresizingMaskIntoConstraints = false
    view.makeReadble(self)

    self.textView = view

//    addBorder()
  }

  func addBorder() {

    let topBorder = UIView(frame: CGRect(x: 0, y: 0, width: self.bounds.width, height: 1))
    topBorder.backgroundColor = borderColor
    topBorder.autoresizingMask = [.flexibleWidth]
    addSubview(topBorder)
  }

  func updateTotals(first: IndexPath, last: IndexPath) {
    if let textView = textView {
      if let f = first.last, let l = last.last {
        textView.text = "\(f + 1) - \(l + 1) of \(totalRows)"
      }
    }
  }
  
  func updateZeroTotals() {
    if let textView = textView {
      textView.text = "0 - 0 of \(totalRows)"
    }
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    layer.shadowColor = UIColor.black.cgColor
    layer.shadowOffset = CGSize(width: 0, height: -0.5)
    layer.shadowOpacity = 0.1
    layer.shadowRadius = 1
    layer.masksToBounds = false
  }
}
