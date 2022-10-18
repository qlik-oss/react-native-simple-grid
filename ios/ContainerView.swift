//
//  ContainerView.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-14.
//

import Foundation
@objc
class ContainerView: UIView {
  var tableTheme: TableTheme?
  var dataSize: DataSize?
  var dataColumns: [DataColumn]?
  var dataRows: [DataRow]?
  var totals: Totals?
  let selectionsEngine = SelectionsEngine()
  let hScrollViewDelegate = HorizontalScrollViewDelegate()
  var needsGrabbers = true
  var cellStyle: CellContentStyle?
  var headerStyle: HeaderContentStyle?
  var defaultCalculated = false
  var menuTranslations: MenuTranslations?
  var columnWidths = ColumnWidths()
  var grabbers = [() -> GrabberView?]()
  weak var mainHeaderView: HeaderView?
  weak var primaryHeaderView: HeaderView?
  weak var secondaryHeaderView: HeaderView?
  weak var primaryCollectionView: MasterColumnCollectionView?
  weak var secondaryCollectionView: DataCollectionView?
  weak var scrollView: UIScrollView?
  weak var rootView: UIView?
  weak var primaryTotalsView: TotalsView?
  weak var secondaryTotalsView: TotalsView?
  weak var totalCellsView: TotalCellsView?
  weak var guideLineView: GuideLineView?
  weak var primarySelectionBand: SelectionBand?

  @objc var onEndReached: RCTDirectEventBlock?
  @objc var onVerticalScrollEnded: RCTDirectEventBlock?
  @objc var onHeaderPressed: RCTDirectEventBlock?
  @objc var containerWidth: NSNumber?
  @objc var freezeFirstColumn: Bool = false
  @objc var isDataView: Bool = false

  override init(frame: CGRect) {
    super.init(frame: frame)
    let rootView = UIView(frame: frame)
    fillParentView(rootView, toParent: self)

    self.addSubview(rootView)
    self.rootView = rootView
  }

  func fillParentView(_ view: UIView, toParent parent: UIView) {
    view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
  }

  required init?(coder: NSCoder) {
    super.init(coder: coder)
  }

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
        dataColumns = decodedCols.header
        totals = decodedCols.totals

        if let primaryTotalsView = primaryTotalsView {
          primaryTotalsView.resetTotals(totals)
        }

        if let secondaryTotalsView = secondaryTotalsView {
          secondaryTotalsView.resetTotals(totals)
        }

