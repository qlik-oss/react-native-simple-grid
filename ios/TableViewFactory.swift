//
//  TableViewFactory.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-11-03.
//

import Foundation

extension UIView {
  func addDebugLayer(_ color: UIColor) {
    self.layer.borderColor = color.cgColor
    self.layer.borderWidth = 1
  }

  func addDebugLayer(_ color: UIColor, width: Double) {
    self.layer.borderColor = color.cgColor
    self.layer.borderWidth = width
  }

  func fitToView(_ superView: UIView) {
    let constraints = [
      self.leadingAnchor.constraint(equalTo: superView.leadingAnchor),
      self.trailingAnchor.constraint(equalTo: superView.trailingAnchor),
      self.topAnchor.constraint(equalTo: superView.topAnchor),
      self.bottomAnchor.constraint(equalTo: superView.bottomAnchor)
    ]
    NSLayoutConstraint.activate(constraints)
    superView.addConstraints(constraints)
  }

  func makeReadble(_ superView: UIView) {
    let constraints = [
        self.leadingAnchor.constraint(equalTo: superView.readableContentGuide.leadingAnchor),
        self.trailingAnchor.constraint(equalTo: superView.readableContentGuide.trailingAnchor),
        self.topAnchor.constraint(equalTo: superView.topAnchor),
        self.bottomAnchor.constraint(equalTo: superView.bottomAnchor)
    ]

    NSLayoutConstraint.activate(constraints)
    superView.addConstraints(constraints)
  }
}

class TableViewFactory {
  var columnWidths: ColumnWidths
  var firstColumnFrozen = false
  var containerView: ContainerView
  var firstColumnTableView = TableView()
  var horizontalScrollView = UIScrollView()
  var dataColumns: [DataColumn]
  var multiColumnTableView = TableView()
  var grabbers = [() -> MultiColumnResizer?]()
  var totals: Totals?

  init(containerView: ContainerView, columnWidths: ColumnWidths, dataColumns: [DataColumn], freezeFirstCol: Bool) {
    self.containerView = containerView
    self.columnWidths = columnWidths
    self.firstColumnFrozen = freezeFirstCol
    self.dataColumns = dataColumns
    self.totals = containerView.totals
  }

  func create() {
    containerView.hScrollViewDelegate.tableView = firstColumnTableView
    containerView.hScrollViewDelegate.multiColumnTable = multiColumnTableView
    containerView.hScrollViewDelegate.columnWidths = containerView.columnWidths
    containerView.hScrollViewDelegate.freezeFirstCol = containerView.freezeFirstColumn
    containerView.hScrollViewDelegate.captureFirstColumnWidth()
    containerView.hScrollViewDelegate.containerView = containerView
    containerView.horizontalScrollView = horizontalScrollView
    containerView.addSubview(horizontalScrollView)
    horizontalScrollView.addSubview(firstColumnTableView)
    horizontalScrollView.addSubview(multiColumnTableView)
    horizontalScrollView.delegate = containerView.hScrollViewDelegate
    createHScrollView()
    addShadowsToHeadersIfNeeded()
    finalizeSetup()

  }

