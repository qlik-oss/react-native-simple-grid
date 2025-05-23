//
//  HeaderCell.swift
//  qlik-trial-react-native-text-grid
//
//  Created by Vittorio Cellucci on 2022-01-19.
//

import Foundation
class HeaderCell: UIView {
  var dynamicWidth = NSLayoutConstraint()
  var dataColumn: DataColumn?
  var onHeaderPressed: RCTDirectEventBlock?
  var onSearchColumn: RCTDirectEventBlock?
  let sortBorderWidth = 3.0
  let sortBorderColor = UIColor.black
  let grabberHalfSize = TableTheme.DefaultResizerWidth / 2.0
  weak var currentSortBorder: UIView?
  weak var searchButton: UIButton?
  weak var paddedLabel: PaddedLabel?

  init(dataColumn: DataColumn, onHeaderPressed: RCTDirectEventBlock?, onSearchColumn: RCTDirectEventBlock?) {
    super.init(frame: CGRect.zero)
    self.dataColumn = dataColumn
    self.onHeaderPressed = onHeaderPressed
    self.onSearchColumn = onSearchColumn
    makePressable()
  }

  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  fileprivate func makePressable() {
    isUserInteractionEnabled = true
    let tapGesture = UITapGestureRecognizer(target: self, action: #selector(onPressedHeader(_:)))
    addGestureRecognizer(tapGesture)
    if let dataColumn = self.dataColumn {
      if dataColumn.isDim {
        createSearchButton()
      }

      let label = PaddedLabel(frame: CGRect.zero, selectionBand: nil)
      addSubview(label)
      paddedLabel = label
      label.translatesAutoresizingMaskIntoConstraints = false
      let constraints = [
        label.topAnchor.constraint(equalTo: self.topAnchor),
        label.bottomAnchor.constraint(equalTo: self.bottomAnchor),
        label.trailingAnchor.constraint(equalTo: searchButton?.leadingAnchor ?? self.trailingAnchor),
        label.leadingAnchor.constraint(equalTo: self.leadingAnchor)
      ]
      NSLayoutConstraint.activate(constraints)
      addConstraints(constraints)

    }
  }

  fileprivate func createSearchButton() {
    let button = UIButton()
    if #available(iOS 13.0, *) {
      guard let image = UIImage(systemName: "magnifyingglass") else { return }
      button.setImage(image.withColor(UIColor(red: 0.25, green: 0.25, blue: 0.25, alpha: 1.00)), for: .normal)
      button.addTarget(self, action: #selector(didSearch), for: .touchUpInside)
    } else {
      // Fallback on earlier versions
    }
    addSubview(button)

    button.translatesAutoresizingMaskIntoConstraints = false
    let constraints = [
      button.topAnchor.constraint(equalTo: self.topAnchor),
      button.bottomAnchor.constraint(equalTo: self.bottomAnchor),
      button.trailingAnchor.constraint(equalTo: self.trailingAnchor, constant: -TableTheme.DefaultResizerWidth / 2.0),
      button.widthAnchor.constraint(equalToConstant: 20)
    ]
    NSLayoutConstraint.activate(constraints)
    addConstraints(constraints)
    searchButton = button
  }

  @objc func didSearch() {
    guard let onSearchColumn = self.onSearchColumn else { return }
    do {
      let jsonEncoder = JSONEncoder()
      let jsonData = try jsonEncoder.encode(dataColumn)
      let column = String(data: jsonData, encoding: String.Encoding.utf8)
      onSearchColumn(["column": column ?? ""])
    } catch {
      print(error)
    }
  }

  func setText(_ label: String, textColor: UIColor, align: NSTextAlignment, fontSize: Double) {
    guard let paddedLabel = self.paddedLabel else { return }

    paddedLabel.text = label
    paddedLabel.textAlignment = align
    paddedLabel.textColor = textColor

    let sizedFont = UIFont.systemFont(ofSize: fontSize)
    paddedLabel.font = UIFontMetrics(forTextStyle: .headline).scaledFont(for: sizedFont)
    paddedLabel.adjustsFontForContentSizeCategory = true
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

  func setTopBorder() {
    clearBorders()
    let border = UIView()
    border.backgroundColor = sortBorderColor
    border.frame = CGRect(x: 0, y: 0, width: frame.size.width, height: sortBorderWidth)
    border.autoresizingMask = [.flexibleWidth, .flexibleBottomMargin]
    addSubview(border)
    currentSortBorder = border
  }

  func setBottomBorder() {
    clearBorders()
    let border = UIView()
    border.backgroundColor = sortBorderColor
    border.autoresizingMask = [.flexibleWidth, .flexibleTopMargin]
    border.frame = CGRect(x: 0, y: frame.size.height - sortBorderWidth, width: frame.size.width, height: sortBorderWidth)
    addSubview(border)
    currentSortBorder = border
  }

  func clearBorders() {
    guard let currentBorder = self.currentSortBorder else { return }
    currentBorder.removeFromSuperview()
  }

  func getLineCount(_ width: Double) -> Int {
    guard let paddedLabel = self.paddedLabel else { return 1 }
    var columnWidth = width
    if searchButton != nil {
      columnWidth -= 30
    }

    return paddedLabel.getLineCount(true, columnWidth: columnWidth)
  }

}
