//
//  ContainerView.swift
//  qlik-trial-react-native-straight-table
//
//  Created by Vittorio Cellucci on 2022-01-14.
//

import Foundation
@objc
class ContainerView : UIView {
  var tableTheme: TableTheme?
  var dataSize: DataSize?
  var dataColumns: [DataColumn]?
  var dataRows: [DataRow]?
  let selectionsEngine = SelectionsEngine()
  weak var headerView: HeaderView? = nil
  weak var collectionView: DataCollectionView? = nil
  weak var scrollView: UIScrollView? = nil
  weak var rootView: UIView? = nil
  weak var overlayView: OverlayView? = nil
  
  override init(frame: CGRect) {
    super.init(frame: frame)
  }
  
  required init?(coder: NSCoder) {
    super.init(coder: coder)
  }
  
  @objc var onEndReached: RCTDirectEventBlock?
  
  @objc var containerWidth: NSNumber?
  
  @objc var size: NSDictionary = [:] {
    didSet {
      do {
        let json = try JSONSerialization.data(withJSONObject: size)
        dataSize = try JSONDecoder().decode(DataSize.self, from: json)
      }
      catch{
        print(error)
      }
    }
  }
  
  @objc var theme: NSDictionary = [:] {
    didSet {
      do {
        let json = try JSONSerialization.data(withJSONObject: theme)
        tableTheme = try JSONDecoder().decode(TableTheme.self, from: json)
      }
      catch{
        print(error)
      }
    }
  }
  
  @objc var cols: NSDictionary = [:] {
    didSet{
      do {
        let json = try JSONSerialization.data(withJSONObject: cols)
        let decodedCols = try JSONDecoder().decode(Cols.self, from: json)
        dataColumns = decodedCols.header
      } catch {
        print(error)
      }
    }
  }
  
  @objc var rows: NSDictionary = [:] {
    didSet{
      do {
        let json = try JSONSerialization.data(withJSONObject: rows)
        let decodedRows = try JSONDecoder().decode(RowsObject.self, from: json)
        if (dataRows == nil || decodedRows.reset == true) {
          dataRows = decodedRows.rows
          if let view = collectionView {
            view.appendData(rows: dataRows!)
          }
        } else {
          if let newRows = decodedRows.rows {
            dataRows!.append(contentsOf: newRows)
            if let view = collectionView {
              view.appendData(rows: dataRows!)
            }
          }
        }
        if (decodedRows.reset == true) {
          selectionsEngine.clear()
        }
      } catch {
        print(error)
      }
    }
  }
  
  override func layoutSubviews() {
    super.layoutSubviews()
    
    createHeaderView()
    createDataCollectionView()
    createGrabbers()
  }
  
  fileprivate func decorate(view: UIView) {
    view.layer.borderWidth = 1;
    view.layer.borderColor = ColorParser().fromCSS(cssString: tableTheme?.borderBackgroundColor ?? "black").cgColor
    view.layer.cornerRadius = CGFloat(tableTheme?.borderRadius ?? 8)
    view.layer.masksToBounds = true
  }
  
  fileprivate func createHeaderView() {
    if( headerView == nil) {
      let newHeaderView = HeaderView(columns: dataColumns!, withTheme: tableTheme!)
      headerView = newHeaderView;
      newHeaderView.backgroundColor = ColorParser().fromCSS(cssString: tableTheme?.headerBackgroundColor ?? "lightgray");
      if (overlayView == nil) {
        let overlayFrame = CGRect(x: 0, y: 0, width: headerView!.frame.width + 50, height: self.frame.height)
        let overlayView = OverlayView(frame: overlayFrame, containerWidth: containerWidth?.intValue ?? Int(self.frame.width))
        addSubview(overlayView)
        self.overlayView = overlayView
      }
      if (rootView == nil) {
        let frame = CGRect(x: 0, y: 0, width: headerView!.frame.width, height: self.frame.height)
        let newRootView = UIView(frame: frame);
        overlayView?.addSubview(newRootView)
        newRootView.addSubview(headerView!)
        let scrollView = UIScrollView(frame: self.frame);
        scrollView.contentSize = CGSize(width: newHeaderView.frame.width + 50, height: self.frame.height)
        addSubview(scrollView)
        scrollView.addSubview(overlayView!)
        rootView = newRootView
        self.scrollView = scrollView
        decorate(view: newRootView)
      }
    }
  }
  
  fileprivate func createDataCollectionView() {
    if(collectionView == nil) {
      let width = Int(headerView?.frame.width ?? frame.width)
      let height = tableTheme?.headerHeight ?? 54
      let frame = CGRect(x: 0, y: height, width: width, height:   Int(self.frame.height) - height)
      let dataCollectionView = DataCollectionView(frame: frame, withRows: dataRows!, andColumns: dataColumns!, theme: tableTheme!, selectionsEngine: selectionsEngine)
      dataCollectionView.onEndReached = self.onEndReached
      dataCollectionView.dataSize = self.dataSize
      dataCollectionView.backgroundColor = ColorParser().fromCSS(cssString: tableTheme?.headerBackgroundColor ?? "lightgray");
      collectionView = dataCollectionView
      rootView!.addSubview(dataCollectionView)
    }
  }
  
  fileprivate func createGrabbers() {
    if let cols = dataColumns, let tableTheme = tableTheme {
      var x = cols[0].width! - 20
      for col in cols {
        let frame = CGRect(x: x, y: 0, width: 40, height: self.frame.height)
        let grabber = GrabberView(frame: frame, index: col.dataColIdx!, theme: tableTheme)
        grabber.collectionView = self.collectionView
        grabber.containerView = self
        grabber.headerView = self.headerView
        grabber.overlayView = self.overlayView
        overlayView!.addSubview(grabber)
        x += col.width!
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
        if (update) {
          sv.contentSize = CGSize(width: cv.frame.width + 50, height: newFrame.height)
        }
      }
    }
  }
}

