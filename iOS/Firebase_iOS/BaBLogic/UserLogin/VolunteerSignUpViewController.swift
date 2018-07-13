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

class VolunteerSignUpViewController: UIViewController {
    
    @IBOutlet weak var firstName: UITextField!
    @IBOutlet weak var lastName: UITextField!
    @IBOutlet weak var emailAddress: UITextField!
    @IBOutlet weak var password: UITextField!
    @IBOutlet weak var phone: UITextField!
    @IBOutlet weak var dateOfBirth: UITextField!
    @IBAction func next(_ sender: Any) {
        guard let fName = firstName.text else {
            return
        }
        
        if(fName.count == 0){
            self.present(createAlert("Sign up Error", "You need to fill in your First Name."), animated: true, completion: nil);
            return
        }
        
        guard let lName = lastName.text else {
            let alert = UIAlertController(title: "Sign up Error", message: "You need to fill in your Last Name.", preferredStyle: .alert);
            self.present(alert, animated: true, completion: nil);
            return
            
        }
        
        if(lName.count == 0){
            self.present(createAlert("Sign up Error", "You need to fill in your Last Name."), animated: true, completion: nil);
            return
        }
        
        guard let pword = password.text else {
            let alert = UIAlertController(title: "Sign up Error", message: "You need to fill in your Password.", preferredStyle: .alert);
            self.present(alert, animated: true, completion: nil);
            return
        }
        
        if(pword.count == 0){
            self.present(createAlert("Sign up Error", "You need to fill in your Password."), animated: true, completion: nil);
            return
        }
        
        guard let eAddress = emailAddress.text else {
            let alert = UIAlertController(title: "Sign up Error", message: "You need to fill in your Email.", preferredStyle: .alert);
            self.present(alert, animated: true, completion: nil);
            return
        }
        
        if(eAddress.count == 0){
            self.present(createAlert("Sign up Error", "You need to fill in your Email."), animated: true, completion: nil);
            return
        }
        
        guard let phoneNumber = phone.text else {
            let alert = UIAlertController(title: "Sign up Error", message: "You need to fill in your Phone Number.", preferredStyle: .alert);
            self.present(alert, animated: true, completion: nil);
            return
        }
        
        if(phoneNumber.count == 0){
            self.present(createAlert("Sign up Error", "You need to fill in your Phone Number."), animated: true, completion: nil);
            return
        }
        
        guard let dob = dateOfBirth.text else {
            let alert = UIAlertController(title: "Sign up Error", message: "You need to fill in your Date of birth.", preferredStyle: .alert);
            self.present(alert, animated: true, completion: nil);
            return
        }
        
        if(dob.count == 0){
            self.present(createAlert("Sign up Error", "You need to fill in your Birth Date."), animated: true, completion: nil);
            return
        }
        
        Auth.auth().createUser(withEmail: eAddress, password: pword) { user, error in
            
            if error != nil || user == nil {
                self.present(createAlert("Sign up Failed", error!.localizedDescription), animated: true, completion: nil)
                
            }else{
                print("user created")
                Utility.userEmail = eAddress;
                Utility.userPassword = pword;
                
                Auth.auth().signIn(withEmail: eAddress, password: pword) { user, error in
                    if error == nil && user != nil {
                        let vc = self.storyboard!.instantiateViewController(withIdentifier: "RefugeeHelpItems");
                        self.navigationController!.pushViewController(vc, animated: true);
                    } else {
                        self.present(createAlert("Sign in Failed", error!.localizedDescription), animated: true, completion: nil);
                    }
                    
                }
                let changeRequest = Auth.auth().currentUser?.createProfileChangeRequest()
                changeRequest?.displayName = "\(fName) \(lName)"
                changeRequest?.commitChanges() { error in
                    if error == nil {
                        //print("user display name changed")
                        //self.performSegue(withIdentifier: "volunteerStep2", sender: self)
                    } else {
                        print("Error: \(error!.localizedDescription)")
                    }
                }
                
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
    
    
    /*
     // MARK: - Navigation
     
     // In a storyboard-based application, you will often want to do a little preparation before navigation
     override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
     // Get the new view controller using segue.destinationViewController.
     // Pass the selected object to the new view controller.
     }
     */
    
}
