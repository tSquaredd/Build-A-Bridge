//
//  ViewControllerLoginEmail.swift
//  BaBLogic
//
//  Created by Baily Troyer on 7/8/18.
//  Copyright © 2018 Build-A-Bridge-Foundation. All rights reserved.
//

import UIKit
import FirebaseAuth
import Firebase

class ViewControllerLoginEmail: UIViewController {
    @IBOutlet weak var email: UITextField!
    @IBOutlet weak var password: UITextField!
    
    @IBAction func login(_ sender: Any) {
        guard let eval = email.text else { return }
        guard let pword = password.text else { return }
        
        Auth.auth().signIn(withEmail: eval, password: pword) { user, error in
            if error == nil && user != nil {
                //signInToHome
               self.performSegue(withIdentifier: "signInToHome", sender: self)
            } else {
                print("error loggin in: \(error?.localizedDescription)")
            }
            
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

}
