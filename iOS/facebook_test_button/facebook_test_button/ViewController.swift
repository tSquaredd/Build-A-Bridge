//
//  ViewController.swift
//  facebook_test_button
//
//  Created by Baily Troyer on 6/15/18.
//  Copyright © 2018 Baily Troyer. All rights reserved.
//

import UIKit
import FBSDKLoginKit

class ViewController: UIViewController, FBSDKLoginButtonDelegate {

    override func viewDidLoad() {
        super.viewDidLoad()
        
        let loginButton = FBSDKLoginButton()
        view.addSubview(loginButton)
        
        
        //frames are obolete use constraints instead
        loginButton.frame = CGRect(x: 16, y: 50, width: view.frame.width - 32, height: 50)
        // Do any additional setup after loading the view, typically from a nib.
        
        loginButton.delegate = self
    }
    
    
    
    func loginButtonDidLogOut(_ loginButton: FBSDKLoginButton!) {
        print("Did log out")
    }

    func loginButton(_ loginButton: FBSDKLoginButton!, didCompleteWith result: FBSDKLoginManagerLoginResult!, error: Error!) {
        
        if error != nil {
            print(error)
            return
        }
        
        print("sucess logging into Facebook")
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }


}

