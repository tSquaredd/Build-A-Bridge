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

class MainViewController: UIViewController {
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
        //self.dismiss(animated: false, completion: nil)
        //self.performSegue(withIdentifier: "toSignUpScreen", sender: self)
        
    override func viewDidLoad() {
        super.viewDidLoad()
        Utility.mainViewController = self;
        // Do any additional setup after loading the view, typically from a nib.
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        if Auth.auth().currentUser == nil {
            //self.performSegue(withIdentifier: "toHomeScreen", sender: self)
            
            let storyboard = UIStoryboard(name: "Login", bundle: nil)
            let vc = storyboard.instantiateViewController(withIdentifier: "Login") as UIViewController
            //self.present(vc, animated: true, completion: nil);
            self.navigationController?.pushViewController(vc, animated: true)
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }


}

