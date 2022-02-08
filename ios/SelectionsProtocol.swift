//
//  SelectionsProtocol.swift
//  qlik-trial-react-native-text-grid
//
//  Created by Vittorio Cellucci on 2022-02-07.
//

import Foundation
protocol SelectionsListener {
  var id: Int {get set}
  func toggleSelected(data: String)
  func clearSelected()
}
