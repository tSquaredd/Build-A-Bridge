//
//  FormSelectView.swift
//  BaBLogic
//
//  Created by IOS Design Coding on 7/12/18.
//  Copyright © 2018 Build-A-Bridge-Foundation. All rights reserved.
//

import Foundation
import UIKit

class ServiceTableCell: UITableViewCell {
    var _service : Service!;
    
    var notSelectedImg : UIImage? = UIImage.init(named: "notSelectedCheckbox");
    var selectedImg : UIImage? = UIImage.init(named: "selectedCheckbox");
    
    
    @IBOutlet weak var theImageView: UIImageView!
    
    @IBOutlet weak var button: UIButton!
    @IBOutlet weak var desc: UILabel!
    func initialize(_ service : Service){
        self._service = service;
        self.theImageView!.image = _service.image;
        updateSelection(service.selected);
        desc.text = service.desc
    }
    func updateSelection(_ selected : Bool){
        if( selected ){
            button.setImage(selectedImg, for: UIControlState.normal)
        }else{
            button.setImage(notSelectedImg, for: UIControlState.normal)
        }
        _service.selected = selected;
    }
    override func layoutSubviews() {
    }
    
    @IBAction func selectionClick(_ sender: Any) {
        self.updateSelection( !_service.selected );
    }
    
    
}
