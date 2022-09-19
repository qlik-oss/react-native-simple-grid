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
  var needsGrabbers = true
  var cellStyle: CellContentStyle?
  var headerStyle: HeaderContentStyle?
  var defaultCalculated = false
  var columnWidths = ColumnWidths()
  var grabbers = [() -> GrabberView?]()
  weak var headerView: HeaderView?
  weak var masterCollectionView: MasterColumnCollectionView?
  weak var slaveCollectionView: DataCollectionView?
  weak var scrollView: UIScrollView?
  weak var rootView: UIView?
  weak var totalsView: TotalsView?
  weak var totalCellsView: TotalCellsView?
  weak var guideLineView: GuideLineView?
  
  @objc var onEndReached: RCTDirectEventBlock?
  @objc var onVerticalScrollEnded: RCTDirectEventBlock?
  @objc var onHeaderPressed: RCTDirectEventBlock?
  @objc var onDoubleTap: RCTDirectEventBlock?
  @objc var containerWidth: NSNumber?
  @objc var freezeFirstColumn: Bool = false
  @objc var isDataView: Bool = false
  @objc var isPercent: Bool = false
 
  
  override init(frame: CGRect) {
    super.init(frame: frame)
    let doubleTapGesture = ShortTapGesture(target: self, action: #selector(handleDoubleTap(_:)))
    doubleTapGesture.numberOfTapsRequired = 2
    addGestureRecognizer(doubleTapGesture)
    let rootView = UIView(frame: frame)
    fillParentView(rootView, toParent: self)
    
    self.addSubview(rootView)
    self.rootView = rootView
  }
  
  func fillParentView(_ view: UIView, toParent parent :UIView) {
    view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
  }
  
  required init?(coder: NSCoder) {
    super.init(coder: coder)
  }
  
  @objc func handleDoubleTap(_ sender: UITapGestureRecognizer) {
    if let onDoubleTap = onDoubleTap {
      onDoubleTap(["doubleTap": "here"])
    }
  }
  
  @objc var onSelectionsChanged: RCTDirectEventBlock? {
    didSet {
      selectionsEngine.onSelectionsChanged = onSelectionsChanged
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
        if let totalsView = totalsView {
          totalsView.resetTotals(totals)
        }
        if let headerView = headerView {
          headerView.updateColumns(dataColumns!)
        }
        
      } catch {
        print(error)
      }
    }
  }
  
  @objc var rows: NSDictionary = [:] {
    didSet {
      do {
        let json = try JSONSerialization.data(withJSONObject: rows)
        let decodedRows = try JSONDecoder().decode(RowsObject.self, from: json)
        if dataRows == nil || decodedRows.reset == true {
          dataRows = decodedRows.rows
          
          if let view = masterCollectionView {
            view.appendData(rows: dataRows!)
            view.scrollToTop()
          }
        } else {
          if let newRows = decodedRows.rows {
            dataRows!.append(contentsOf: newRows)
            if let view = masterCollectionView {
              view.appendData(rows: dataRows!)
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
//      createGrabbers()
      createTotalCellsView()
      
      guard let collectionView = masterCollectionView else {
        return
      }

      DispatchQueue.main.async {
        collectionView.initialSignalVisibleRows()
      }
    }
  }
  
  override func layoutSubviews() {
    super.layoutSubviews()
    
    guard let collectionView = masterCollectionView else {
      return
    }
    
    if let headerView = headerView {
      headerView.resizeLabels()
    }
    
    if let totalsView = totalsView {
      totalsView.resizeLabels(withFrame: self.frame)
    }
    
    if let guideLineView = guideLineView {
      guideLineView.resize(withFrame: self.frame)
    }
    updateHScrollContentSize()
//    collectionView.resizeCells(withFrame: getCollectionViewFrame())
    repositionGrabbers()
    repositionTotalCellsView()
  }
  
  fileprivate func createHScrollView() {
    guard let rootView = rootView else {
      return
    }
    
    if(scrollView == nil) {
      let scrollView = UIScrollView(frame: self.frame)
      rootView.addSubview(scrollView)
      fillParentView(scrollView, toParent: rootView)
      self.scrollView = scrollView
    }
  }
  
  fileprivate func createHeaderView() {
    guard let scrollView = scrollView else {
      return
    }
    
    guard let dataColumns = dataColumns else {
      return
    }
    
    if  headerView == nil {
      let newHeaderView = HeaderView(columns: dataColumns, withTheme: tableTheme!, onHeaderPressed: onHeaderPressed, headerStyle: headerStyle!, columnWidths: columnWidths)
      newHeaderView.backgroundColor = ColorParser().fromCSS(cssString: tableTheme?.headerBackgroundColor ?? "lightgray")
      scrollView.addSubview(newHeaderView)
      headerView = newHeaderView
      updateHScrollContentSize()
    }
  }
  
  fileprivate func updateHScrollContentSize() {
    guard let scrollView = scrollView else {
      return
    }
    
    let width = columnWidths.getTotalWidth()
    
    scrollView.contentSize = CGSize(width: width + 25, height: 0)
  }
  
  fileprivate func createTotalsView() {
    if self.totalsView == nil {
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
      
      guard let headerView = headerView else {
        return
      }
      
      if (isDataView) {
        return
      }
      
      let y = totals.position == "bottom" ? self.frame.height - headerView.frame.height * 2  : headerView.frame.height
      let frame = CGRect(x: 0.0, y: y, width: headerView.frame.width, height: headerView.frame.height)
      let view = TotalsView(frame: frame, withTotals: totals, dataColumns: dataColumns, theme: tableTheme, cellStyle: cellStyle, columnWidths: self.columnWidths)
      scrollView.addSubview(view)
      self.totalsView = view
    }
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
    
   
    let collectionViewHeight = getCollectionViewHeight();
    let top = getCollectionViewTop();
    let firstColumnWidth = columnWidths.getTotalWidth(range: 0..<1);
    let slaveWidth = columnWidths.getTotalWidth(range: 1..<columnWidths.count());
    
    if masterCollectionView == nil {
      
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
      
      scrollView.addSubview(dataCollectionView)
      masterCollectionView = dataCollectionView
    }
    
    if slaveCollectionView == nil {
      let collectionView = DataCollectionView(frame: CGRect(x: firstColumnWidth, y: top, width: slaveWidth, height: collectionViewHeight),
                                              withRows: dataRows,
                                              andColumns: dataColumns,
                                              theme: tableTheme,
                                              selectionsEngine: selectionsEngine,
                                              cellStyle: cellStyle!,
                                              columnWidths: columnWidths,
                                              range: 1..<columnWidths.count())
      
      collectionView.dataSize = self.dataSize
      collectionView.backgroundColor = ColorParser().fromCSS(cssString: tableTheme.headerBackgroundColor ?? "lightgray")
      collectionView.isDataView = self.isDataView
      
      scrollView.addSubview(collectionView)
      slaveCollectionView = collectionView
      if let masterCollectionView = masterCollectionView {
        masterCollectionView.slave = collectionView
        collectionView.slave = masterCollectionView
      }
    }
  }
  
  fileprivate func getCollectionViewFrame() -> CGRect {
    
    let width = Int(columnWidths.getTotalWidth(range: 0..<1))
        
    let headerHeight = tableTheme?.headerHeight ?? 54
    var y = headerHeight
//    let width = Int(headerView.frame.width)
    var totalHeight = self.frame.height - (CGFloat(headerHeight) * 2) // 2 is header + totals
    if totalsView != nil {
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
    
    let width = Int(columnWidths.getTotalWidth(range: 0..<1))
        
    let headerHeight = tableTheme?.headerHeight ?? 54
//    var y = headerHeight
//    let width = Int(headerView.frame.width)
    var totalHeight = self.frame.height - (CGFloat(headerHeight) * 2) // 2 is header + totals
    if totalsView != nil {
      totalHeight -= CGFloat(headerHeight)
    }
    return totalHeight
  }
  
  fileprivate func getCollectionViewTop() -> Double {
    let height = getCollectionViewHeight();
    let headerHeight = tableTheme?.headerHeight ?? 54
    var y = headerHeight;
    if let totals = totals {
      if totals.position != "bottom" {
          y += headerHeight
      }
    }
    return Double(y);
    
  }
  
  fileprivate func createTotalCellsView() {
    if(totalCellsView == nil) {
      guard let height = tableTheme?.headerHeight else { return }
      let frame = CGRect(x: 0, y: self.frame.height - CGFloat(height), width: self.frame.width, height: CGFloat(height))
      let withShadow = totals?.position != "bottom"
      let view = TotalCellsView(frame: frame, withShadow: withShadow)
      view.backgroundColor = .white
      view.createTextView()
      addSubview(view)
      self.totalCellsView = view
      masterCollectionView?.totalCellsView = view
      if let dataSize = dataSize {
        view.totalRows = dataSize.qcy ?? 0
        if let collectionView = masterCollectionView {
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
        var startX: Double = -20
        var colIdx = 0
        for width in columnWidths.columnWidths {
          let x = width + startX
          let frame = CGRect(x: x, y: 0, width: 40, height: self.frame.height)
          let grabber = GrabberView(frame: frame, index: Double(colIdx), theme: tableTheme)
          grabber.isLast = colIdx == columnWidths.columnWidths.count - 1
          grabber.collectionView = self.masterCollectionView
          grabber.containerView = self
          grabber.headerView = self.headerView
          grabber.totalsView = self.totalsView
          grabber.scrollView = self.scrollView
          grabber.guideLineView = guideLineView
          scrollView.addSubview(grabber)
          
          grabbers.append({[weak grabber] in return grabber})
          startX += width
          colIdx += 1
        }
      }
    }
  }
  
  func updateSize(_ index: Int) {
    resizeFrame(index, updateContent: false)
  }
  
  func onEndDragged(_ index: Int) {
    resizeFrame(index, updateContent: true)
    columnWidths.saveToStorage()
  }
  
  fileprivate func repositionGrabbers() {
    var startX: Double = 0
    grabbers.enumerated().forEach({(index, grabber) in
      let width = columnWidths.columnWidths[index]
      let x = width + startX
      grabber()?.repositionTo(x)
      startX += width
    })
  }
  
  fileprivate func repositionTotalCellsView() {
    guard let totalCellsView = totalCellsView else { return }
    guard let height = tableTheme?.headerHeight else { return }
    let frame = CGRect(x: 0, y: self.frame.height - (CGFloat(height)), width: self.frame.width, height: CGFloat(height))
    totalCellsView.frame = frame
  }
  
  fileprivate func resizeFrame(_ index: Int, updateContent update: Bool) {
    if index + 1 == dataColumns!.count {
      if let view = rootView, let cv = masterCollectionView, let sv = scrollView, let hv = headerView {
        let oldFrame = view.frame
        let newFrame = CGRect(x: 0, y: 0, width: cv.frame.width, height: oldFrame.height)
        hv.frame = CGRect(x: 0, y: 0, width: cv.frame.width, height: CGFloat(tableTheme!.headerHeight!))
        if let fv =  totalsView {
          fv.frame = CGRect(x: 0, y: fv.frame.origin.y, width: cv.frame.width, height: CGFloat(tableTheme!.headerHeight!))
        }
        if update {
          sv.contentSize = CGSize(width: cv.frame.width + 25, height: newFrame.height)
        }
      }
    }
  }
}
