//
//  PaddedLabel.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-16.
//

import Foundation
class PaddedLabel : UILabel {
  let UIEI = UIEdgeInsets(top: 0, left: 8, bottom: 0, right: 8) // as desired

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
}
