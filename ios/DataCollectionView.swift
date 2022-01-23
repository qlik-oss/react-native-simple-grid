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
  var onColumnsResized: RCTDirectEventBlock?
  var childCollectionView: UICollectionView?
  var tableTheme: TableTheme?
  var selectionsEngine: SelectionsEngine?
  let reuseIdentifier = "CellIdentifer"
  weak var doubleTapGesture: UITapGestureRecognizer?
  
  init(frame: CGRect, withRows rows: [DataRow], andColumns cols: [DataColumn], theme: TableTheme, selectionsEngine: SelectionsEngine) {
    super.init(frame: frame)
    self.tableTheme = theme
    self.selectionsEngine = selectionsEngine
    setData(columns: cols, withRows: rows)
    
  }
  
  required init?(coder: NSCoder) {
    super.init(coder: coder)
  }
  
  func scrollToTop() {
    if let childCollectionView = childCollectionView {
      childCollectionView.scrollToItem(at: IndexPath(item: 0, section: 0), at: .top, animated: true)
    }
  }
  
  func updateSize(_ translation: CGPoint, withColumn index: Int) {
    if let cv = self.childCollectionView{
      let visibleCells = cv.subviews
        for cell in visibleCells {
          if let uiCell = cell as? DataCellView {
            uiCell.updateSize(translation, forColumn: index)
          }
        }
      dataColumns![index].width! += Double(translation.x)
      if index + 1 < dataColumns!.count {
        dataColumns![index + 1].width! -= Double(translation.x)
      }
    }
    
    resizeFrame(index)
  }
  
  func onEndDrag( _ index: Int) {
    resizeFrame(index)
    signalColumnsWidthChanged()
  }
  
  fileprivate func signalColumnsWidthChanged() {
    if let onColumnsResized = onColumnsResized, let dataColumns = dataColumns {
      let widths = dataColumns.map{$0.width}
      onColumnsResized(["widths": widths])
    }
  }
  
  fileprivate func resizeFrame(_ index: Int) {
    if(index + 1 == dataColumns!.count) {
      if let cv = self.childCollectionView {
        // need to resize everyone
        let oldFrame = self.frame
        let width = dataColumns!.reduce(0, {$0 + $1.width!})
        let newFrame = CGRect(x: 0, y: oldFrame.origin.y, width: width, height: oldFrame.height)
        self.frame = newFrame
        cv.collectionViewLayout.invalidateLayout()
      }
    }
  }
  
  fileprivate func setData(columns: [DataColumn], withRows rows: [DataRow]) {
    dataColumns = columns;
    dataRows = rows;
    let flowLayout = UICollectionViewFlowLayout()
    let uiCollectionView = UICollectionView(frame: .zero, collectionViewLayout: flowLayout)
    uiCollectionView.translatesAutoresizingMaskIntoConstraints = false
    
    uiCollectionView.register(DataCellView.self, forCellWithReuseIdentifier: reuseIdentifier)
    uiCollectionView.delegate = self
    uiCollectionView.dataSource = self
    childCollectionView = uiCollectionView
    addSubview(uiCollectionView)
   
    uiCollectionView.translatesAutoresizingMaskIntoConstraints = false
    let top = uiCollectionView.topAnchor.constraint(equalTo: self.topAnchor)
    let bottom = uiCollectionView.bottomAnchor.constraint(equalTo: self.bottomAnchor)
    let left = uiCollectionView.leftAnchor.constraint(equalTo: self.leftAnchor)
    let right = uiCollectionView.rightAnchor.constraint(equalTo: self.rightAnchor)
    self.addConstraints([top, bottom, left, right])

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
    cell.doubleTapGesture = self.doubleTapGesture
    if let data = dataRows {
      let dataRow = data[indexPath.row]
      cell.setData(row: dataRow, withColumns: dataColumns!, theme: tableTheme!, selectionsEngine: selectionsEngine!)
    }
    return cell
  }
  
  func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
    return dataRows?.count ?? 0
  }
  
  func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
    let width = dataColumns?.reduce(0, {$0 + $1.width!}) ?? frame.width
    return CGSize(width: width, height: CGFloat(tableTheme!.rowHeight!))
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
