//
//  ImageCell.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-08-08.
//

import Foundation

class ImageCell: UIView, ConstraintCellProtocol {
  
  var dynamicWidth = NSLayoutConstraint()
  weak var imageView: UIImageView?
  var imagedata: Data?
  var representation: Representation?
  
  init() {
    super.init(frame: CGRect.zero)
    
  }
  
  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
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
  
  func setData(data: DataCell, representedAs rep: Representation) {
    self.representation = rep
    guard let qAttrExps = data.qAttrExps else {return}
    guard let qValues = qAttrExps.qValues else {return}
    if qValues.count > 0 {
      guard let urlString = qValues[0].qText else {return}
      guard let url = URL(string: urlString) else {return}
      DispatchQueue.global(qos: .background).async {
        do {
          let data = try Data.init(contentsOf: url)
          self.imagedata = data
          DispatchQueue.main.async {
            self.setNeedsLayout()
          }
        } catch let error {
          print(error)
        }
      }
    }
  }
  
  override func layoutSubviews() {
    super.layoutSubviews()
    guard let data = imagedata else { return }
    if self.imageView == nil {
      let im = UIImageView(frame: CGRect.zero)
      let image = UIImage(data: data)
      im.image = image
      addSubview(im)
      setupConstraints(imageView: im)
      self.imageView = im
      displayImage()
    } else {
      updateContentScaleFactorIfNeeded()
    }
  }
  
  func setupConstraints(imageView: UIImageView) {
    imageView.translatesAutoresizingMaskIntoConstraints = false
    guard let rep = self.representation else { return }
    if(rep.imageSize == "fitHeight" ) {
      if let image = imageView.image {
        let aspectRatio = image.size.width/image.size.height
        let height = self.frame.height
        let width = height * aspectRatio
        var leadingAnchor = rep.imagePosition == "topCenter" ?
        imageView.leadingAnchor.constraint(equalTo: self.leadingAnchor) :
        imageView.trailingAnchor.constraint(equalTo: self.trailingAnchor)
        
        if(rep.imagePosition == "centerCenter") {
          leadingAnchor = imageView.centerXAnchor.constraint(equalTo: self.centerXAnchor)
        }
        let constraints = [
          leadingAnchor,
          imageView.heightAnchor.constraint(equalToConstant: height),
          imageView.trailingAnchor.constraint(equalTo: self.trailingAnchor, constant: width)
        ]
        NSLayoutConstraint.activate(constraints)
        addConstraints(constraints)
      }
    } else if rep.imageSize == "alwaysFit" {
      if rep.imagePosition == "centerCenter" {
        imageView.fitToView(self)
      } else  if let image = imageView.image {
        let aspectRatio = image.size.width/image.size.height
        let height = self.frame.height
        let width = height * aspectRatio
        var leadingAnchor = rep.imagePosition == "topCenter" ?
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
    else {
      imageView.fitToView(self)
    }
  }
  
  
  func displayImage() {
    guard let rep = representation else { return }
    guard let imageView = self.imageView else {return}
    if(rep.imageSize == "fitHeight" || rep.imageSize == "alwaysFit") {
      imageView.contentMode = .scaleAspectFit
    } else if rep.imageSize == "fitWidth" {
      imageView.contentMode = .scaleAspectFill
      if rep.imagePosition == "centerRight" {
        imageView.contentMode = .bottom
      } else if rep.imagePosition == "centerLeft" {
        imageView.contentMode = .top
      }
      imageView.clipsToBounds = true
    }
    updateConstraintsIfNeeded()
    self.setNeedsLayout()
  }
  
  func updateContentScaleFactorIfNeeded() {
    guard let imageView = self.imageView else { return }
    guard let image = imageView.image else { return }
    guard let rep = representation else { return }
    if rep.imageSize == "fitWidth" && imageView.contentMode != .scaleAspectFill {
      let widthScale = image.size.width/imageView.frame.width
      let heightScale = image.size.height/imageView.frame.height
      imageView.contentScaleFactor = min(widthScale, heightScale)
    }
  }
  
}
