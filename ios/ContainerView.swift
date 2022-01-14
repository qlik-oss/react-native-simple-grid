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
  var dataColumns: [DataColumn]?
  var dataRows: [DataRow]?
  var rootView = UIStackView()
  weak var headerView: HeaderView? = nil
  
  override init(frame: CGRect) {
    super.init(frame: frame)
    addSubview(rootView)
  }
  
  required init?(coder: NSCoder) {
    super.init(coder: coder)
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
        dataRows = decodedRows.rows

      } catch {
        print(error)
      }
    }
  }
  
  override func layoutSubviews() {
    super.layoutSubviews()
    if (headerView == nil) {
      let newHeaderView = HeaderView(frame: CGRect(x: 0, y: 0, width: Int(self.frame.width), height: tableTheme?.headerHeight ?? 54))
      headerView = newHeaderView;
      newHeaderView.backgroundColor = ColorParser().fromCSS(cssString: tableTheme?.headerBackgroundColor ?? "lightgray");
      addSubview(newHeaderView)
      let dataCollectionView = DataCollectionView(frame: self.frame)
      addSubview(dataCollectionView)
      dataCollectionView.setData(columns: dataColumns!, withRows: dataRows!)
    }
  }
}

