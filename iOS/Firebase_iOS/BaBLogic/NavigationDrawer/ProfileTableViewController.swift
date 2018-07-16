//
//  ProfileTableViewController.swift
//  BaBLogic
//
//  Created by IOS Design Coding on 7/15/18.
//  Copyright © 2018 Build-A-Bridge-Foundation. All rights reserved.
//

import Foundation
import UIKit
class ProfileTableViewController : TableViewMenuController{
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if(indexPath.row == 0){
            let cell = Bundle.main.loadNibNamed("ProfessionalHeadshotCell", owner: self, options: nil)![0] as! ProfessionalHeadshotCell;
            cell.selectionStyle = UITableViewCellSelectionStyle.none
            cell.updateContent(arrayMenu[indexPath.row]["title"]!, UIImage(named: arrayMenu[indexPath.row]["icon"]!));
            cell.preservesSuperviewLayoutMargins = false
            cell.backgroundColor = UIColor.clear
            return cell;
        }else{
            return super.tableView(tableView, cellForRowAt: indexPath);
        }
    }
    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        if(indexPath.row == 0){
            return 195;
        }else{
            return super.tableView(tableView, heightForRowAt: indexPath);
        }
    }
}
