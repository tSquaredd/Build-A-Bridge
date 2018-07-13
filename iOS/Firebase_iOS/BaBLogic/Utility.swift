//
//  StaticDatatype.swift
//  BaBLogic
//
//  Created by IOS Design Coding on 7/12/18.
//  Copyright © 2018 Build-A-Bridge-Foundation. All rights reserved.
//

import Foundation
import UIKit

class Utility {
    static var mainViewController : UIViewController!;
    static var userEmail : String?;
    static var userPassword : String?;
}
func createAlert(_ title: String, _ desc: String) -> UIViewController{
    
    let alert = UIAlertController(title: title, message: desc, preferredStyle: .alert);
    alert.addAction(UIAlertAction(title: "OK", style: .default, handler: { _ in
        NSLog(desc)
    }))
    return alert;
}

// images and services are the same.
var imageNames : [String] = ["Educations", "Forms", "Home", "Jobs"];
var servicesSupported : [Service] = [   Service(imageNames[0]),
                                        Service(imageNames[1]),
                                        Service(imageNames[2]),
                                        Service(imageNames[3])];
func getServicesSupported() -> [Service]{
    resetServices();
    return servicesSupported;
}

func resetServices(){
    
    servicesSupported = servicesSupported.map(){(serv:Service) in
        serv.selected = false;
        serv.cell = nil;
        return serv;
    };
}

class Service {
    var image : UIImage?;
    var desc : String;
    var selected : Bool = false;
    var cell : ServiceTableCell!;
    
    init(_ name: String){
        self.image = UIImage(named: name);
        self.desc = name;
    }
}
