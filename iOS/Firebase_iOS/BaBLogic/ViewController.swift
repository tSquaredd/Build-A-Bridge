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

class ViewController: UIViewController {

    @IBAction func signIn(_ sender: Any) {
    //toSignIn
        self.performSegue(withIdentifier: "toSignIn", sender: self)
    }
    
    @IBAction func signUp(_ sender: Any) {
        print("moving to sign up vc")
        self.performSegue(withIdentifier: "signUpSegue", sender: self)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        if Auth.auth().currentUser != nil {
            self.performSegue(withIdentifier: "toHomeScreen", sender: self)
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }


}

