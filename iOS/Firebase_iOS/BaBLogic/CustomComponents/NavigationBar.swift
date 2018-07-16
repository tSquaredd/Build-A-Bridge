
//
//  NavigationController.swift
//  BaBLogic
//
//  Created by IOS Design Coding on 7/15/18.
//  Copyright © 2018 Build-A-Bridge-Foundation. All rights reserved.
//

import Foundation
import UIKit

class NavigationBar: UINavigationBar {
    override func sizeThatFits(_ size: CGSize) -> CGSize {
        let newSize:CGSize = CGSize.init(width: self.superview!.frame.size.width, height: 187);
        return newSize
    }
}
