//
//  ContainerView.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-14.
//

import Foundation
import SDWebImage

@objc
class ContainerView: UIView {
  var created = false
  var tableTheme: TableTheme?
  var dataSize: DataSize?
  var dataColumns: [DataColumn]?
  var dataRows: [DataRow]?
  var totals: Totals?
  let selectionsEngine = SelectionsEngine()
  let hScrollViewDelegate = HorizontalScrollViewDelegate()
  var needsGrabbers = true
  var cellStyle: CellStyle?
  var headerStyle: HeaderStyle?
  var defaultCalculated = false
  var menuTranslations: MenuTranslations?
  var horizontalScrollView: UIScrollView?
  var columnWidths = ColumnWidths()
  var maxHeaderLineCount = 1
  var maxTotalsLineCount = 1
  var maxCollectionViewsLineCount = 1
  weak var firstColumnTable: TableView?
  weak var multiColumnTable: TableView?

  @objc var onEndReached: RCTDirectEventBlock?
  @objc var onVerticalScrollEnded: RCTDirectEventBlock?
  @objc var onHeaderPressed: RCTDirectEventBlock?
  @objc var onExpandCell: RCTDirectEventBlock?
  @objc var onSearchColumn: RCTDirectEventBlock?
  @objc var containerWidth: NSNumber?
  @objc var freezeFirstColumn: Bool = false
  @objc var isDataView: Bool = false

  @objc var onSelectionsChanged: RCTDirectEventBlock? {
    didSet {
      selectionsEngine.onSelectionsChanged = onSelectionsChanged
    }
  }

  @objc var onConfirmSelections: RCTDirectEventBlock? {
    didSet {
      selectionsEngine.onConfirmSelections = onConfirmSelections
    }
  }

  @objc var clearSelections: NSString? {
    didSet {
      if let clearSelections = clearSelections {
        if clearSelections.compare("yes") == .orderedSame {
          selectionsEngine.clear()
        }
      }
    }
  }

  @objc var size: NSDictionary = [:] {
    didSet {
      do {
        let json = try JSONSerialization.data(withJSONObject: size)
        dataSize = try JSONDecoder().decode(DataSize.self, from: json)
        if let first = firstColumnTable, let multi = multiColumnTable {
          first.dataCollectionView?.dataSize = dataSize
          multi.dataCollectionView?.dataSize = dataSize
          first.dataCollectionView?.totalCellsView?.totalRows = dataSize?.qcy ?? 0
        }
      } catch {
        print(error)
      }
    }
  }

  @objc var theme: NSDictionary = [:] {
    didSet {
      do {
        let json = try JSONSerialization.data(withJSONObject: theme)
        tableTheme = try JSONDecoder().decode(TableTheme.self, from: json)
      } catch {
        print(error)
      }
    }
  }

  @objc var cols: NSDictionary = [:] {
    didSet {
      do {
        let json = try JSONSerialization.data(withJSONObject: cols)
        let decodedCols = try JSONDecoder().decode(Cols.self, from: json)
        if(dataColumns != nil && decodedCols.header?.count != dataColumns?.count) {
          return
        }
        dataColumns = decodedCols.header
        totals = decodedCols.totals
        guard let firstTable = self.firstColumnTable else { return }
        guard let multiTable = self.multiColumnTable else { return }
        
        if let firstTotals = firstTable.totalView, let multiTotals = multiTable.totalView {
          firstTotals.resetTotals(totals)
          multiTotals.resetTotals(totals)
        }

        if dataColumns != nil {
          if let firstHeader = firstTable.headerView, let multiHeader = multiTable.headerView {
            firstHeader.updateColumns(dataColumns!)
            multiHeader.updateColumns(dataColumns!)
          }
        }
      } catch {
        print(error)
      }
    }
  }

