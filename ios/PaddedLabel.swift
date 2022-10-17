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
  static let PaddingSize = CGFloat(8)
  let UIEI = UIEdgeInsets(top: 0, left: PaddingSize, bottom: 0, right: PaddingSize) // as desired
  let selectedBackgroundColor = ColorParser().fromCSS(cssString: "#009845")
  var selected = false
  var selectionsEngine: SelectionsEngine?
  var url: URL?
  weak var selectionBand: SelectionBand?
  weak var dataCollectionView: DataCollectionView?

  private static let numberFormatter = NumberFormatter()

  override init(frame: CGRect) {
    super.init(frame: frame.integral)
  }

  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func drawText(in rect: CGRect) {
    if numberOfLines != 1 {
      let numLines = Self.numberFormatter.number(from: self.text ?? "") == nil ? numberOfLines : 1
      let r = self.textRect(forBounds: rect.inset(by: UIEI), limitedToNumberOfLines: numLines)
      super.drawText(in: r)
    } else {
      let r = rect.inset(by: UIEI)
      super.drawText(in: r)
    }

  }

  override func textRect(forBounds bounds: CGRect,
                         limitedToNumberOfLines n: Int) -> CGRect {

    let ctr = super.textRect(forBounds: bounds, limitedToNumberOfLines: n)
    let xOffset = self.textAlignment == .right ?  -PaddedLabel.PaddingSize : 0
    return CGRect(x: ctr.origin.x + xOffset, y: ctr.origin.y + PaddedLabel.PaddingSize, width: ctr.size.width, height: ctr.size.height)
  }

  func makeSelectable(selectionsEngine: SelectionsEngine) {
    isUserInteractionEnabled = true
    self.selectionsEngine = selectionsEngine
    let tapGesture = UITapGestureRecognizer(target: self, action: #selector(labelClicked(_:)))

    addGestureRecognizer(tapGesture)
    selectionsEngine.addListener(listener: self)
    NotificationCenter.default.addObserver(self, selector: #selector(onTappedSelectionBand), name: Notification.Name.onTappedSelectionBand, object: nil)
    NotificationCenter.default.addObserver(self, selector: #selector(onSelectionDragged), name: Notification.Name.onSelectionDragged, object: nil)
  }

  @objc func labelClicked(_ sender: UITapGestureRecognizer) {
    if sender.state == .ended {
      if let url = url {
        UIApplication.shared.open(url)
      } else {
        guard let selectionsEngine = self.selectionsEngine else {return}
        if selectionsEngine.canSelect(self.cell!) {
          if let selectionBand = self.selectionBand {
            if !selected {
              let convertedFrame = convertLocalFrameToSelectionBandFrame(selectionBand)
              let envelope = SelectionBandEnvelope(convertedFrame, sender: self, colIdx: self.cell?.colIdx ?? -1.0)
              selectionBand.handleActivation(envelope)
              NotificationCenter.default.post(name: Notification.Name.onTappedSelection, object: envelope)
            }
          }
          toggleSelection()
        }
      }
    }
  }

  @objc func onTappedSelectionBand(notificaiton: Notification) {
    guard let point = notificaiton.object as? CGPoint else {return}
    guard let parentView = self.selectionBand else {return}
    let hitTestPoint = parentView.convert(point, to: self)
    if self.frame.contains(hitTestPoint) {
      toggleSelection()
      if !selected {
        NotificationCenter.default.post(name: Notification.Name.onClearSelectionBand, object: nil)
      }
    }
  }

  @objc func onSelectionDragged(notificaiton: Notification) {
    if !selected {
      guard let envelope = notificaiton.object as? SelectionBandEnvelope else {return}
      guard let selectionBand = self.selectionBand else {return}
      if envelope.sender === selectionBand {
        let convertedFrame = convertLocalFrameToSelectionBandFrame(selectionBand)
        if convertedFrame.intersects(envelope.frame) {
          print("\(text) -> Frame \(self.frame), converted: \(convertedFrame), enve\(envelope.frame)")
          addToSelections()
        }
      }
    }
  }

  fileprivate func convertLocalFrameToSelectionBandFrame(_ selectionBand: UIView) -> CGRect {
    let convertedFrame = convert(self.frame, from: self.superview)// convert(self.frame, to: dataCollectionView) //dataCollectionView.convert(self.frame, to: selectionBand)
    // account for the 1 width colunm grabber line
    return convert(convertedFrame, to: selectionBand).insetBy(dx: 0.5, dy: 0).offsetBy(dx: -0.5, dy: 0)// .offsetBy(dx: -dataCollectionView.frame.origin.x, dy: 0)
  }

  fileprivate func addToSelections() {
    guard let selectionsEngine = self.selectionsEngine else { return }
    let sig = SelectionsEngine.buildSelectionSignator(from: cell!)
    selectionsEngine.addSelection(sig)
    selected = true
    updateBackground()

  }

  fileprivate func toggleSelection() {
    let sig = SelectionsEngine.buildSelectionSignator(from: cell!)
    if let selectionsEngine = selectionsEngine {
      selectionsEngine.toggleSelected(sig)
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

  func addedToSelection(data: String) {
    let sig = SelectionsEngine.signatureKey(from: data)
    let comp = SelectionsEngine.signatureKey(from: cell!)
    if sig == comp {
      selected = true
      updateBackground()
    }
  }

  func checkSelected(_ selectionsEngine: SelectionsEngine) {
    selected = selectionsEngine.contains(cell!)
    updateBackground()
  }

  fileprivate func updateBackground() {
    textColor = selected ? .white : .black
    animateBackgroundColor(to: selected ? selectedBackgroundColor : .clear)
  }

  fileprivate func animateBackgroundColor(to: UIColor) {
    UIView.animate(withDuration: 0.3, animations: {
      self.layer.backgroundColor = to.cgColor
    })
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

  func checkForUrls() {
    let urls = checkForUrls(text: self.text ?? "")
    if urls.count > 0 {
      self.text = urls[0].absoluteString
      self.url = urls[0]
      self.textColor = UIColor.systemBlue
    }
  }

  fileprivate func checkForUrls(text: String) -> [URL] {
    let types: NSTextCheckingResult.CheckingType = .link

    do {
      let detector = try NSDataDetector(types: types.rawValue)

      let matches = detector.matches(in: text, options: .reportCompletion, range: NSRange(location: 0, length: text.count))

      return matches.compactMap({$0.url})
    } catch let error {
      debugPrint(error.localizedDescription)
    }

    return []
  }

  func setAttributedText(_ t: String, withIcon: UniChar, element: DataCell) {
    var iconColor = UIColor.black// self.textColor;
    var applyTextColor = false
    var showTextValues = false
    var right = false
    let iconFont = UIFont.init(name: "fontello", size: font.pointSize)
    if let indicator = element.indicator {
      if let indicatorColor = indicator.color {
        iconColor = ColorParser().fromCSS(cssString: indicatorColor.lowercased())
        applyTextColor = indicator.applySegmentColors == true
      }
      showTextValues = indicator.showTextValues == true
      right = indicator.position != "left" // because it could be nil
    }
    let textAttributes = [NSAttributedString.Key.font: self.font, NSAttributedString.Key.foregroundColor: applyTextColor ? iconColor : self.textColor ]
    let iconAttributes = [NSAttributedString.Key.font: iconFont, NSAttributedString.Key.foregroundColor: iconColor]

    if showTextValues {
      let attributedString = NSMutableAttributedString(string: t, attributes: textAttributes as [NSAttributedString.Key: Any])
      if right {
        let attributedIcon = NSMutableAttributedString(string: String(format: " %C", withIcon), attributes: iconAttributes as [NSAttributedString.Key: Any])
        attributedString.append(attributedIcon)
        self.attributedText = attributedString
      } else {
        let attributedIcon = NSMutableAttributedString(string: String(format: "%C ", withIcon), attributes: iconAttributes as [NSAttributedString.Key: Any])
        attributedIcon.append(attributedString)
        self.attributedText = attributedIcon
      }
    } else {
      let attributedString1 = NSMutableAttributedString(string: String(format: "%C", withIcon), attributes: iconAttributes as [NSAttributedString.Key: Any])
      self.attributedText = attributedString1
    }
  }

  deinit {
    NotificationCenter.default.removeObserver(self.onTappedSelectionBand)
  }
}
