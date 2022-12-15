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
  
  var stylingInfo = [StylingInfo]()
  var dataColumns: [DataColumn]?
  var dataRows: [DataRow]?
  var dataSize: DataSize?
  var loading = false
  var onEndReached: RCTDirectEventBlock?
  var onExpandedCell: RCTDirectEventBlock?
  var childCollectionView: UICollectionView?
  var tableTheme: TableTheme?
  var selectionsEngine: SelectionsEngine?
  let reuseIdentifier = "CellIdentifier"
  var cellColor = UIColor.black
  var isDataView = false
  var dataRange: CountableRange = 0..<2
  var freezeFirstColumn = false
  var cellStyle: CellStyle?
  var menuTranslations: MenuTranslations?
  var maxRowLineCount = 1
  weak var totalCellsView: TotalCellsView?
  weak var columnWidths: ColumnWidths?
  weak var coupled: DataCollectionView?
  weak var headerView: HeaderView?
  weak var hScrollView: UIScrollView?
  weak var selectionBand: SelectionBand?
  
  init(frame: CGRect, withRows rows: [DataRow],
       andColumns cols: [DataColumn],
       theme: TableTheme,
       selectionsEngine: SelectionsEngine,
       cellStyle: CellStyle?,
       columnWidths: ColumnWidths,
       range: CountableRange<Int>) {
    super.init(frame: frame)
    self.columnWidths = columnWidths
    self.tableTheme = theme
    self.selectionsEngine = selectionsEngine
    self.cellStyle = cellStyle
    self.dataRange = range
    self.clipsToBounds = false
    if let cellContentStyle = cellStyle?.cellContentStyle {
      if let colorString = cellContentStyle.color {
        cellColor = ColorParser.fromCSS(cssString: colorString)
      }
    }
    setData(columns: cols, withRows: rows)
    fitToFrame()
    createSelectionBands()
  }
  
  fileprivate func fitToFrame() {
    guard let childCollectionView = childCollectionView else {
      return
    }
    childCollectionView.translatesAutoresizingMaskIntoConstraints = false
    childCollectionView.fitToView(self)
    childCollectionView.showsHorizontalScrollIndicator = false
  }
  
  fileprivate func createSelectionBands() {
    guard let childCollectionView = childCollectionView else { return }
    
    let selectionBand = SelectionBand(frame: self.frame)
    childCollectionView.addSubview(selectionBand)
    selectionBand.translatesAutoresizingMaskIntoConstraints = false
    selectionBand.fitToView(self)
    if let selectionsEngine = self.selectionsEngine {
      selectionsEngine.setSelectionBand(selectionBand)
    }
    self.selectionBand = selectionBand
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
    if !updateCell(translation, withColum: index) {
      return false
    }
    
    return true
  }
  
  func updateCell(_ translation: CGPoint, withColum index: Int) -> Bool {
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
    return true
  }
  
  func resizeCells() {
    guard let columnWidths = columnWidths else { return }
    if let cv = self.childCollectionView {
      let visibleCells = cv.subviews
      for cell in visibleCells {
        if let uiCell = cell as? DataCellView {
          uiCell.resizeCells(columnWidths, withRange: dataRange)
        }
      }
    }
    childCollectionView?.collectionViewLayout.invalidateLayout()
  }
  
  func scrollViewDidScroll(_ scrollView: UIScrollView) {
    syncScrolling()
  }
  
  func syncScrolling() {
    if let coupled = coupled, let childCollectionView = childCollectionView {
      coupled.selectionBand?.clearRect()
      if let child = coupled.childCollectionView {
        let y = childCollectionView.contentOffset.y
        child.contentOffset.y = y
      }
      selectionBand?.clearRect()
    }
    signalVisibleRows()
  }
  
  
  func signalVisibleRows() {
    if let childCollectionView = childCollectionView {
      let visibleIndexPaths = childCollectionView.indexPathsForVisibleItems.sorted()
      let fullyVisible = visibleIndexPaths.filter { indexPath in
        let layout = childCollectionView.layoutAttributesForItem(at: indexPath)!
        let half = layout.frame.height / 2
        var deflatedFrame = layout.frame.insetBy(dx: 0, dy: half).offsetBy(dx: 0, dy: -half/4.0)
        return childCollectionView.bounds.intersects(deflatedFrame)
      }
      let firstItem = fullyVisible.first
      let lastItem = fullyVisible.last
      if let totalCellsView = totalCellsView, let first = firstItem, let last = lastItem {
        totalCellsView.updateTotals(first: first, last: last)
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
    uiCollectionView.panGestureRecognizer.minimumNumberOfTouches = 2
    childCollectionView = uiCollectionView
    addSubview(uiCollectionView)
  }
  
  fileprivate func setupDataCols() {
    guard let dataColumns = dataColumns else {
      return
    }
    self.stylingInfo = [StylingInfo]()
    dataColumns[dataRange].enumerated().forEach {(index, element) in
      if let stylingInfo = element.stylingInfo {
        var index = 0
        var si = StylingInfo()
        for style in stylingInfo {
          if  style == "cellBackgroundColor" {
            si.backgroundColorIdx = index
          } else if style == "cellForegroundColor" {
            si.foregroundColorIdx = index
          }
          index += 1
        }
        self.stylingInfo.append(si)
      }
    }
  }
  
  func appendData(rows: [DataRow]) {
    DispatchQueue.main.async {
      self.dataRows = rows
      self.childCollectionView?.reloadData()
      self.childCollectionView?.performBatchUpdates({
        self.signalVisibleRows()
      })
      self.loading = false
    }
  }
  
  func postSignalVisibleRows(scrollsToTop: Bool) {
    DispatchQueue.main.async {
      self.childCollectionView?.performBatchUpdates({
        self.childCollectionView?.reloadData()
        self.signalVisibleRows()
        if(scrollsToTop) {
          self.scrollToTop()
        }
      })
    }
  }
  
  // MARK: collectionview
  func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
    // swiftlint:disable:next force_cast
    let cell = collectionView.dequeueReusableCell(withReuseIdentifier: reuseIdentifier, for: indexPath) as! DataCellView
    cell.isDataView = self.isDataView
    cell.selectionBand = self.selectionBand
    cell.menuTranslations = self.menuTranslations
    cell.dataCollectionView = self
    cell.backgroundColor = isDataView ? indexPath.row % 2 == 0 ? .white : UIColor(red: 0.97, green: 0.97, blue: 0.97, alpha: 1.0) : .white
    cell.cellColor = cellColor
    cell.onExpandedCellEvent = onExpandedCell
    cell.numberOfLines = maxRowLineCount
    if let data = dataRows, let columnWidths = columnWidths, let dataColumns = dataColumns {
      let dataRow = data[indexPath.row]
      cell.selectionsEngine = self.selectionsEngine
      cell.setData(row: dataRow,
                   dataColumns: dataColumns,
                   columnWidths: columnWidths,
                   theme: tableTheme!,
                   selectionsEngine: selectionsEngine!,
                   withStyle: self.stylingInfo,
                   cellStyle: self.cellStyle,
                   withRange: dataRange)
    }
    
    return cell
  }
  
  func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
    return dataRows?.count ?? 0
  }
  
  func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
    let width = self.bounds.width
    let numberOfLines = max(cellStyle?.cellContentStyle?.rowHeight ?? maxRowLineCount, maxRowLineCount)
    let lineHeight = cellStyle?.lineHeight ?? TableTheme.CellContentHeight
    let height = Double(numberOfLines) * lineHeight
    return CGSize(width: width, height: height + (PaddedLabel.PaddingSize * 2.0))
  }
  
  func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumInteritemSpacingForSectionAt section: Int) -> CGFloat {
    return 0
  }
  
  func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumLineSpacingForSectionAt section: Int) -> CGFloat {
    return 0.5
  }
  
  func collectionView(_ collectionView: UICollectionView, willDisplay cell: UICollectionViewCell, forItemAt indexPath: IndexPath) {
    let rowCount = dataRows!.count
    if indexPath.row == rowCount - 50 && !loading {
      loadMoreData()
    }
  }
  
  func loadMoreData() {
    DispatchQueue.global(qos: .userInitiated).async {
      if let requestOnEndReached = self.onEndReached, let size = self.dataSize, let rows = self.dataRows {
        if rows.count < size.qcy ?? 0 {
          self.loading = true
          requestOnEndReached(nil)
        }
      }
    }
  }
  
  func getMaxLineCount() -> Int {
    guard let childCollectionView = childCollectionView else { return maxRowLineCount }
    var lines = 0
    for view in childCollectionView.subviews {
      if let cellView = view as? DataCellView {
        lines = max(cellView.getMaxLineCount(), lines)
      }
    }
    return lines
  }
  
  func setMaxLineCount(_ lines: Int) {
    maxRowLineCount = lines
    childCollectionView?.collectionViewLayout.invalidateLayout()
  }
  
}
