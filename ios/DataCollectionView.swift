//
//  DataCollectionView.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-14.
//

import Foundation
import UIKit
class DataCollectionView: UIView, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout, UICollectionViewDelegate {
  enum DataCollectionViewError: Error {
    case noCellForIdentifier
  }
  
  var stylingInfo = StylingInfo()
  var dataColumns: [DataColumn]?
  var dataRows: [DataRow]?
  var dataSize: DataSize?
  var loading = false
  var onEndReached: RCTDirectEventBlock?
  var childCollectionView: UICollectionView?
  var tableTheme: TableTheme?
  var selectionsEngine: SelectionsEngine?
  let reuseIdentifier = "CellIdentifier"
  var cellColor = UIColor.black
  var cellStyle = CellContentStyle()
  var isDataView = false
  var dataRange: CountableRange = 0..<2
  weak var totalCellsView: TotalCellsView?
  weak var columnWidths: ColumnWidths?
  weak var slave: DataCollectionView?
  
  init(frame: CGRect, withRows rows: [DataRow],
       andColumns cols: [DataColumn],
       theme: TableTheme,
       selectionsEngine: SelectionsEngine,
       cellStyle: CellContentStyle,
       columnWidths: ColumnWidths,
       range: CountableRange<Int>) {
    super.init(frame: frame)
    self.columnWidths = columnWidths
    self.tableTheme = theme
    self.selectionsEngine = selectionsEngine
    let colorParser = ColorParser()
    self.cellStyle = cellStyle
    self.dataRange = range
    if let colorString = cellStyle.color {
      cellColor = colorParser.fromCSS(cssString: colorString)
    }
    setData(columns: cols, withRows: rows)
    fitToFrame()
  }
  
  fileprivate func fitToFrame() {
    guard let childCollectionView = childCollectionView else {
      return
    }
    childCollectionView.translatesAutoresizingMaskIntoConstraints = false
    let top = childCollectionView.topAnchor.constraint(equalTo: self.topAnchor)
    let bottom = childCollectionView.bottomAnchor.constraint(equalTo: self.bottomAnchor)
    let left = childCollectionView.leftAnchor.constraint(equalTo: self.leftAnchor)
    let right = childCollectionView.rightAnchor.constraint(equalTo: self.rightAnchor)
    NSLayoutConstraint.activate([top, bottom, left, right])
    self.addConstraints([top, bottom, left, right])
  }
  
  required init?(coder: NSCoder) {
    super.init(coder: coder)
  }
  
  func scrollToTop() {
    if let childCollectionView = childCollectionView {
      childCollectionView.scrollToItem(at: IndexPath(item: 0, section: 0), at: .top, animated: true)
    }
  }
  
 
  
  func updateSize(_ translation: CGPoint, withColumn index: Int) -> Bool {
    if let cv = self.childCollectionView {
      let visibleCells = cv.subviews
      for cell in visibleCells {
        if let uiCell = cell as? DataCellView {
          if !uiCell.updateSize(translation, forColumn: index) {
            return false
          }
        }
      }
    }
    
    if let columnWidths = columnWidths {
      columnWidths.resize(index: index, by: translation)
    }
    
    resizeFrame(index)
    
    return true
  }
  
  func scrollViewDidScroll(_ scrollView: UIScrollView) {
    syncScrolling()
  }
  
  func syncScrolling() {
    if let slave = slave, let childCollectionView = childCollectionView  {
      if let slaveChild = slave.childCollectionView {
        let y = childCollectionView.contentOffset.y
        slaveChild.contentOffset.y = y
      }
    }
  }
  
  func onEndDrag( _ index: Int) {
    resizeFrame(index)
  }
  
  func resizeFrame(_ index: Int) {
    if index + 1 == dataColumns!.count {
      if let cv = self.childCollectionView, let columnWidths = columnWidths {
        // need to resize everyone
        let oldFrame = self.frame
        let width = columnWidths.getTotalWidth()
        let newFrame = CGRect(x: 0, y: oldFrame.origin.y, width: width, height: oldFrame.height)
        self.frame = newFrame
        cv.collectionViewLayout.invalidateLayout()
      }
    }
  }
  
