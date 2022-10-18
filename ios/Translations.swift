//
//  Translations.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-10-17.
//

import Foundation

struct MenuTranslations: Decodable {
  var copy: String?
  var expand: String?
}

struct Translations: Decodable {
  var menu: MenuTranslations?
}
