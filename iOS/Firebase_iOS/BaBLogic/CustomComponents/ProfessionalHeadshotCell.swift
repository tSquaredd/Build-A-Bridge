//
//  ProfessionalHeadshot.swift
//  BaBLogic
//
//  Created by IOS Design Coding on 7/15/18.
//  Copyright © 2018 Build-A-Bridge-Foundation. All rights reserved.
//

import Foundation
import UIKit
class ProfessionalHeadshotCell : UITableViewCell{
    
    @IBOutlet weak var profileImg: UIImageView!
    @IBOutlet weak var Name: UILabel!
    
    override func awakeFromNib() {
        
        self.profileImg.layer.cornerRadius = self.profileImg.bounds.width/2;
        self.profileImg.clipsToBounds = true;
    }
    // todo: support profile picture
    func updateContent(_ str: String, _ img: UIImage?){
        self.profileImg.image = img;
        self.Name.text = str;
    }
}
