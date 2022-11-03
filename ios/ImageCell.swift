//
//  ImageCell.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-08-08.
//

import Foundation
class ImageCell: UIImageView, ConstraintCellProtocol {
  
  var dynamicWidth = NSLayoutConstraint()
  
  func getDynamicWidth() -> NSLayoutConstraint {
    return dynamicWidth
  }
  
  func setDynamicWidth(_ newVal: NSLayoutConstraint) {
    dynamicWidth = newVal
  }
  
  func setData(data: DataCell, representedAs rep: Representation) {
    self.backgroundColor = .clear
    guard let qAttrExps = data.qAttrExps else {return}
    guard let qValues = qAttrExps.qValues else {return}
    if qValues.count > 0 {
      guard let urlString = qValues[0].qText else {return}
      guard let url = URL(string: urlString) else {return}
      DispatchQueue.global(qos: .background).async {
        do {
          let data = try Data.init(contentsOf: url)
          DispatchQueue.main.async {
            self.image = UIImage(data: data)
            self.displayImage(rep: rep)
          }
        } catch let error {
          print(error)
        }
      }
    }
  }

  func displayImage(rep: Representation) {
    if rep.imageSize == "fitToHeight" {
      self.contentMode = .center
    } else {
      self.contentMode = .scaleToFill
    }
    self.setNeedsDisplay()
  }
}
