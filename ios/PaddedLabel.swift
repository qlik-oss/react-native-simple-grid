//
//  PaddedLabel.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-16.
//

import Foundation
class PaddedLabel: UILabel, SelectionsListener {
  var id: Int = 0

  var column = 0
  var cell: DataCell?
  var hasSystemImage = false
  static let PaddingSize = 8
  let UIEI = UIEdgeInsets(top: 0, left: CGFloat(PaddingSize), bottom: 0, right: CGFloat(PaddingSize)) // as desired
  let selectedBackgroundColor = ColorParser().fromCSS(cssString: "#009845")
  var selected = false
  var selectionsEngine: SelectionsEngine?
  
  override init(frame: CGRect) {
    super.init(frame: frame.integral)
  }
  
  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }
  
  override var intrinsicContentSize: CGSize {
    numberOfLines = 0       // don't forget!
    var s = super.intrinsicContentSize
    s.height = s.height + UIEI.top + UIEI.bottom
    s.width = s.width + UIEI.left + UIEI.right
    return s
  }

  override func drawText(in rect: CGRect) {
    let r = rect.inset(by: UIEI)
    super.drawText(in: r)
  }

  override func textRect(forBounds bounds: CGRect,
                         limitedToNumberOfLines n: Int) -> CGRect {
    let b = bounds
    let tr = b.inset(by: UIEI)
    let ctr = super.textRect(forBounds: tr, limitedToNumberOfLines: 0)
    // that line of code MUST be LAST in this function, NOT first
    return ctr
  }

  func makeSelectable(selectionsEngine: SelectionsEngine) {
    isUserInteractionEnabled = true
    self.selectionsEngine = selectionsEngine
    let tapGesture = UITapGestureRecognizer(target: self, action: #selector(labelClicked(_:)))

    addGestureRecognizer(tapGesture)
    selectionsEngine.addListener(listener: self)
  }

  @objc func labelClicked(_ sender: UITapGestureRecognizer) {
    if sender.state == .ended {
      let sig = SelectionsEngine.buildSelectionSignator(from: cell!)
      if let selectionsEngine = selectionsEngine {
        selectionsEngine.toggleSelected(sig)
      }
    }
  }

  func clearSelected() {
    selected = false
    updateBackground()
  }

  func toggleSelected(data: String) {
    let sig = SelectionsEngine.signatureKey(from: data)
    let comp = SelectionsEngine.signatureKey(from: cell!)
    if sig == comp {
      selected = !selected
      updateBackground()
    }
  }

  func checkSelected(_ selectionsEngine: SelectionsEngine) {
    selected = selectionsEngine.contains(cell!)
    updateBackground()
  }

  fileprivate func updateBackground() {
    backgroundColor = selected ? selectedBackgroundColor     : .clear
    textColor = selected ? .white : .black
  }

  func addSystemImage(imageName: String, afterLabel bolAfterLabel: Bool = false) {
    if #available(iOS 13.0, *) {
      let config = UIImage.SymbolConfiguration(pointSize: 10)
      let imageAttachment = NSTextAttachment()
      let image = UIImage(systemName: imageName, withConfiguration: config)
      imageAttachment.image = image
      imageAttachment.bounds = CGRect(x: 0, y: 0, width: imageAttachment.image!.size.width, height: imageAttachment.image!.size.height)
      let attachmentString = NSAttributedString(attachment: imageAttachment)
      let completeText = NSMutableAttributedString(string: "")

      completeText.append(attachmentString)
      if !hasSystemImage {
        completeText.append(NSAttributedString(string: " "))
        hasSystemImage = true
      }
      let tempText = self.text
      if var tempText = tempText {
        tempText = tempText.trimmingCharacters(in: .whitespaces)
        let textAfterIcon = NSAttributedString(string: tempText)
        completeText.append(textAfterIcon)
      } else {
        let textAfterIcon = NSAttributedString(string: tempText ?? "")
        completeText.append(textAfterIcon)
      }
      self.attributedText = completeText
    } else {
      // no icon :(
    }
  }

  func removeSystemImage() {
    let text = self.text?.trimmingCharacters(in: .whitespaces)
    self.attributedText = nil
    self.text = text
    self.hasSystemImage = false
  }
}