  @objc var rows: NSDictionary = [:] {
    didSet {
      do {
        NotificationCenter.default.post(name: Notification.Name.onClearSelectionBand, object: nil)
        let json = try JSONSerialization.data(withJSONObject: rows)
        let decodedRows = try JSONDecoder().decode(RowsObject.self, from: json)
       
        
        if(dataRows != nil && decodedRows.rows?.count != 0 && dataRows?.count != 0 && dataRows?[0].cells.count != decodedRows.rows?[0].cells.count) {
          return
        }
        if dataRows == nil || decodedRows.reset == true {
          self.dataRows = decodedRows.rows
          if self.dataRows != nil {
            if let firstColumnTable = self.firstColumnTable {
              firstColumnTable.dataCollectionView?.dataSize = dataSize
              firstColumnTable.dataCollectionView?.appendData(rows: dataRows!)
              firstColumnTable.dataCollectionView?.scrollToTop()
            }

            if let multiColumnTable = self.multiColumnTable {
              multiColumnTable.dataCollectionView?.dataSize = dataSize
              multiColumnTable.dataCollectionView?.appendData(rows: dataRows!)
              multiColumnTable.dataCollectionView?.scrollToTop()
            }
          }
        } else {
          if let newRows = decodedRows.rows {
            if dataRows != nil {
              dataRows?.append(contentsOf: newRows)
              if let firstColumnTable = self.firstColumnTable, let multiColumnTable = self.multiColumnTable {
                firstColumnTable.dataCollectionView?.appendData(rows: dataRows!)
                multiColumnTable.dataCollectionView?.appendData(rows: dataRows!)
              }
            }
          }
        }
        if decodedRows.reset == true {
          selectionsEngine.clear()
        }
      } catch {
        print(error)
      }
    }
  }

  @objc var cellContentStyle: NSDictionary = [:] {
    didSet {
      do {
        let json = try JSONSerialization.data(withJSONObject: cellContentStyle)
        let decodedCellStyle = try JSONDecoder().decode(CellContentStyle.self, from: json)
        cellStyle = CellStyle(cellContentStyle: decodedCellStyle)
      } catch {
        print(error)
      }
    }
  }

  @objc var headerContentStyle: NSDictionary = [:] {
    didSet {
      do {
        let json = try JSONSerialization.data(withJSONObject: headerContentStyle)
        let decodedHeaderStyle = try JSONDecoder().decode(HeaderContentStyle.self, from: json)
        headerStyle = HeaderStyle(headerContentSyle: decodedHeaderStyle)
      } catch {
        print(error)
      }
    }
  }

  @objc var name: String? {
    didSet {
      columnWidths.key = name
    }
  }

  @objc var translations: NSDictionary = [:] {
    didSet {
      do {
        let json = try JSONSerialization.data(withJSONObject: translations)
        let decodedTranslations = try JSONDecoder().decode(Translations.self, from: json)
        menuTranslations = decodedTranslations.menu
      } catch {
        print(error)
      }
    }
  }

  override var bounds: CGRect {
    didSet {
      guard let dataColumns = dataColumns else {return}
      guard let dataRows = dataRows else {return}
      columnWidths.loadDefaultWidths(bounds, columnCount: dataColumns.count, dataRows: dataRows, dataCols: dataColumns)

      if !created {
        created = true
        let tableViewFactory = TableViewFactory(containerView: self,
                                                columnWidths: columnWidths,
                                                dataColumns: dataColumns,
                                                freezeFirstCol: freezeFirstColumn)
        tableViewFactory.create()
        firstColumnTable = tableViewFactory.firstColumnTableView
        multiColumnTable = tableViewFactory.multiColumnTableView
        setNeedsLayout()
        DispatchQueue.main.async {
          self.horizontalScrollView?.setContentOffset(CGPoint(x: 0, y: 0), animated: false)
          self.firstColumnTable?.dataCollectionView?.postSignalVisibleRows(scrollsToTop: true)
          self.testTruncation()
          self.updateVScrollPos()
        }
      } else {
        guard let firstColumnTable = self.firstColumnTable else { return }
        guard let multiColumnTable = self.multiColumnTable else { return }
        hScrollViewDelegate.captureFirstColumnWidth()
        firstColumnTable.resizeCells()
        multiColumnTable.resizeCells()
        DispatchQueue.main.async {
          self.testTruncation()
          self.firstColumnTable?.dataCollectionView?.postSignalVisibleRows(scrollsToTop: true)
          self.updateVScrollPos()
        }
      }
    }
  }

  func testTruncation() {
    let headerWrap = headerStyle?.headerContentStyle?.wrap ?? true
    let cellWrap = cellStyle?.cellContentStyle?.wrap ?? true
    if headerWrap {
      testHeaders()
    }
    if cellWrap {
      testCollectionViews()
    }
  }

