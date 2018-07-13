//
//  ViewControllerMain.swift
//  BaBLogic
//
//  Created by Baily Troyer on 7/8/18.
//  Copyright © 2018 Build-A-Bridge-Foundation. All rights reserved.
//

import UIKit
import FirebaseAuth
import Firebase


// Sign in page
class LoginViewController: UIViewController {
    @IBOutlet weak var email: UITextField!
    @IBOutlet weak var password: UITextField!
    
    override func viewWillAppear(_ animated: Bool) {
        self.navigationController?.navigationBar.isHidden = true;
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        /*
        let user = Auth.auth().currentUser
        print("USER: \(user)")
        print("USER DISPLAY NAME : \(user?.displayName)")
        
        homeLabel.text = "Greetings \(user?.displayName)"
        */
        // Do any additional setup after loading the view.
    }

    @IBAction func login(_ sender: Any) {
        guard let eval = email.text  else {
            return
        }
        if(eval.count == 0){
            self.present(createAlert("Sign in Error", "You need to fill in your Email."), animated: true, completion: nil);
            return
        }
        guard let pword = password.text else{
            return
        }
        if(pword.count == 0){
            self.present(createAlert("Sign in Error", "You need to fill in your Password."), animated: true, completion: nil);
            return
        }
        Auth.auth().signIn(withEmail: eval, password: pword) { user, error in
            if error == nil && user != nil {
                //signInToHome
                
               // let storyboard = UIStoryboard(name: "Main", bundle: nil)
               // let vc = storyboard.instantiateViewController(withIdentifier: "HomePage") as UIViewController;
                
            self.navigationController?.popViewController(animated: true);
                //self.performSegue(withIdentifier: "signInToHome", sender: self)
            } else {
                self.present(createAlert("Sign in Failed", error!.localizedDescription), animated: true, completion: nil);
            }
            
        }
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
