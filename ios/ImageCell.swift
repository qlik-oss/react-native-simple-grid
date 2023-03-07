//
//  ImageCell.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-08-08.
//

import Foundation
import SDWebImage

class ImageCell: UIView, ConstraintCellProtocol, SelectionsListener {
  var id: Int = 0
  var dynamicWidth = NSLayoutConstraint()
  let contextMenu = ContextMenu()
  weak var imageView: UIImageView?
  weak var delegate: ExpandedCellProtocol?
  var representation: Representation?
  var menuTranslations: MenuTranslations?
  var cell: DataCell?
  var selectionsEngine: SelectionsEngine?
  var selectionBand: SelectionBand?
  var selected = false
  let selectedBackgroundColor = ColorParser.fromCSS(cssString: "#009845")
  var prevBackgroundColor = UIColor.clear
  var svgCoder: SvgCoder?

  init( selectionBand: SelectionBand?) {
    self.selectionBand = selectionBand
    super.init(frame: CGRect.zero)
    if let selectionBand = self.selectionBand {
      selectionBand.notificationCenter.addObserver(self, selector: #selector(onTappedSelectionBand), name: Notification.Name.onTappedSelectionBand, object: nil)
      selectionBand.notificationCenter.addObserver(self, selector: #selector(onSelectionDragged), name: Notification.Name.onSelectionDragged, object: nil)
    }

    showMenus()
  }

  @objc func onTappedSelectionBand(notificaiton: Notification) {
    guard let point = notificaiton.object as? CGPoint else {return}
    guard let selectionBand = self.selectionBand else { return }
    let hitTestPoint = selectionBand.convert(point, to: self)
    if self.frame.contains(hitTestPoint) {
      toggleSelection()
      if !selected {

        selectionBand.notificationCenter.post(name: Notification.Name.onClearSelectionBand, object: nil)

      }
    }
  }

  @objc func onSelectionDragged(notificaiton: Notification) {
    if !selected {
      guard let envelope = notificaiton.object as? SelectionBandEnvelope else {return}
      guard let selectionBand = self.selectionBand else { return }
      if envelope.sender === selectionBand {
        let convertedFrame = convertLocalFrameToSelectionBandFrame(selectionBand)
        if envelope.frame.contains(convertedFrame) {
          addToSelections()
        }
      }
    }
  }

  fileprivate func addToSelections() {
    guard let selectionsEngine = self.selectionsEngine else { return }
    let sig = SelectionsEngine.buildSelectionSignator(from: cell!)
    selectionsEngine.addSelection(sig)
    selected = true
    updateBackground()
  }

  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override var canBecomeFirstResponder: Bool {
    return true
  }

  func getDynamicWidth() -> NSLayoutConstraint {
    return dynamicWidth
  }

  func setDynamicWidth(_ newVal: NSLayoutConstraint, value: Double) {
    dynamicWidth = newVal
  }

  func getLineCount(columnWidth: Double) -> Int {
    return 1
  }

  func showMenus() {
    isUserInteractionEnabled = true
    let longPress = UILongPressGestureRecognizer(target: self, action: #selector(showMenu))
    self.addGestureRecognizer(longPress)
  }

  @objc func showMenu(_ sender: UILongPressGestureRecognizer) {
    self.becomeFirstResponder()
    contextMenu.menuTranslations = self.menuTranslations
    contextMenu.cell = self.cell
    contextMenu.showMenu(sender, view: self)
  }

  @objc func handleCopy(_ controller: UIMenuController) {

    let format = UIGraphicsImageRendererFormat()
    format.scale = UIScreen.main.scale
    if let img = imageView?.image {
      let board = UIPasteboard.general
      board.image = img
    }
    controller.setMenuVisible(false, animated: true)
    self.resignFirstResponder()
  }

  @objc func handleExpand(_ controller: UIMenuController) {
    guard let cell = self.cell else { return }
    guard let delegate = self.delegate else { return }
    delegate.onExpandedCell(cell: cell)
    self.resignFirstResponder()
  }

  func setData(data: DataCell, representedAs rep: Representation, index: Int?) {
    self.representation = rep
    self.cell = data

    guard let urlString = getUrlString(data: data, representedAs: rep, index: index) else { return }
    if urlString.starts(with: "data:image/svg+xml,<svg") {
      svgCoder = SvgCoder(urlString, with: self.frame.size)
    } else {
      guard let url = URL(string: urlString) else {return}
      if let downloadedImage = SDImageCache.shared.imageFromCache(forKey: urlString) {
        self.displayImage(with: downloadedImage)
      } else {
        SDWebImageDownloader.shared.downloadImage(with: url) {(image, _, _, _) in
          if let image = image {
            DispatchQueue.main.async {
              self.displayImage(with: image)
            }
          }
        }
      }
    }
  }

  func getUrlString(data: DataCell, representedAs rep: Representation, index: Int?) -> String? {
    var urlString = ""
    if rep.imageSetting == "label" {
      urlString = data.qText ?? ""
      return urlString
    }

    guard let qAttrExps = data.qAttrExps else {return nil}
    guard let qValues = qAttrExps.qValues else {return nil}
    guard let attrIndex = index else { return nil }

    if qValues.count > 0 {
      guard let urlString = qValues[attrIndex].qText else {return nil}
      return urlString
    }
    return nil
  }

  override func layoutSubviews() {
    super.layoutSubviews()
  }

  func displayImage(with image: UIImage) {
    if self.imageView == nil {
      let imageView = UIImageView()
      imageView.image = image
      addSubview(imageView)
      setupConstraints(imageView: imageView)
      self.imageView = imageView
      displayImage()
    }
  }

  fileprivate func fitToHeight(_ imageView: UIImageView, _ rep: Representation) {
    if let image = imageView.image {
      let aspectRatio = image.size.width/image.size.height
      let height = self.frame.height
      let width = height * aspectRatio

      if rep.imagePosition == "centerCenter" {
        let constraints = [
          imageView.centerXAnchor.constraint(equalTo: self.centerXAnchor),
          imageView.centerYAnchor.constraint(equalTo: self.centerYAnchor),
          imageView.heightAnchor.constraint(equalTo: self.heightAnchor),
          imageView.widthAnchor.constraint(equalTo: imageView.heightAnchor, multiplier: aspectRatio)
        ]
        NSLayoutConstraint.activate(constraints)
        addConstraints(constraints)

      } else {
        let leadingAnchor = rep.imagePosition == "topCenter" ?
        imageView.leadingAnchor.constraint(equalTo: self.leadingAnchor) :
        imageView.trailingAnchor.constraint(equalTo: self.trailingAnchor)

        let constraints = [
          leadingAnchor,
          imageView.heightAnchor.constraint(equalToConstant: height),
          imageView.widthAnchor.constraint(equalToConstant: width)
        ]
        NSLayoutConstraint.activate(constraints)
        addConstraints(constraints)
      }
    }
  }

  fileprivate func alwaysFit(_ rep: Representation, _ imageView: UIImageView) {
    if rep.imagePosition == "centerCenter" {
      imageView.fitToView(self)
    } else  if let image = imageView.image {
      let aspectRatio = (image.size.height/image.size.width)
      let maxWidth = self.frame.height / aspectRatio
      let width = imageView.widthAnchor.constraint(lessThanOrEqualToConstant: maxWidth)
      let leading = rep.imagePosition == "topCenter" ? imageView.leadingAnchor.constraint(equalTo: self.leadingAnchor) :
      imageView.leadingAnchor.constraint(greaterThanOrEqualTo: self.trailingAnchor, constant: -maxWidth)

      var constraints = [
        leading,
        imageView.trailingAnchor.constraint(equalTo: self.trailingAnchor),
        imageView.topAnchor.constraint(equalTo: self.topAnchor),
        imageView.bottomAnchor.constraint(equalTo: self.bottomAnchor),
        width
      ]
      // need to bind to the leading of imageView and keep trailing lower priority
      if rep.imagePosition == "bottomCenter" {
        let hardLeading = imageView.leadingAnchor.constraint(greaterThanOrEqualTo: self.leadingAnchor)
        leading.priority = UILayoutPriority(999)
        constraints.append(hardLeading)
      }

      NSLayoutConstraint.activate(constraints)
      addConstraints(constraints)

    }
  }

  fileprivate func fitToWidth(_ rep: Representation, _ imageView: UIImageView) {
    guard let image = imageView.image else { return }
    let aspectRatio = (image.size.height/image.size.width)
    var constraints = [
      imageView.centerXAnchor.constraint(equalTo: self.centerXAnchor),
      imageView.centerYAnchor.constraint(equalTo: self.centerYAnchor),
      imageView.widthAnchor.constraint(equalTo: self.widthAnchor),
      imageView.heightAnchor.constraint(equalTo: imageView.widthAnchor, multiplier: aspectRatio)
    ]
    if rep.imagePosition == "centerLeft" {
      constraints = [
        imageView.topAnchor.constraint(equalTo: self.topAnchor),
        imageView.centerXAnchor.constraint(equalTo: self.centerXAnchor),
        imageView.widthAnchor.constraint(equalTo: self.widthAnchor),
        imageView.heightAnchor.constraint(equalTo: imageView.widthAnchor, multiplier: aspectRatio)
      ]
    } else if rep.imagePosition == "centerRight" {
      constraints = [
        imageView.bottomAnchor.constraint(equalTo: self.bottomAnchor),
        imageView.centerXAnchor.constraint(equalTo: self.centerXAnchor),
        imageView.widthAnchor.constraint(equalTo: self.widthAnchor),
        imageView.heightAnchor.constraint(equalTo: imageView.widthAnchor, multiplier: aspectRatio)
      ]
    }
    NSLayoutConstraint.activate(constraints)
    addConstraints(constraints)
  }

  func setupConstraints(imageView: UIImageView) {
    imageView.translatesAutoresizingMaskIntoConstraints = false
    guard let rep = self.representation else { return }
    if rep.imageSize == "fitHeight" {
      fitToHeight(imageView, rep)
    } else if rep.imageSize == "alwaysFit" {
      alwaysFit(rep, imageView)
    } else if rep.imageSize == "fitWidth" {
      fitToWidth(rep, imageView)
    } else {
      imageView.fitToView(self)
    }
  }

  func displayImage() {
    guard let rep = representation else { return }
    guard let imageView = self.imageView else {return}
    if rep.imageSize == "fitHeight" || rep.imageSize == "alwaysFit" {
      imageView.contentMode = .scaleAspectFit
    } else if rep.imageSize == "fitWidth" {
      imageView.contentMode = .scaleAspectFill
      if rep.imagePosition == "centerRight" {
        imageView.contentMode = .scaleAspectFit
      } else if rep.imagePosition == "centerLeft" {
        imageView.contentMode = .scaleAspectFit
      }
    }
    imageView.clipsToBounds = true
    self.clipsToBounds = true
    self.setNeedsLayout()
    imageView.setNeedsLayout()
  }

  func makeSelectable(selectionsEngine: SelectionsEngine) {
    isUserInteractionEnabled = true
    self.selectionsEngine = selectionsEngine
    let tapGesture = UITapGestureRecognizer(target: self, action: #selector(imageCliked(_:)))

    addGestureRecognizer(tapGesture)
    selectionsEngine.addListener(listener: self)

  }

  @objc func imageCliked(_ sender: UITapGestureRecognizer) {
    let menu = UIMenuController.shared
    if menu.isMenuVisible {
      menu.setMenuVisible(false, animated: true)
    }

    guard let selectionsEngine = self.selectionsEngine else {return}
    guard let selectionBand = self.selectionBand else { return }
    if selectionsEngine.canSelect(self.cell!) {
      if !selected {
        let convertedFrame = convertLocalFrameToSelectionBandFrame(selectionBand)
        let envelope = SelectionBandEnvelope(convertedFrame, sender: self, colIdx: self.cell?.colIdx ?? -1.0)
        selectionBand.handleActivation(envelope)
        selectionBand.notificationCenter.post(name: Notification.Name.onTappedSelection, object: envelope)
      }
      toggleSelection()
    }
  }

  fileprivate func toggleSelection() {
    let sig = SelectionsEngine.buildSelectionSignator(from: cell!)
    if let selectionsEngine = selectionsEngine {
      selectionsEngine.toggleSelected(sig)
    }
  }

  fileprivate func convertLocalFrameToSelectionBandFrame(_ selectionBand: UIView) -> CGRect {
    let convertedFrame = convert(self.frame, from: self.superview)
    // account for the 1 width colunm grabber line
    return convert(convertedFrame, to: selectionBand).insetBy(dx: 0.5, dy: 0).offsetBy(dx: -0.5, dy: 0)
  }

  func toggleSelected(data: String) {
    let sig = SelectionsEngine.signatureKey(from: data)
    let comp = SelectionsEngine.signatureKey(from: cell!)
    if sig == comp {
      selected = !selected
      updateBackground()
    }
  }

  func clearSelected() {
    selected = false
    updateBackground()
  }

  func addedToSelection(data: String) {
    let sig = SelectionsEngine.signatureKey(from: data)
    let comp = SelectionsEngine.signatureKey(from: cell!)
    if sig == comp {
      selected = true
      updateBackground()
    }
  }

  fileprivate func updateBackground() {
    animateBackgroundColor(to: selected ? selectedBackgroundColor : prevBackgroundColor)
  }

  fileprivate func animateBackgroundColor(to: UIColor) {
    UIView.animate(withDuration: 0.3, animations: {
      self.layer.backgroundColor = to.cgColor
    })
  }

  override func draw(_ rect: CGRect) {
    if let svgCoder = svgCoder {
      guard let context = UIGraphicsGetCurrentContext() else { return }
      let clipRect = rect.insetBy(dx: svgCoder.padding, dy: 0)
      context.clip(to: [clipRect])
      svgCoder.shapes.forEach { shape in
        shape.draw(context, rect: rect)
      }
    }
  }

  deinit {
    if let selectionBand = self.selectionBand {
      selectionBand.notificationCenter.removeObserver(self.onTappedSelectionBand)
    }
  }
}
