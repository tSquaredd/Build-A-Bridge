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

class ViewControllerMain: UIViewController {
    @IBOutlet weak var homeLabel: UILabel!
    
    @IBAction func logOut(_ sender: Any) {
        print("logging out")
        try! Auth.auth().signOut()
        //self.dismiss(animated: false, completion: nil)
        self.performSegue(withIdentifier: "toSignUpScreen", sender: self)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()

        let user = Auth.auth().currentUser
        print("USER: \(user)")
        print("USER DISPLAY NAME : \(user?.displayName)")
        
        homeLabel.text = "Greetings \(user?.displayName)"
        
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
