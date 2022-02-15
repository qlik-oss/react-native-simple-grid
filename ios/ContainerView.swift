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
  var totals: [TotalsCell]?
  let selectionsEngine = SelectionsEngine()
  var needsGrabbers = true
  weak var headerView: HeaderView?
  weak var collectionView: DataCollectionView?
  weak var scrollView: UIScrollView?
  weak var rootView: UIView?
  weak var overlayView: OverlayView?
  weak var footerView: FooterView?

  @objc var onEndReached: RCTDirectEventBlock?
  @objc var onColumnsResized: RCTDirectEventBlock?
  @objc var onHeaderPressed: RCTDirectEventBlock?
  @objc var onDoubleTap: RCTDirectEventBlock?

  @objc var containerWidth: NSNumber?

  override init(frame: CGRect) {
    super.init(frame: frame)
    let doubleTapGesture = ShortTapGesture(target: self, action: #selector(handleDoubleTap(_:)))
    doubleTapGesture.numberOfTapsRequired = 2
    addGestureRecognizer(doubleTapGesture)
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
        totals = decodedCols.footer
        if let footerView = footerView {
          footerView.resetTotals(totals)
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

          if let view = collectionView {
            view.appendData(rows: dataRows!)
            view.scrollToTop()
          }
        } else {
          if let newRows = decodedRows.rows {
            dataRows!.append(contentsOf: newRows)
            if let view = collectionView {
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

  override func layoutSubviews() {
    super.layoutSubviews()

    calculateDefaultColWidth()
    createHeaderView()
    createFooterView()
    createDataCollectionView()
    createGrabbers()
  }

  func calculateDefaultColWidth() {
      var resized = false
      if var dataColumns = dataColumns {
        let columns = dataColumns.count - 1
        for i in 0...columns {
          if dataColumns[i].width == 0 {
            dataColumns[i].width = calculateAverageWidthForColumn(i)
            resized = true
           }
         }
        self.dataColumns = dataColumns

        if resized {
          signalOnColumnsResized()
        }
      }
    }

  func calculateAverageWidthForColumn(_ i: Int) -> Double {
    if let dataRows = dataRows {
      let totalCount = dataRows.reduce(0) { partialResult, row in
        return partialResult + row.cells[i].qText!.count
      }
      let average = totalCount / dataRows.count
      let tempLabel = UILabel()
      tempLabel.text = String(repeating: "M", count: average)
      tempLabel.sizeToFit()
      let newWidth = max(tempLabel.frame.width + (Double(PaddedLabel.PaddingSize) * 2.5), DataCellView.minWidth)
      return newWidth
    }
    return 100
  }

  func signalOnColumnsResized() {
    if let onColumnsResized = onColumnsResized, let dataColumns = dataColumns {
      let widths = dataColumns.map {$0.width}
      onColumnsResized(["widths": widths])
    }
  }

  fileprivate func decorate(view: UIView) {
    view.layer.cornerRadius = CGFloat(tableTheme?.borderRadius ?? 8)
    view.layer.masksToBounds = true
  }

  fileprivate func createHeaderView() {
    if  headerView == nil {
      let newHeaderView = HeaderView(columns: dataColumns!, withTheme: tableTheme!, onHeaderPressed: onHeaderPressed)
      newHeaderView.backgroundColor = ColorParser().fromCSS(cssString: tableTheme?.headerBackgroundColor ?? "lightgray")
      if overlayView == nil {
        let overlayFrame = CGRect(x: 0, y: 0, width: newHeaderView.frame.width + 50, height: self.frame.height)
        let overlayView = OverlayView(frame: overlayFrame, containerWidth: containerWidth?.intValue ?? Int(self.frame.width))
        addSubview(overlayView)
        self.overlayView = overlayView
      }
      if rootView == nil {
        let frame = CGRect(x: 0, y: 0, width: newHeaderView.frame.width, height: self.frame.height)
        let newRootView = UIView(frame: frame)
        overlayView?.addSubview(newRootView)
        newRootView.addSubview(newHeaderView)
        let scrollView = UIScrollView(frame: self.frame)
        scrollView.indicatorStyle = .black
        scrollView.contentSize = CGSize(width: newHeaderView.frame.width + 25, height: self.frame.height)
        addSubview(scrollView)
        scrollView.addSubview(overlayView!)
        rootView = newRootView
        self.scrollView = scrollView
        decorate(view: newRootView)
        headerView = newHeaderView
      }
    }
  }

  fileprivate func createFooterView() {
    if let totals = totals {
      guard let height = tableTheme?.headerHeight else { return }
      let frame = CGRect(x: 0, y: self.frame.height - CGFloat(height), width: headerView?.frame.width ?? self.frame.width, height: CGFloat(height))
      let footerView = FooterView(frame: frame, withTotals: totals, dataColumns: dataColumns!, theme: tableTheme!)
      footerView.backgroundColor = ColorParser().fromCSS(cssString: tableTheme?.headerBackgroundColor ?? "white" )
      rootView?.addSubview(footerView)
      self.footerView = footerView
    }

  }

  fileprivate func createDataCollectionView() {
    if collectionView == nil {
      let width = Int(headerView?.frame.width ?? frame.width)
      let height = tableTheme?.headerHeight ?? 54
      var totalHeight = self.frame.height - CGFloat(height)
      if footerView != nil {
        totalHeight -= CGFloat(height)
      }

      let frame = CGRect(x: 0, y: height, width: width, height: Int(totalHeight))
      let dataCollectionView = DataCollectionView(frame: frame, withRows: dataRows!, andColumns: dataColumns!, theme: tableTheme!, selectionsEngine: selectionsEngine)
      dataCollectionView.onEndReached = self.onEndReached
      dataCollectionView.onColumnsResized = self.onColumnsResized
      dataCollectionView.dataSize = self.dataSize
      dataCollectionView.backgroundColor = ColorParser().fromCSS(cssString: tableTheme?.headerBackgroundColor ?? "lightgray")

      collectionView = dataCollectionView
      rootView!.addSubview(dataCollectionView)
    }
  }

  fileprivate func createGrabbers() {
    if needsGrabbers {
      needsGrabbers = false
      if let cols = dataColumns, let tableTheme = tableTheme {
        var startX: Double = -20
        for col in cols {
          let x = col.width! + startX
          let frame = CGRect(x: x, y: 0, width: 40, height: self.frame.height)
          let grabber = GrabberView(frame: frame, index: col.dataColIdx!, theme: tableTheme)
          grabber.isLast = Int(col.dataColIdx!) == cols.count - 1
          grabber.collectionView = self.collectionView
          grabber.containerView = self
          grabber.headerView = self.headerView
          grabber.overlayView = self.overlayView
          grabber.footerView = self.footerView
          grabber.scrollView = self.scrollView
          overlayView!.addSubview(grabber)
          startX += col.width!
        }
      }
    }
  }

  func updateSize(_ index: Int) {
    resizeFrame(index, updateContent: false)
  }

  func onEndDragged(_ index: Int) {
    resizeFrame(index, updateContent: true)
  }

  fileprivate func resizeFrame(_ index: Int, updateContent update: Bool) {
    if index + 1 == dataColumns!.count {
      if let view = rootView, let cv = collectionView, let sv = scrollView, let hv = headerView, let ov = overlayView {
        let oldFrame = view.frame
        let newFrame = CGRect(x: 0, y: 0, width: cv.frame.width, height: oldFrame.height)
        view.frame = newFrame
        ov.frame = CGRect(x: 0, y: 0, width: cv.frame.width + 50, height: oldFrame.height)
        hv.frame = CGRect(x: 0, y: 0, width: cv.frame.width, height: CGFloat(tableTheme!.headerHeight!))
        if let fv = footerView {
          fv.frame = CGRect(x: 0, y: fv.frame.origin.y, width: cv.frame.width, height: CGFloat(tableTheme!.headerHeight!))
        }
        if update {
          sv.contentSize = CGSize(width: cv.frame.width + 50, height: newFrame.height)
        }
      }
    }
  }
}
