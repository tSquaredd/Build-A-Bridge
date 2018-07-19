//
//  ViewController.swift
//  BaBLogic
//
//  Created by Baily Troyer on 7/8/18.
//  Copyright © 2018 Build-A-Bridge-Foundation. All rights reserved.
//

import UIKit
import Firebase
import FirebaseAuth

class MainViewController: BaseViewController {
    private var initializeLater : Bool = false;
    @IBAction func logOut(_ sender: Any) {
        print("logging out")
        do{
            try Auth.auth().signOut()
            
            let storyboard = UIStoryboard(name: "Login", bundle: nil)
            let vc = storyboard.instantiateViewController(withIdentifier: "Login") as UIViewController
            self.navigationController?.pushViewController(vc, animated: true)
        }catch {
            
            self.present(createAlert("Can not sign out", error.localizedDescription), animated: false, completion: nil);
        }
    }
    
    
        
    override func viewDidLoad() {
        super.viewDidLoad()
        Utility.mainViewController = self;
        
        if Auth.auth().currentUser == nil {
            //self.performSegue(withIdentifier: "toHomeScreen", sender: self)
            
            let storyboard = UIStoryboard(name: "Login", bundle: nil)
            let vc = storyboard.instantiateViewController(withIdentifier: "Login") as UIViewController
            //self.present(vc, animated: true, completion: nil);
            self.navigationController?.pushViewController(vc, animated: true);
            self.initializeLater = true;
            return
        }
        
        addChildView(storyBoardID: "Home", titleOfChildren: (Auth.auth().currentUser?.email)!, iconName: "cuteDeath")
        addChildView(storyBoardID: "Contacts", titleOfChildren: "Contacts", iconName: "two")
        addChildView(storyBoardID: "Tasks", titleOfChildren: "Tasks", iconName: "task")
        addChildView(storyBoardID: "Setting", titleOfChildren: "Setting", iconName: "settin")
        addChildView(storyBoardID: "AboutBAB", titleOfChildren: "About BAB", iconName: "book")
        addChildView(storyBoardID: "PrivacyPolicy", titleOfChildren: "Privacy Policy", iconName: "door")
        
        
        showFirstChild()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        if(initializeLater){
            addChildView(storyBoardID: "Home", titleOfChildren: (Auth.auth().currentUser?.displayName)!, iconName: "cuteDeath")
            addChildView(storyBoardID: "Contacts", titleOfChildren: "Contacts", iconName: "two")
            addChildView(storyBoardID: "Tasks", titleOfChildren: "Tasks", iconName: "task")
            addChildView(storyBoardID: "Setting", titleOfChildren: "Setting", iconName: "settin")
            addChildView(storyBoardID: "AboutBAB", titleOfChildren: "About BAB", iconName: "book")
            addChildView(storyBoardID: "PrivacyPolicy", titleOfChildren: "Privacy Policy", iconName: "door")
            
            
            showFirstChild()
            self.initializeLater = false;
        }
        
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }


}

