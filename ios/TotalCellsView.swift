//
//  TotalCellsView.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-07-29.
//

import Foundation
class TotalCellsView : UIView {
  weak var textView: UITextView?
  var totalRows = 0
  override init(frame: CGRect) {
    super.init(frame: frame)
    self.backgroundColor = .lightGray
    
  }
  
  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }
  
  func addBorder() {
    let topBorder = UIView(frame: CGRect(x: self.bounds.origin.x, y: self.bounds.origin.y, width: self.bounds.width, height: 1))
    topBorder.backgroundColor = .lightGray
    addSubview(topBorder)
    
    let bottomBorder = UIView(frame: CGRect(x: self.bounds.origin.x, y: self.bounds.origin.y + self.bounds.height, width: self.bounds.width, height: 1))
    bottomBorder.backgroundColor = .lightGray
    addSubview(bottomBorder)
  }
  
  func createTextView() {
    if let textView = textView {
      textView.removeFromSuperview()
    }
    let view = UITextView(frame: self.bounds)
    view.text = "NA"
    view.textAlignment = .right
    addSubview(view)
    self.textView = view;
  }
  
  func updateTotals(first:IndexPath, last: IndexPath) {
    if let textView = textView {
      if let f = first.last, let l = last.last {
        textView.text = "\(f) - \(l) of \(totalRows)"
      }
    }
  }
}
