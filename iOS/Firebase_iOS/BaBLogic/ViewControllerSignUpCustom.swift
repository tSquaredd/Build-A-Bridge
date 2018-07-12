//
//  ViewControllerSignUpCustom.swift
//  BaBLogic
//
//  Created by Baily Troyer on 7/8/18.
//  Copyright © 2018 Build-A-Bridge-Foundation. All rights reserved.
//

import UIKit
import Firebase
import FirebaseAuth

class ViewControllerSignUpCustom: UIViewController {
    
    @IBOutlet weak var firstName: UITextField!
    @IBOutlet weak var lastName: UITextField!
    @IBOutlet weak var emailAddress: UITextField!
    @IBOutlet weak var password: UITextField!
    
    @IBAction func next(_ sender: Any) {
        
        guard let fName = firstName.text else { return }
        guard let lName = lastName.text else { return }
        guard let pword = password.text else { return }
        guard let eAddress = emailAddress.text else { return }
        
        Auth.auth().createUser(withEmail: eAddress, password: pword) { user, error in
            if error == nil && user != nil {
                print("user created")
                
                let changeRequest = Auth.auth().currentUser?.createProfileChangeRequest()
                changeRequest?.displayName = "\(fName) \(lName)"
                changeRequest?.commitChanges() { error in
                    if error == nil {
                        print("user display name changed")
                        self.dismiss(animated: false, completion: nil)
                        
                    } else {
                        print("Error: \(error!.localizedDescription)")
                    }
                }
                
            } else {
                self.performSegue(withIdentifier: "cantDo", sender: self)
                print("Error: \(error!.localizedDescription)")
            }
            
        }
    }
    
    @IBAction func back(_ sender: Any) {
        print("moving back to selection")
        self.performSegue(withIdentifier: "backSegue", sender: self)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