        if let dataColumns = dataColumns {
          if let primaryHeaderView = primaryHeaderView {
            primaryHeaderView.updateColumns(dataColumns)
          }
          if let secondaryHeaderView = secondaryHeaderView {
            secondaryHeaderView.updateColumns(dataColumns)
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
        if dataRows == nil || decodedRows.reset == true {
          dataRows = decodedRows.rows

          if let view = primaryCollectionView {
            view.appendData(rows: dataRows!)
            view.scrollToTop()
          }

          if let slave = secondaryCollectionView {
            slave.appendData(rows: dataRows!)
            slave.scrollToTop()
          }

        } else {
          if let newRows = decodedRows.rows {
            dataRows!.append(contentsOf: newRows)
            if let primaryCollectionView = primaryCollectionView {
              primaryCollectionView.appendData(rows: dataRows!)
            }
            if let secondaryCollectionView = secondaryCollectionView {
              secondaryCollectionView.appendData(rows: dataRows!)
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
        cellStyle = decodedCellStyle
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
        headerStyle = decodedHeaderStyle
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
      guard let dataRows = dataRows else {
        return
      }

      if let dataColumns = dataColumns {
        columnWidths.loadDefaultWidths(bounds, columnCount: dataColumns.count, dataRows: dataRows)
      }

      createHScrollView()
      createHeaderView()
      createTotalsView()
      createDataCollectionView()
      createGrabbers()
      createTotalCellsView()

      guard let collectionView = primaryCollectionView else {
        return
      }

      DispatchQueue.main.async {
        collectionView.initialSignalVisibleRows()
      }
    }
  }

  override func layoutSubviews() {
    super.layoutSubviews()

    if let headerView = secondaryHeaderView {
      headerView.resizeLabels()
    }

    if let totalsView = secondaryTotalsView {
      totalsView.resizeLabels(withFrame: self.frame)
    }

    if let guideLineView = guideLineView {
      guideLineView.resize(withFrame: self.frame)
    }
    updateHeadersView()
    updateTotalsView()
    updateHScrollFrame()
    updateCollectionViewFrame()
    repositionGrabbers()
    repositionTotalCellsView()
  }

  fileprivate func updateHeadersView() {
    guard let masterHeaderView = primaryHeaderView else {return}
    let width = columnWidths.getTotalWidth(range: 0..<1)
    let frame = CGRect(x: 0, y: 0, width: width, height: Double(tableTheme!.headerHeight!))

    // explicit call to resizeLabels
    masterHeaderView.frame = frame
    masterHeaderView.resizeLabels()

    if let slaveHeaderView = secondaryHeaderView {
      slaveHeaderView.frame = getSlaveHeaderFrame()
      slaveHeaderView.resizeLabels()
    }
  }

  fileprivate func updateTotalsView() {
    guard let headerView = primaryHeaderView else { return }
    guard let totals = totals else { return }
    guard let masterTotalsView = self.primaryTotalsView else {return}
    let y = totals.position == "bottom" ? self.frame.height - headerView.frame.height * 2  : headerView.frame.height
    let frame = CGRect(x: 0.0, y: y, width: columnWidths.getTotalWidth(range: 0..<1), height: headerView.frame.height)
    masterTotalsView.frame = frame

    if let slaveTotalsView = secondaryTotalsView {
      var frame = getSlaveHeaderFrame()
      frame.origin.y = totals.position == "bottom" ? self.frame.height - headerView.frame.height * 2  : headerView.frame.height
      slaveTotalsView.frame = frame

    }
  }

  fileprivate func createHScrollView() {
    guard let rootView = rootView else {
      return
    }

    if scrollView == nil {
      let scrollView = UIScrollView(frame: getHScrollViewFrame())
      scrollView.delegate = freezeFirstColumn ? hScrollViewDelegate : nil
      rootView.addSubview(scrollView)
      fillParentView(scrollView, toParent: rootView)
      self.scrollView = scrollView
    }
  }

  fileprivate func getHScrollViewFrame() -> CGRect {
    var width = 0.0
    var x = 0.0
    if freezeFirstColumn {
      let firstColumnWidth = columnWidths.getTotalWidth(range: 0..<1)
      width = self.frame.width - firstColumnWidth
      x = firstColumnWidth
    } else {
      width = self.frame.width
    }

    return CGRect(x: x, y: 0, width: width, height: self.frame.height)
  }

  fileprivate func updateHScrollFrame() {
    guard let scrollView = scrollView else {return}
    scrollView.frame = getHScrollViewFrame()
    updateHScrollContentSize()
  }

  fileprivate func updateHScrollContentSize() {
    guard let scrollView = scrollView else {
      return
    }

    let width = freezeFirstColumn ? columnWidths.getTotalWidth(range: 1..<columnWidths.count()) : columnWidths.getTotalWidth()
    scrollView.contentSize = CGSize(width: width + HorizontalScrollValues.RightScrollContentPadding.rawValue, height: 0)
  }

  fileprivate func updateCollectionViewFrame() {
    guard let primaryCollectionView = primaryCollectionView else { return }

    let collectionViewHeight = getCollectionViewHeight()
    let top = getCollectionViewTop()
    let firstColumnWidth = columnWidths.getTotalWidth(range: 0..<1)
    let slaveWidth = columnWidths.getTotalWidth(range: 1..<columnWidths.count())

    let primaryFrame =  CGRect(x: 0, y: top, width: firstColumnWidth, height: collectionViewHeight)
    primaryCollectionView.resizeCells(withFrame: primaryFrame)

    if let secondaryCollectionView = secondaryCollectionView {
      let x = freezeFirstColumn ? 0 : firstColumnWidth
      let slaveFrame =  CGRect(x: x, y: top, width: slaveWidth, height: collectionViewHeight)
      secondaryCollectionView.resizeCells(withFrame: slaveFrame)
    }

  }

  fileprivate func createHeaderView() {
    guard let scrollView = scrollView else {
      return
    }

    guard let dataColumns = dataColumns else {
      return
    }

    if primaryHeaderView == nil {
      let width = columnWidths.getTotalWidth(range: 0..<1)
      let frame = CGRect(x: 0, y: 0, width: width, height: Double(tableTheme!.headerHeight!))
      let newHeaderView = HeaderView(frame: frame,
                                     columns: dataColumns,
                                     withTheme: tableTheme!,
                                     onHeaderPressed: onHeaderPressed,
                                     headerStyle: headerStyle!,
                                     columnWidths: columnWidths,
                                     withRange: 0..<1)
      newHeaderView.backgroundColor = ColorParser().fromCSS(cssString: tableTheme?.headerBackgroundColor ?? "lightgray")
      if freezeFirstColumn {
        rootView?.addSubview(newHeaderView)
      } else {
        scrollView.addSubview(newHeaderView)
      }
      primaryHeaderView = newHeaderView
      hScrollViewDelegate.headersView = newHeaderView
    }

    if secondaryHeaderView == nil {
      let frame = getSlaveHeaderFrame()
      let newHeaderView = HeaderView(frame: frame,
                                     columns: dataColumns,
                                     withTheme: tableTheme!,
                                     onHeaderPressed: onHeaderPressed,
                                     headerStyle: headerStyle!,
                                     columnWidths: columnWidths,
                                     withRange: 1..<columnWidths.count())
      newHeaderView.backgroundColor = ColorParser().fromCSS(cssString: tableTheme?.headerBackgroundColor ?? "lightgray")
      scrollView.addSubview(newHeaderView)
      secondaryHeaderView = newHeaderView
      updateHScrollContentSize()
    }
  }

  fileprivate func createTotalsView() {
    guard let tableTheme = tableTheme else {
      return
    }

    guard let totals = totals else {
      return
    }

    guard let dataColumns = dataColumns else {
      return
    }

    guard let cellStyle = cellStyle else {
      return
    }

    guard let scrollView = scrollView else {
      return
    }

    guard let headerView = primaryHeaderView else {
      return
    }

    if isDataView {
      return
    }

    if self.primaryTotalsView == nil {
      let y = totals.position == "bottom" ? self.frame.height - headerView.frame.height * 2  : headerView.frame.height
      let frame = CGRect(x: 0.0, y: y, width: columnWidths.getTotalWidth(range: 0..<1), height: headerView.frame.height)
      let view = TotalsView(frame: frame,
                            withTotals: totals,
                            dataColumns: dataColumns,
                            theme: tableTheme,
                            cellStyle: cellStyle,
                            columnWidths: self.columnWidths,
                            withRange: 0..<1)
      if freezeFirstColumn {
        rootView?.addSubview(view)
      } else {
        scrollView.addSubview(view)
      }
      primaryTotalsView = view
      hScrollViewDelegate.totalsView = view
    }

    if self.secondaryTotalsView == nil {
      var frame = getSlaveHeaderFrame()
      frame.origin.y = totals.position == "bottom" ? self.frame.height - headerView.frame.height * 2  : headerView.frame.height
      let view = TotalsView(frame: frame,
                            withTotals: totals,
                            dataColumns: dataColumns,
                            theme: tableTheme,
                            cellStyle: cellStyle,
                            columnWidths: self.columnWidths,
                            withRange: 1..<columnWidths.count())
      scrollView.addSubview(view)
      self.secondaryTotalsView = view
    }
  }

  fileprivate func getSlaveHeaderFrame() -> CGRect {
    let width = columnWidths.getTotalWidth(range: 1..<columnWidths.count())
    let x = freezeFirstColumn ? 0.0 : columnWidths.getTotalWidth(range: 0..<1)
    return CGRect(x: x, y: 0, width: width, height: Double(tableTheme!.headerHeight!))
  }

  fileprivate func createDataCollectionView() {
    guard let scrollView = scrollView else {
      return
    }
    guard let dataRows = dataRows else {
      return
    }

    guard let dataColumns = dataColumns else {
      return
    }

    guard let tableTheme = tableTheme else {
      return
    }

    let collectionViewHeight = getCollectionViewHeight()
    let top = getCollectionViewTop()
    let firstColumnWidth = columnWidths.getTotalWidth(range: 0..<1)
    let slaveWidth = columnWidths.getTotalWidth(range: 1..<columnWidths.count())

    if primaryCollectionView == nil {

      let dataCollectionView = MasterColumnCollectionView(frame: CGRect(x: 0, y: top, width: firstColumnWidth, height: collectionViewHeight),
                                                          withRows: dataRows,
                                                          andColumns: dataColumns,
                                                          theme: tableTheme,
                                                          selectionsEngine: selectionsEngine,
                                                          cellStyle: cellStyle!,
                                                          columnWidths: columnWidths)
      dataCollectionView.onEndReached = self.onEndReached
      dataCollectionView.dataSize = self.dataSize
      dataCollectionView.backgroundColor = ColorParser().fromCSS(cssString: tableTheme.headerBackgroundColor ?? "lightgray")
      dataCollectionView.isDataView = self.isDataView
      dataCollectionView.headerView = self.primaryHeaderView
      dataCollectionView.totalsView = self.primaryTotalsView
      dataCollectionView.freezeFirstColumn = self.freezeFirstColumn
      dataCollectionView.menuTranslations = self.menuTranslations
      hScrollViewDelegate.collectionView = dataCollectionView

      if let masterHeaderView = primaryHeaderView {
        masterHeaderView.autoresizingMask = [.flexibleWidth]
      }
      if let masterTotalsView = primaryTotalsView {
        masterTotalsView.autoresizingMask = [.flexibleWidth]
      }

      if freezeFirstColumn {
        rootView?.addSubview(dataCollectionView)
      } else {
        scrollView.addSubview(dataCollectionView)
      }
      primaryCollectionView = dataCollectionView
    }

    if secondaryCollectionView == nil {
      let x = freezeFirstColumn ? 0 : firstColumnWidth
      let collectionView = DataCollectionView(frame: CGRect(x: x, y: top, width: slaveWidth, height: collectionViewHeight),
                                              withRows: dataRows,
                                              andColumns: dataColumns,
                                              theme: tableTheme,
                                              selectionsEngine: selectionsEngine,
                                              cellStyle: cellStyle!,
                                              columnWidths: columnWidths,
                                              range: 1..<columnWidths.count())

      collectionView.dataSize = self.dataSize
      collectionView.backgroundColor = ColorParser().fromCSS(cssString: tableTheme.headerBackgroundColor ?? "lightgray")
      collectionView.onEndReached = self.onEndReached
      collectionView.isDataView = self.isDataView
      collectionView.headerView = self.secondaryHeaderView
      collectionView.totalsView = self.secondaryTotalsView
      collectionView.freezeFirstColumn = self.freezeFirstColumn
      collectionView.menuTranslations = self.menuTranslations
      collectionView.hScrollView = scrollView
      collectionView.backgroundColor = .red

      scrollView.addSubview(collectionView)
      secondaryCollectionView = collectionView
      if let masterCollectionView = primaryCollectionView {
        masterCollectionView.slave = collectionView
        collectionView.slave = masterCollectionView
      }
    }

  }

  fileprivate func getCollectionViewFrame() -> CGRect {

    let width = Int(columnWidths.getTotalWidth(range: 0..<1))

    let headerHeight = tableTheme?.headerHeight ?? 54
    var y = headerHeight
    var totalHeight = self.frame.height - (CGFloat(headerHeight) * 2) // 2 is header + totals
    if secondaryTotalsView != nil {
      totalHeight -= CGFloat(headerHeight)
      if let totals = totals {
        if totals.position != "bottom" {
            y += headerHeight
        }
      }
    }
    return CGRect(x: 0, y: y, width: width, height: Int(totalHeight))
  }

  fileprivate func getCollectionViewHeight() -> Double {
    let headerHeight = tableTheme?.headerHeight ?? 54
    var totalHeight = self.frame.height - (CGFloat(headerHeight) * 2) // 2 is header + totals
    if secondaryTotalsView != nil {
      totalHeight -= CGFloat(headerHeight)
    }
    return totalHeight
  }

  fileprivate func getCollectionViewTop() -> Double {
    let headerHeight = tableTheme?.headerHeight ?? 54
    var y = headerHeight
    if let totals = totals {
      if totals.position != "bottom" {
          y += headerHeight
      }
    }
    return Double(y)

  }

  fileprivate func createTotalCellsView() {
    if totalCellsView == nil {
      guard let height = tableTheme?.headerHeight else { return }
      let frame = CGRect(x: 0, y: self.frame.height - CGFloat(height), width: self.frame.width, height: CGFloat(height))
      let withShadow = totals?.position != "bottom"
      let view = TotalCellsView(frame: frame, withShadow: withShadow)
      view.backgroundColor = .white
      view.createTextView()
      addSubview(view)
      self.totalCellsView = view
      primaryCollectionView?.totalCellsView = view
      if let dataSize = dataSize {
        view.totalRows = dataSize.qcy ?? 0
        if let collectionView = primaryCollectionView {
          DispatchQueue.main.async {
            collectionView.signalVisibleRows()
          }
        }
      }
    }
  }

  fileprivate func createGrabbers() {
    guard let scrollView = scrollView else {
      return
    }

    if needsGrabbers {
      needsGrabbers = false
      let guideLineView = GuideLineView(frame: self.frame, containerWidth: Int(self.frame.width))
      scrollView.addSubview(guideLineView)
      self.guideLineView = guideLineView
      if let tableTheme = tableTheme {
        var startX: Double = -20 // half the width of the grabber
        var colIdx = 0
        // first check wich one to add
        let firstWidth = columnWidths.columnWidths[0]
        let firstGrabber = createGrabber(width: columnWidths.columnWidths[0], startX: startX, colIdx: colIdx, tableTheme: tableTheme, isMaster: true, range: 0..<1)
        if freezeFirstColumn {
          rootView?.addSubview(firstGrabber)
        } else {
          scrollView.addSubview(firstGrabber)
          startX += firstWidth
        }

        grabbers.append({[weak firstGrabber] in return firstGrabber})
        colIdx += 1

        let range = 1..<columnWidths.count()
        let widths = columnWidths.columnWidths[range]
        for width in widths {
          let grabber = createGrabber(width: width, startX: startX, colIdx: colIdx, tableTheme: tableTheme, isMaster: false, range: range)
          scrollView.addSubview(grabber)
          grabbers.append({[weak grabber] in return grabber})
          startX += width
          colIdx += 1
        }

        if let slaveCollectionView = secondaryCollectionView {
          slaveCollectionView.grabbers = grabbers
        }
      }
    }
  }

  fileprivate func createGrabber(width: Double,
                                 startX: Double,
                                 colIdx: Int,
                                 tableTheme: TableTheme,
                                 isMaster: Bool,
                                 range: CountableRange<Int>) -> GrabberView {
    let x = width + startX
    let frame = CGRect(x: x, y: 0, width: 40, height: self.frame.height)
    let grabber = GrabberView(frame: frame, index: Double(colIdx), theme: tableTheme, withRange: range)
    grabber.isLast = colIdx == columnWidths.columnWidths.count - 1
    grabber.collectionView = isMaster ? self.primaryCollectionView : self.secondaryCollectionView
    grabber.containerView = self
    grabber.headerView = isMaster ? self.primaryHeaderView : self.secondaryHeaderView
    grabber.totalsView = isMaster ? self.primaryTotalsView : self.secondaryTotalsView
    grabber.scrollView = self.scrollView
    grabber.guideLineView = guideLineView
    grabber.primarySelectionBand = primaryCollectionView?.selectionBand
    grabber.secondarySelectionBand = secondaryCollectionView?.selectionBand
    return grabber
  }

  func updateSize(_ index: Int) {
    resizeFrame(index, updateContent: false)
  }

  func onEndDragged(_ index: Int) {
    updateHScrollContentSize()
    columnWidths.saveToStorage()
  }

  fileprivate func repositionGrabbers() {
    if grabbers.count > 0 {
      var startX: Double = 0.0
      let firstWidth = columnWidths.columnWidths[0]
      let x = firstWidth + startX
      grabbers[0]()?.repositionTo(x)
      if !freezeFirstColumn {
        startX += firstWidth
      }

      let range = 1..<columnWidths.count()
      grabbers[range].enumerated().forEach({(index, grabber) in
        let width = columnWidths.columnWidths[index + 1]
        let x = width + startX
        grabber()?.repositionTo(x)
        startX += width
      })
    }
  }

  fileprivate func repositionTotalCellsView() {
    guard let totalCellsView = totalCellsView else { return }
    guard let height = tableTheme?.headerHeight else { return }
    let frame = CGRect(x: 0, y: self.frame.height - (CGFloat(height)), width: self.frame.width, height: CGFloat(height))
    totalCellsView.frame = frame
  }

  fileprivate func resizeFrame(_ index: Int, updateContent update: Bool) {
    guard let dataColumns = dataColumns else { return }
    if index + 1 == dataColumns.count {
      if dataColumns.count > 1 {
        if let slaveCollectionView = secondaryCollectionView {
          slaveCollectionView.resizeLastCell()
        }
      }
    }
  }
}