  func testHeaders() {
    guard let firstHeader = firstColumnTable?.headerView else { return }
    guard let multiHeader = multiColumnTable?.headerView else { return }
    var headerLineCount = 0
    headerLineCount = max(firstHeader.getMaxLineCount(), headerLineCount)
    headerLineCount = max(multiHeader.getMaxLineCount(), headerLineCount)

    if headerLineCount != maxHeaderLineCount {
      maxHeaderLineCount = headerLineCount
      let height = Double(maxHeaderLineCount) * (headerStyle?.lineHeight ?? 1.0)
      firstHeader.dynamicHeightAnchor.constant = height + (PaddedLabel.PaddingSize * 2.0)
      multiHeader.dynamicHeightAnchor.constant = height + (PaddedLabel.PaddingSize * 2.0)
      firstColumnTable?.updateGrabbers(height + (PaddedLabel.PaddingSize * 2.0))
      multiColumnTable?.updateGrabbers(height + (PaddedLabel.PaddingSize * 2.0))
      firstHeader.layoutIfNeeded()
      multiHeader.layoutIfNeeded()
      layoutIfNeeded()
      DispatchQueue.main.async {
        self.firstColumnTable?.dataCollectionView?.postSignalVisibleRows(scrollsToTop: false)
      }
    }
    testTotals()
  }

  func testTotals() {
    guard let firstTotal = firstColumnTable?.totalView else { return }
    guard let multiTotal = multiColumnTable?.totalView else { return }
    var lineCount = 0
    lineCount = max(firstTotal.getMaxLineCount(), lineCount)
    lineCount = max(multiTotal.getMaxLineCount(), lineCount)

    // uses cellcontent style for style, but header.wrap for checking wrap
    if lineCount != maxTotalsLineCount {
      maxTotalsLineCount = lineCount
      let height = Double(maxTotalsLineCount) * (cellStyle?.lineHeight ?? 1.0)
      firstTotal.dynamicHeight.constant = height + (PaddedLabel.PaddingSize * 2.0)
      multiTotal.dynamicHeight.constant = height + (PaddedLabel.PaddingSize * 2.0)
      firstTotal.layoutIfNeeded()
      multiTotal.layoutIfNeeded()
      layoutIfNeeded()
      DispatchQueue.main.async {
        self.firstColumnTable?.dataCollectionView?.postSignalVisibleRows(scrollsToTop: false)
      }
    }
  }

  func testCollectionViews() {
    guard let first = firstColumnTable?.dataCollectionView else { return }
    guard let multi = multiColumnTable?.dataCollectionView else { return }
    var lineCount = 0
    lineCount = max(first.getMaxLineCount(), lineCount)
    lineCount = max(multi.getMaxLineCount(), lineCount)
    if lineCount != maxCollectionViewsLineCount {
      maxCollectionViewsLineCount = lineCount
      first.setMaxLineCount(maxCollectionViewsLineCount)
      multi.setMaxLineCount(maxCollectionViewsLineCount)
      DispatchQueue.main.async {
        self.firstColumnTable?.dataCollectionView?.postSignalVisibleRows(scrollsToTop: false)
      }
      layoutIfNeeded()
    }
  }
  
  override func layoutSubviews() {
    super.layoutSubviews()
    self.backgroundColor = isDataView ? .white : ColorParser.fromCSS(cssString: tableTheme?.backgroundColor ?? "white")
    firstColumnTable?.dataCollectionView?.childCollectionView?.setScrollableArea(self.frame);
    multiColumnTable?.dataCollectionView?.childCollectionView?.setScrollableArea(self.frame);
    updateVScrollPos()
  }
  
  func updateVScrollPos() {
    let totalWidth = columnWidths.getTotalWidth()
    let rawX = firstColumnTable?.horizontalScrolLView?.contentOffset.x ?? 0.0
    var right = max(abs(self.frame.width  -  totalWidth) - rawX, 0)
    if(totalWidth < frame.width) {
      right = 2.0
    }
    firstColumnTable?.dataCollectionView?.childCollectionView?.scrollIndicatorInsets = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: right)
    multiColumnTable?.dataCollectionView?.childCollectionView?.scrollIndicatorInsets = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: right)
  }

}