  func setData(columns: [DataColumn], withRows rows: [DataRow]) {
    dataColumns = columns
    dataRows = rows
    setupDataCols()
    let flowLayout = UICollectionViewFlowLayout()
    let uiCollectionView = UICollectionView(frame: .zero, collectionViewLayout: flowLayout)
    uiCollectionView.translatesAutoresizingMaskIntoConstraints = false
    
    uiCollectionView.register(DataCellView.self, forCellWithReuseIdentifier: reuseIdentifier)
    uiCollectionView.delegate = self
    uiCollectionView.dataSource = self
    uiCollectionView.indicatorStyle = .black
    uiCollectionView.backgroundColor = .white
    childCollectionView = uiCollectionView
    addSubview(uiCollectionView)
  }
  
  fileprivate func setupDataCols() {
    guard let dataColumns = dataColumns else {
      return
    }
    
    for col in dataColumns[dataRange] {
      if let stylingInfo = col.stylingInfo {
        var index = 0
        for style in stylingInfo {
          if( style == "cellBackgroundColor") {
            self.stylingInfo.backgroundColorIdx = index;
          } else if (style == "cellForegroundColor") {
            self.stylingInfo.foregroundColorIdx = index
          }
          index += 1
        }
      }
    }
  }
  
  func appendData(rows: [DataRow]) {
    DispatchQueue.main.async {
      self.dataRows = rows
      self.childCollectionView?.reloadData()
      self.loading = false
    }
  }
  
  func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
    // swiftlint:disable:next force_cast
    let cell = collectionView.dequeueReusableCell(withReuseIdentifier: reuseIdentifier, for: indexPath) as! DataCellView
    cell.isDataView = self.isDataView
    cell.backgroundColor = isDataView ? indexPath.row % 2 == 0 ? .white : UIColor(red: 0.97, green: 0.97, blue: 0.97, alpha: 1.0) : .white
    cell.cellColor = cellColor
    cell.numberOfLines = cellStyle.rowHeight ?? 1
    if let data = dataRows, let columnWidths = columnWidths, let dataColumns = dataColumns {
      let dataRow = data[indexPath.row]
      cell.selectionsEngine = self.selectionsEngine
      cell.setData(row: dataRow, withColumns: dataColumns[dataRange],
                   columnWidths: columnWidths.columnWidths,
                   theme: tableTheme!,
                   selectionsEngine: selectionsEngine!,
                   withStyle: self.stylingInfo,
                   withRange: dataRange)
    }
    return cell
  }
  
  func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
    return dataRows?.count ?? 0
  }
  
  func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
    let width = columnWidths?.getTotalWidth(range: dataRange) ?? 400.0
    let height = cellStyle.rowHeight ?? 1
    return CGSize(width: width, height: CGFloat(tableTheme!.rowHeight! * height))
  }
  
  func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumInteritemSpacingForSectionAt section: Int) -> CGFloat {
    return 0
  }
  
  func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumLineSpacingForSectionAt section: Int) -> CGFloat {
    return 0
  }
  
  func collectionView(_ collectionView: UICollectionView, willDisplay cell: UICollectionViewCell, forItemAt indexPath: IndexPath) {
    let rowCount = dataRows!.count
    if indexPath.row == rowCount - 50 && !loading {
      loadMoreData()
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
  
  public func resizeCells (withFrame frame: CGRect) {
    guard let childCollectionView = childCollectionView else {
      return
    }
    guard let dataColumns = dataColumns else {
      return
    }
    
    guard let columnWidths = columnWidths else {
      return
    }
    
    self.frame = frame
    for visibleRow in childCollectionView.subviews {
      if let dataCellView = visibleRow as? DataCellView {
        dataCellView.updateLayout(withColumns: dataColumns, columnWidths: columnWidths.columnWidths)
      }
    }
    childCollectionView.collectionViewLayout.invalidateLayout()
    childCollectionView.reloadData()
  }
  
}
