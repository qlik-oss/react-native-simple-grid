//
//  DataCollectionView.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-14.
//

import Foundation
import UIKit
class DataCollectionView : UIView, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout {
  var dataColumns: [DataColumn]?
  var dataRows: [DataRow]?
  var dataSize: DataSize?
  var loading = false
  var onEndReached: RCTDirectEventBlock?
  var childCollectionView: UICollectionView?
  let reuseIdentifier = "CellIdentifer"
  
  init(frame: CGRect, withRows rows: [DataRow], andColumns cols: [DataColumn]) {
    super.init(frame: frame)
    setData(columns: cols, withRows: rows)
  }
  
  required init?(coder: NSCoder) {
    super.init(coder: coder)
  }
  
  fileprivate func setData(columns: [DataColumn], withRows rows: [DataRow]) {
    dataColumns = columns;
    dataRows = rows;
    let flowLayout = UICollectionViewFlowLayout()
    let frame = CGRect(x: 0, y: 0, width: frame.width, height: frame.height)
    let uiCollectionView = UICollectionView(frame: frame, collectionViewLayout: flowLayout)
    
    uiCollectionView.register(DataCellView.self, forCellWithReuseIdentifier: reuseIdentifier)
    uiCollectionView.delegate = self
    uiCollectionView.dataSource = self
    childCollectionView = uiCollectionView
    addSubview(uiCollectionView)
  }
  
  func appendData(rows: [DataRow]) {
    DispatchQueue.main.async {
      self.dataRows = rows
      self.childCollectionView?.reloadData()
      self.loading = false
    }
  }
  
  func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
    let cell = collectionView.dequeueReusableCell(withReuseIdentifier: reuseIdentifier, for: indexPath) as! DataCellView
    
    cell.backgroundColor = indexPath.row % 2 == 0 ? .white : UIColor(red:0.97, green:0.97, blue:0.97, alpha:1.0)
    if let data = dataRows {
      let dataRow = data[indexPath.row]
      cell.setData(row: dataRow, withColumns: dataColumns!)
    }
    return cell
  }
  
  func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
    return dataRows?.count ?? 0
  }
  
  func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
    let width = dataColumns?.reduce(0, {$0 + $1.width!}) ?? frame.width
    return CGSize(width: width, height: 48)
  }
  
  func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumInteritemSpacingForSectionAt section: Int) -> CGFloat {
    return 0
  }
  
  func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumLineSpacingForSectionAt section: Int) -> CGFloat {
    return 0;
  }
  
  func collectionView(_ collectionView: UICollectionView, willDisplay cell: UICollectionViewCell, forItemAt indexPath: IndexPath) {
    let rowCount = dataRows!.count
    if (indexPath.row == rowCount - 50 && !loading) {
      loadMoreData();
    }
  }
  
  fileprivate func loadMoreData() {
    DispatchQueue.global(qos: .userInitiated).async {
      if let requestOnEndReached = self.onEndReached, let size = self.dataSize, let rows = self.dataRows {
        if rows.count < size.qcy ?? 0 {
          self.loading = true
          requestOnEndReached(nil)
        }
      }
    }
  }
}
