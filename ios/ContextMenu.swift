//
//  ContextMenu.swift
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-10-21.
//

import Foundation

class ContextMenu: NSObject {
  var cell: DataCell?
  var menuTranslations: MenuTranslations?

  func showMenu(_ sender: UILongPressGestureRecognizer, view: UIView) {
    guard let menuTranslations = self.menuTranslations else {return}
    view.becomeFirstResponder()

    let menu = UIMenuController.shared

    let locationOfTouchInLabel = sender.location(in: view)

    if !menu.isMenuVisible {
      var rect = view.bounds
      rect.origin = locationOfTouchInLabel
      rect.size = CGSize(width: 1, height: 1)

      menu.menuItems = [
        UIMenuItem(
          title: menuTranslations.copy ?? "Copy",
          action: #selector(handleCopy(_:))
        ),
        UIMenuItem(
          title: menuTranslations.expand ?? "Expand Row",
          action: #selector(handleExpand(_:))

        )
      ]

      menu.setTargetRect(view.frame, in: view)

      if #available(iOS 13.0, *) {
        menu.showMenu(from: view, rect: rect)
      } else {
        // Fallback on earlier versions
      }
    }
  }

  @objc func handleCopy(_ controller: UIMenuController) {
  }

  @objc func handleExpand(_ controller: UIMenuController) {
  }

}
