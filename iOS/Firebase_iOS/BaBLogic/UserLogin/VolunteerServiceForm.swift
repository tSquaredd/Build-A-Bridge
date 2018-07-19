//
//  RefugeeServiceForm.swift
//  BaBLogic
//
//  Created by IOS Design Coding on 7/12/18.
//  Copyright © 2018 Build-A-Bridge-Foundation. All rights reserved.
//

import Foundation
import UIKit

class VolunteerServiceForm : UIViewController,UITableViewDataSource, UITableViewDelegate {
    private var services : [Service] = getServicesSupported();
    
    @IBOutlet weak var tableView: UITableView!
    override func viewDidLoad() {
        self.tableView.delegate = self;
        self.tableView.dataSource = self;
        tableView.allowsSelection = false;
    }
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1;
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return services.count;
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 170;
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = Bundle.main.loadNibNamed("ServiceTableCell", owner: self, options: nil)![0] as! ServiceTableCell;
        cell.initialize(services[indexPath.row]);
        services[indexPath.row].cell = cell;
        return cell;
    }
    
    
    
}
