//
//  DataCollectionView.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-14.
//

import Foundation
import UIKit
let reuseIdentifier = "CellIdentifer";
class DataCollectionView : UIView, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout {
  
  
  var dataColumns: [DataColumn]?
  var dataRows: [DataRow]?
  
  override init(frame: CGRect) {
    super.init(frame: frame)
    
  }
  
  required init?(coder: NSCoder) {
    super.init(coder: coder)
  }
  
  func setData(columns: [DataColumn], withRows rows: [DataRow]) {
    dataColumns = columns;
    dataRows = rows;
    let flowLayout = UICollectionViewFlowLayout()
    
    let uiCollectionView = UICollectionView(frame: frame, collectionViewLayout: flowLayout)
    uiCollectionView.register(UICollectionViewCell.classForCoder(), forCellWithReuseIdentifier: "CellIdentifer")
    uiCollectionView.delegate = self
    uiCollectionView.dataSource = self
//    uiCollectionView.la
    addSubview(uiCollectionView)
  }
  
  func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
    let cell = collectionView.dequeueReusableCell(withReuseIdentifier: reuseIdentifier, for: indexPath)
    cell.backgroundColor = self.randomColor()
    return cell
  }
  
  //UICollectionViewDatasource methods
  func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
    return dataRows?.count ?? 0
  }
  
  func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
    return CGSize(width: self.frame.width, height: 48)
  }
  func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumInteritemSpacingForSectionAt section: Int) -> CGFloat {
    return 0
  }
  func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumLineSpacingForSectionAt section: Int) -> CGFloat {
    return 0;
  }
  // custom function to generate a random UIColor
  func randomColor() -> UIColor{
    let red = CGFloat(drand48())
    let green = CGFloat(drand48())
    let blue = CGFloat(drand48())
    return UIColor(red: red, green: green, blue: blue, alpha: 1.0)
  }
}
