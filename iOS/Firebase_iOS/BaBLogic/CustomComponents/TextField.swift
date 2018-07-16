//
//  TextField.swift
//  BaBLogic
//
//  Created by IOS Design Coding on 7/15/18.
//  Copyright © 2018 Build-A-Bridge-Foundation. All rights reserved.
//

import Foundation
import UIKit
@IBDesignable
class TextField: UITextField {
    @IBInspectable var insetX: CGFloat = 0
    @IBInspectable var insetY: CGFloat = 0
    
    // placeholder position
    override func textRect(forBounds bounds: CGRect) -> CGRect {
        return bounds.insetBy(dx: insetX, dy: insetY)
    }
    
    // text position
    override func editingRect(forBounds bounds: CGRect) -> CGRect {
        return bounds.insetBy(dx: insetX, dy: insetY)
    }
}
