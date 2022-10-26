//
//  HeaderCell.swift
//  qlik-trial-react-native-text-grid
//
//  Created by Vittorio Cellucci on 2022-01-19.
//

import Foundation
class HeaderCell: UIView {
  var dataColumn: DataColumn?
  var onHeaderPressed: RCTDirectEventBlock?
  var onSearchColumn: RCTDirectEventBlock?
  let sortBorderWidth = 3.0
  let sortBorderColor = UIColor.black
  let buttonSize = CGSize(width: 20, height: 40)
  let grabberHalfSize = 20.0
  weak var currentSortBorder: UIView?
  weak var searchButton: UIButton?
  weak var paddedLabel: PaddedLabel?

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
    if let dataColumn = self.dataColumn {
      if dataColumn.isDim {
        createSearchButton()
        let label = PaddedLabel(frame: CGRect(x: 0, y: 0, width: frame.width - buttonSize.width, height: frame.height), selectionBand: nil)
        addSubview(label)
        paddedLabel = label
      } else {
        let label = PaddedLabel(frame: CGRect(x: 0, y: 0, width: frame.width, height: frame.height), selectionBand: nil)
        label.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        addSubview(label)
        paddedLabel = label
      }
    }
  }

  fileprivate func createSearchButton() {
    let button = UIButton(frame: CGRect(origin: CGPoint(x: self.frame.width - buttonSize.width - grabberHalfSize, y: 0), size: buttonSize))
    if #available(iOS 13.0, *) {
      guard let image = UIImage(systemName: "magnifyingglass") else { return }
      button.setImage(image.withColor(UIColor(red: 0.25, green: 0.25, blue: 0.25, alpha: 1.00)), for: .normal)
      button.addTarget(self, action: #selector(didSearch), for: .touchUpInside)
    } else {
      // Fallback on earlier versions
    }
    addSubview(button)
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

  func setText(_ label: String, textColor: UIColor, align: NSTextAlignment) {
    guard let paddedLabel = self.paddedLabel else { return }

    paddedLabel.text = label
    paddedLabel.textAlignment = align
    paddedLabel.textColor = textColor

    let sizedFont = UIFont.systemFont(ofSize: 14)
    paddedLabel.font = UIFontMetrics(forTextStyle: .headline).scaledFont(for: sizedFont)
    paddedLabel.adjustsFontForContentSizeCategory = true
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    guard let paddedLabel = self.paddedLabel else { return }
    if let searchButton = self.searchButton {
      searchButton.frame = CGRect(origin: CGPoint(x: self.frame.width - buttonSize.width - grabberHalfSize, y: 0), size: buttonSize)
      paddedLabel.frame = CGRect(origin: CGPoint.zero, size: CGSize(width: self.frame.width - buttonSize.width - grabberHalfSize, height: self.frame.height))
    }
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

}
