// CryptoProDssLib.swift

import Foundation
import SDKFramework
import UIKit

@objc(CryptoProDssLib)
class CryptoProDssLib : UIViewController {
    
    private var jsPromiseResolver: RCTPromiseResolveBlock? = nil;
    private var jsPromiseRejecter: RCTPromiseRejectBlock? = nil;
    
    @objc
    func firstInitialization(
        _ resolve: @escaping RCTPromiseResolveBlock,
        withRejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        
        jsPromiseResolver = resolve;
        jsPromiseRejecter = reject;
        
        DispatchQueue.main.async {
            guard let rootVC = UIApplication.shared.delegate?.window??.visibleViewController, (rootVC.navigationController != nil) else {
                 reject("E_INIT", "Error getting rootViewController", NSError(domain: "", code: 200, userInfo: nil))
                 return
            }
            
            
            let cpd = CryptoProDss();
            cpd._init(view: rootVC) { code in
                
                print(code)
                
                if (self.jsPromiseResolver != nil) {
                    self.jsPromiseResolver!(String(format: "since ok"))
                }
            }
            
            
          
                       
       }
        
    }
    
    @objc
    func getOperations(
        _ resolve: @escaping RCTPromiseResolveBlock,
        withRejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        
        jsPromiseResolver = resolve;
        jsPromiseRejecter = reject;
        
        DispatchQueue.main.async {
            guard let rootVC = UIApplication.shared.delegate?.window??.visibleViewController, (rootVC.navigationController != nil) else {
                 reject("E_INIT", "Error getting rootViewController", NSError(domain: "", code: 200, userInfo: nil))
                 return
            }
            
            
            let policy = Policy();
            policy.getOperations(view: rootVC, kid: self.getLastUserKid()!, type: nil, opId: nil){ operationsInfo,error  in
                
                print("getOperations result")
                print(operationsInfo)
                print(error)
                
                if (self.jsPromiseResolver != nil) {
                    self.jsPromiseResolver!(String(format: "since ok"))
                }
            }
            
            
          
                       
       }
        
    }
    
    override func viewDidLoad() {
            super.viewDidLoad()
          

        }
    
    func getLastUserKid() -> String? {
            
        var authList = [] as [DSSUser];
            do {
                authList = try Auth.getAuthList();
                print(authList, authList.count)
                let lastUser = authList[authList.count-1];
                return lastUser.kid;
            } catch {
                
            }


        return nil;
    }
    
    @objc
    func initViaQr(
        _ base64: String?,
        withResolver resolve: @escaping RCTPromiseResolveBlock,
        withRejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        
        jsPromiseResolver = resolve;
        jsPromiseRejecter = reject;
        
        let base642: String? = nil
       
        DispatchQueue.main.async {
                
            do {
                
                
                
                
                let auth = try Auth()
          
                
                let rootViewController = UIApplication.shared.delegate?.window??.rootViewController

                
                guard let rootVC = UIApplication.shared.delegate?.window??.visibleViewController, (rootVC.navigationController != nil) else {
                     reject("E_INIT", "Error getting rootViewController", NSError(domain: "", code: 200, userInfo: nil))
                     return
                }
                let user = DSSUser();
                let registerInfo = RegisterInfo();
                
                auth.scanQR(view: rootVC, base64QR: base64)  { type, error in
                    print("scanqr", type, error)
                    if error != nil {
                        if (self.jsPromiseRejecter != nil) {
                            self.jsPromiseRejecter!("fail", "not ok", error)
                        }
                    }
               
                    auth.kinit(view: rootVC, dssUser: user, registerInfo: registerInfo, keyProtectionType: SDKFramework.ProtectionType.BIOMETRIC, activationCode: nil, password: nil) { error in
                        print("kinit", error)
                        if error != nil {
                            if (self.jsPromiseRejecter != nil) {
                                self.jsPromiseRejecter!("fail", "not ok", error)
                            }
                        }
                        
                        auth.confirm(view: rootVC, kid: self.getLastUserKid()!) { error in
                            print("confirm", error)
                            
                            if error != nil {
                                if (self.jsPromiseRejecter != nil) {
                                    self.jsPromiseRejecter!("fail", "not ok", error)
                                }
                            }
                            
                            auth.verify(view: rootVC, kid: self.getLastUserKid()!, silent: false) { error in
                                print("verify", error)
                                
                                if error != nil {
                                    if (self.jsPromiseRejecter != nil) {
                                        self.jsPromiseRejecter!("fail", "not ok", error)
                                    }
                                }
                                
                                if (self.jsPromiseResolver != nil) {
                                    self.jsPromiseResolver!(String(format: "since ok"))
                                }
                            }
                        }
                    }
                }
                
            } catch {
                print("scanQR error")
                // обработка ошибок }
                if (self.jsPromiseRejecter != nil) {
                    self.jsPromiseRejecter!("fail", "not ok", "smth error")
                }
                   
            }
                  
        }
    }
}


public extension UIWindow {
    var visibleViewController: UIViewController? {
        self.window?.makeKeyAndVisible()
        return UIWindow.getVisibleViewControllerFrom(self.rootViewController)
    }
    
    static func getVisibleViewControllerFrom(_ vc: UIViewController?) -> UIViewController? {
        if let nc = vc as? UINavigationController {
            return UIWindow.getVisibleViewControllerFrom(nc.visibleViewController)
        } else if let tc = vc as? UITabBarController {
            return UIWindow.getVisibleViewControllerFrom(tc.selectedViewController)
        } else {
            if let pvc = vc?.presentedViewController {
                return UIWindow.getVisibleViewControllerFrom(pvc)
            } else {
                return vc
            }
        }
    }
}