  func createHScrollView() {
    horizontalScrollView.translatesAutoresizingMaskIntoConstraints = false

    let constraints = [
      horizontalScrollView.leadingAnchor.constraint(equalTo: containerView.leadingAnchor),
      horizontalScrollView.trailingAnchor.constraint(equalTo: containerView.trailingAnchor),
      horizontalScrollView.topAnchor.constraint(equalTo: containerView.topAnchor),
      horizontalScrollView.bottomAnchor.constraint(equalTo: containerView.bottomAnchor, constant: -TableTheme.TotalRowViewHeight)
    ]
    NSLayoutConstraint.activate(constraints)
    containerView.addConstraints(constraints)

    createFirstColumn()
    firstColumnTableView.horizontalScrolLView = horizontalScrollView

    let totalWidth = columnWidths.getTotalWidth()
    horizontalScrollView.contentSize = CGSize(width: totalWidth, height: 0)
    horizontalScrollView.contentInset = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: TableTheme.HorizontalScrollPadding)
  }

  func createFirstColumn() {
    let width = columnWidths.columnWidths[0]
    firstColumnTableView.translatesAutoresizingMaskIntoConstraints = false
    firstColumnTableView.dataRange = 0..<1
    firstColumnTableView.isFirst = true
    firstColumnTableView.dynamicWidth = firstColumnTableView.widthAnchor.constraint(equalToConstant: width)
    firstColumnTableView.backgroundColor = .white
    let leadingAnchor = containerView.freezeFirstColumn ? horizontalScrollView.frameLayoutGuide.leadingAnchor : horizontalScrollView.leadingAnchor

    let constraints = [
      firstColumnTableView.leadingAnchor.constraint(equalTo: leadingAnchor),
      firstColumnTableView.heightAnchor.constraint(equalTo: horizontalScrollView.heightAnchor),
      firstColumnTableView.dynamicWidth
    ]

    NSLayoutConstraint.activate(constraints)
    horizontalScrollView.addConstraints(constraints)

    createFirstColumnHeaderView()
  }

  func createFirstColumnHeaderView() {
    createHeader(forTable: firstColumnTableView, withRange: 0..<1)
    createTotalsView(for: firstColumnTableView, withRange: 0..<1, first: true)
    createFirstColumnTotalsView()
  }

  func createFirstColumnTotalsView() {
    createFirstColumnDataView()
  }

  func createFirstColumnDataView() {
    guard let dataRows = containerView.dataRows else { return }
    guard let cellStyle = containerView.cellStyle else { return }
    guard let theme = containerView.tableTheme else { return }
    let frame = CGRect(origin: CGPoint.zero, size: CGSize(width: columnWidths.getTotalWidth(range: 0..<1), height: containerView.frame.height))

    let dataCollectionView = DataCollectionView(frame: frame,
                                                withRows: dataRows,
                                                andColumns: dataColumns,
                                                theme: theme,
                                                selectionsEngine: containerView.selectionsEngine,
                                                cellStyle: cellStyle,
                                                columnWidths: columnWidths,
                                                isDataView: containerView.isDataView,
                                                range: 0..<1)
    fitDataCollectionView(dataCollectionView, on: firstColumnTableView, withRane: 0..<1)
    wireDataCollectionView(dataCollectionView)
    createFirstColumnScreenGrabber()
  }

  func createFirstColumnScreenGrabber() {

    let width = columnWidths.columnWidths[0]
    let resizer = ColumnResizerView(columnWidths, index: 0, bindTo: firstColumnTableView)
    resizer.adjacentTable = multiColumnTableView
    resizer.horizontalScrollView = horizontalScrollView
    resizer.headerView = firstColumnTableView.headerView
    resizer.totalsView = firstColumnTableView.totalView
    resizer.selectionsBand = firstColumnTableView.dataCollectionView?.selectionBand
    resizer.lineWidth = 2.0
    resizer.translatesAutoresizingMaskIntoConstraints = false
    resizer.borderColor = ColorParser.fromCSS(cssString: containerView.tableTheme?.borderBackgroundColor ?? "lightgray")
    containerView.hScrollViewDelegate.grabber = resizer

    var constraints = [NSLayoutConstraint]()

    if containerView.freezeFirstColumn {
      containerView.addSubview(resizer)
      resizer.centerConstraint = resizer.centerXAnchor.constraint(equalTo: containerView.leadingAnchor, constant: width)
      constraints = [
        resizer.topAnchor.constraint(equalTo: containerView.topAnchor),
        resizer.bottomAnchor.constraint(equalTo: containerView.bottomAnchor, constant: -TableTheme.TotalRowViewHeight),
        resizer.widthAnchor.constraint(equalToConstant: TableTheme.DefaultResizerWidth),
        resizer.centerConstraint
      ]
    } else {
      horizontalScrollView.addSubview(resizer)
      resizer.centerConstraint = resizer.centerXAnchor.constraint(equalTo: horizontalScrollView.leadingAnchor, constant: width)
      constraints = [
        resizer.topAnchor.constraint(equalTo: horizontalScrollView.frameLayoutGuide.topAnchor),
        resizer.bottomAnchor.constraint(equalTo: horizontalScrollView.frameLayoutGuide.bottomAnchor),
        resizer.widthAnchor.constraint(equalToConstant: TableTheme.DefaultResizerWidth),
        resizer.centerConstraint
      ]
    }

    NSLayoutConstraint.activate(constraints)
    containerView.addConstraints(constraints)
    firstColumnTableView.firstGrabber = resizer
    resizer.containerView = containerView
    createMultiColumnTable()
  }

  func createMultiColumnTable() {

    let width = columnWidths.getTotalWidth(range: 1..<columnWidths.count())
    multiColumnTableView.dataRange = 1..<columnWidths.count()
    multiColumnTableView.translatesAutoresizingMaskIntoConstraints = false
    multiColumnTableView.dynamicWidth = multiColumnTableView.widthAnchor.constraint(greaterThanOrEqualToConstant: width)
    multiColumnTableView.dymaniceLeadingAnchor = multiColumnTableView.leadingAnchor.constraint(equalTo: horizontalScrollView.leadingAnchor,
                                                                                               constant: columnWidths.columnWidths[0])

    let constraints = [
      multiColumnTableView.heightAnchor.constraint(equalTo: horizontalScrollView.heightAnchor),
      multiColumnTableView.dynamicWidth,
      multiColumnTableView.dymaniceLeadingAnchor
    ]
    NSLayoutConstraint.activate(constraints)
    horizontalScrollView.addConstraints(constraints)
    createMultiColumnHeader()
  }

  func createMultiColumnHeader() {
    createHeader(forTable: multiColumnTableView, withRange: 1..<columnWidths.count())
    createTotalsView(for: multiColumnTableView, withRange: 1..<columnWidths.count(), first: false)
    createMultiColumnDataCollection()
  }

  func createMultiColumnDataCollection() {
    guard let dataRows = containerView.dataRows else { return }
    guard let cellStyle = containerView.cellStyle else { return }
    guard let theme = containerView.tableTheme else { return }
    let frame = CGRect.zero

    let dataCollectionView = DataCollectionView(frame: frame,
                                                withRows: dataRows,
                                                andColumns: dataColumns,
                                                theme: theme,
                                                selectionsEngine: containerView.selectionsEngine,
                                                cellStyle: cellStyle,
                                                columnWidths: columnWidths,
                                                isDataView: containerView.isDataView,
                                                range: 1..<columnWidths.count())
    fitDataCollectionView(dataCollectionView, on: multiColumnTableView, withRane: 0..<1)
    wireDataCollectionView(dataCollectionView)
    createGrabbers()
  }

  func createGrabbers() {
    if columnWidths.count() > 1 {
      let range = 1..<columnWidths.count() - 1
      var x = 0.0
      columnWidths.columnWidths[range].enumerated().forEach {(index, width) in
        x += width
        let resizer = MultiColumnResizer(columnWidths, index: index, bindTo: multiColumnTableView)
        resizer.translatesAutoresizingMaskIntoConstraints = false
        resizer.centerConstraint = resizer.centerXAnchor.constraint(equalTo: multiColumnTableView.leadingAnchor, constant: x)
        resizer.headerView = multiColumnTableView.headerView
        resizer.totalsView = multiColumnTableView.totalView
        resizer.horizontalScrollView = horizontalScrollView
        resizer.containerView = containerView
        resizer.borderColor = ColorParser.fromCSS(cssString: containerView.tableTheme?.borderBackgroundColor ?? "lightGray")
        resizer.selectionsBand = multiColumnTableView.dataCollectionView?.selectionBand
        resizer.layer.zPosition = 1
        multiColumnTableView.addSubview(resizer)
        let constraints = [
          resizer.topAnchor.constraint(equalTo: multiColumnTableView.topAnchor),
          resizer.bottomAnchor.constraint(equalTo: multiColumnTableView.bottomAnchor),
          resizer.widthAnchor.constraint(equalToConstant: TableTheme.DefaultResizerWidth),
          resizer.centerConstraint
        ]
        NSLayoutConstraint.activate(constraints)
        containerView.addConstraints(constraints)
        grabbers.append({[weak resizer] in return resizer})
      }
    }

    createLastGrabber()

    grabbers.enumerated().forEach({(index, grabber) in
      let next = index + 1
      if  next < grabbers.count {
        grabber()?.adjacentGrabber = grabbers[index + 1]()
      }
    })
    multiColumnTableView.grabbers = grabbers
  }

  func createLastGrabber() {
    if dataColumns.count > 1 {
      let resizer = LastColumnResizer(columnWidths, index: columnWidths.count() - 2, bindTo: multiColumnTableView)
      resizer.horizontalScrollView = horizontalScrollView
      resizer.translatesAutoresizingMaskIntoConstraints = false
      resizer.borderColor = ColorParser.fromCSS(cssString: containerView.tableTheme?.borderBackgroundColor ?? "lightgray")
      resizer.containerView = containerView
      resizer.totalsView = multiColumnTableView.totalView
      resizer.headerView = multiColumnTableView.headerView
      resizer.selectionsBand = multiColumnTableView.dataCollectionView?.selectionBand
      grabbers.append({[weak resizer] in return resizer})
      let width = columnWidths.getTotalWidth(range: 1..<columnWidths.count())
      horizontalScrollView.addSubview(resizer)
      resizer.centerConstraint = resizer.centerXAnchor.constraint(equalTo: multiColumnTableView.leadingAnchor, constant: width)
      let constraints = [
        resizer.topAnchor.constraint(equalTo: multiColumnTableView.topAnchor),
        resizer.bottomAnchor.constraint(equalTo: multiColumnTableView.bottomAnchor),
        resizer.widthAnchor.constraint(equalToConstant: TableTheme.DefaultResizerWidth),
        resizer.centerConstraint
      ]
      NSLayoutConstraint.activate(constraints)
      horizontalScrollView.addConstraints(constraints)
      multiColumnTableView.lastGrabber = resizer
    }
    coupleCollectionViews()
  }

  func coupleCollectionViews() {
    firstColumnTableView.dataCollectionView?.coupled = multiColumnTableView.dataCollectionView
    multiColumnTableView.dataCollectionView?.coupled = firstColumnTableView.dataCollectionView

    createTotalRowsCount()
  }

  func createTotalRowsCount() {
    let totalRows = TotalCellsView(withShadow: true)
    containerView.addSubview(totalRows)
    totalRows.translatesAutoresizingMaskIntoConstraints = false
    let constraints = [
      totalRows.leadingAnchor.constraint(equalTo: containerView.leadingAnchor),
      totalRows.trailingAnchor.constraint(equalTo: containerView.trailingAnchor),
      totalRows.topAnchor.constraint(equalTo: containerView.bottomAnchor, constant: -TableTheme.TotalRowViewHeight),
      totalRows.bottomAnchor.constraint(equalTo: containerView.bottomAnchor)
    ]
    NSLayoutConstraint.activate(constraints)
    containerView.addConstraints(constraints)
    totalRows.totalRows = containerView.dataSize?.qcy ?? 0
    firstColumnTableView.dataCollectionView?.totalCellsView = totalRows

  }

  func createHeader(forTable table: TableView, withRange: CountableRange<Int>) {
    let header = HeaderView(columnWidths: columnWidths,
                            withRange: withRange,
                            isDataView: containerView.isDataView,
                            onHeaderPressed: containerView.onHeaderPressed,
                            onSearchColumn: containerView.onSearchColumn)
    let headerHeight = containerView.headerStyle?.lineHeight ?? TableTheme.CellContentHeight
    header.translatesAutoresizingMaskIntoConstraints = false
    header.backgroundColor = ColorParser.fromCSS(cssString: containerView.tableTheme?.headerBackgroundColor
                                                 ?? "lightgray")
    header.dynamicHeightAnchor = header.heightAnchor.constraint(equalToConstant: headerHeight + (PaddedLabel.PaddingSize * 2.0))

    header.addLabels(columns: dataColumns, headerStyle: containerView.headerStyle?.headerContentStyle)
    table.addSubview(header)
    table.headerView = header
    let constraints = [
      header.leadingAnchor.constraint(equalTo: table.leadingAnchor),
      header.topAnchor.constraint(equalTo: table.topAnchor),
      header.trailingAnchor.constraint(equalTo: table.trailingAnchor),
      header.dynamicHeightAnchor
    ]

    NSLayoutConstraint.activate(constraints)
    table.addConstraints(constraints)
  }

  func fitDataCollectionView(_ dataCollectionView: DataCollectionView, on tableView: TableView, withRane range: CountableRange<Int>) {
    dataCollectionView.translatesAutoresizingMaskIntoConstraints = false

    tableView.addSubview(dataCollectionView)
    tableView.dataCollectionView = dataCollectionView
    tableView.columnWidths = columnWidths
    var topAnchor = NSLayoutConstraint()
    var bottomAnchor = NSLayoutConstraint()
    let position = totals?.position ?? ""

    if let totalsView = tableView.totalView, let header = tableView.headerView {
      if position == "bottom" {
        topAnchor = dataCollectionView.topAnchor.constraint(equalTo: header.bottomAnchor)
        bottomAnchor = dataCollectionView.bottomAnchor.constraint(equalTo: totalsView.topAnchor)
      } else {
        topAnchor = dataCollectionView.topAnchor.constraint(equalTo: totalsView.bottomAnchor)
        bottomAnchor = dataCollectionView.bottomAnchor.constraint(equalTo: tableView.bottomAnchor)
      }
    } else if let header = tableView.headerView {
      topAnchor = dataCollectionView.topAnchor.constraint(equalTo: header.bottomAnchor)
      bottomAnchor = dataCollectionView.bottomAnchor.constraint(equalTo: tableView.bottomAnchor)
    }

    let constraints = [
      dataCollectionView.leadingAnchor.constraint(equalTo: tableView.leadingAnchor),
      dataCollectionView.trailingAnchor.constraint(equalTo: tableView.trailingAnchor),
      topAnchor,
      bottomAnchor
    ]
    NSLayoutConstraint.activate(constraints)
    tableView.addConstraints(constraints)
  }

  func createTotalsView(for tableView: TableView, withRange range: CountableRange<Int>, first: Bool) {
    if containerView.isDataView {
      return
    }

    if let totals = totals, let headerView = tableView.headerView {
      let totalsColView = TotalsView(withTotals: totals, dataColumns: dataColumns, cellStyle: containerView.cellStyle, columnWidths: columnWidths, withRange: range)
      let height = containerView.cellStyle?.lineHeight ?? TableTheme.CellContentHeight
      totalsColView.translatesAutoresizingMaskIntoConstraints = false
      totalsColView.dynamicHeight = totalsColView.heightAnchor.constraint(equalToConstant: height + (PaddedLabel.PaddingSize * 2.0))
      totalsColView.isFirstColumn = first
      totalsColView.backgroundColor = ColorParser.fromCSS(cssString: containerView.tableTheme?.backgroundColor ?? "white")
      tableView.addSubview(totalsColView)
      tableView.totalView = totalsColView
      var constraints = [
        totalsColView.leadingAnchor.constraint(equalTo: tableView.leadingAnchor),
        totalsColView.trailingAnchor.constraint(equalTo: tableView.trailingAnchor),
        totalsColView.dynamicHeight
      ]

      if totals.position == "bottom" {
        constraints.append(totalsColView.bottomAnchor.constraint(equalTo: tableView.bottomAnchor))
      } else {
        constraints.append(totalsColView.topAnchor.constraint(equalTo: headerView.bottomAnchor))
      }
      NSLayoutConstraint.activate(constraints)
      tableView.addConstraints(constraints)
      totalsColView.layer.zPosition = 1
    }
  }

  func wireDataCollectionView(_ dataCollectionView: DataCollectionView) {
    dataCollectionView.onEndReached = containerView.onEndReached
    dataCollectionView.dataSize = containerView.dataSize
    dataCollectionView.isDataView = containerView.isDataView
    dataCollectionView.freezeFirstColumn = containerView.freezeFirstColumn
    dataCollectionView.menuTranslations = containerView.menuTranslations
    dataCollectionView.onExpandedCell = containerView.onExpandCell
  }

  func addShadowsToHeadersIfNeeded() {
    let position = totals?.position ?? "none"
    if firstColumnTableView.totalView == nil || position == "bottom" {
      if let firstHeader = firstColumnTableView.headerView, let secondHeader = multiColumnTableView.headerView {
        firstHeader.hasShadow = true
        secondHeader.hasShadow = true
        firstHeader.layer.zPosition = 1
        secondHeader.layer.zPosition = 1
      }
    }
  }

  func finalizeSetup() {
    firstColumnTableView.adjacentTable = multiColumnTableView
    firstColumnTableView.layer.zPosition = 2
    horizontalScrollView.bringSubviewToFront(firstColumnTableView)
    if let lastGrabber = multiColumnTableView.lastGrabber {
      multiColumnTableView.bringSubviewToFront(lastGrabber)
      lastGrabber.layer.zPosition = 2
    }

    if let firstGrabber = firstColumnTableView.firstGrabber {
      firstGrabber.superview?.bringSubviewToFront(firstGrabber)
    }
    if dataColumns.count < 2 {
      firstColumnTableView.dataCollectionView?.childCollectionView?.showsVerticalScrollIndicator = true
    } else {
      multiColumnTableView.dataCollectionView?.childCollectionView?.firstColumnTable = firstColumnTableView
      firstColumnTableView.dataCollectionView?.childCollectionView?.showsVerticalScrollIndicator = false
    }
  }
}
