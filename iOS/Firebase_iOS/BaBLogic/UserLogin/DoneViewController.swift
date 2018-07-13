//
//  DoneViewController.swift
//  BaBLogic
//
//  Created by IOS Design Coding on 7/12/18.
//  Copyright © 2018 Build-A-Bridge-Foundation. All rights reserved.
//

import Foundation
import UIKit
import FirebaseAuth
import Firebase

//back to main page
class DoneViewController : UIViewController{
    @IBAction func done(){
        self.navigationController?.popToViewController(Utility.mainViewController, animated: true);
    }
    
}
