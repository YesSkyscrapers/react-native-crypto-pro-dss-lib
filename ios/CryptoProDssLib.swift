// CryptoProDssLib.swift

import Foundation
import SDKFramework
import UIKit


struct DictionaryEncoder {
    static func encode<T>(_ value: T) throws -> [String: Any] where T: Encodable {
        let jsonData = try JSONEncoder().encode(value)
        return try JSONSerialization.jsonObject(with: jsonData) as? [String: Any] ?? [:]
    }
    
    static func convertDssUser(user: DSSUser) -> [String:Any] {
        
        var map = [String:Any]()
        
        let kid : String = user.kid;
        let uid : String = user.uid;
        
        map.updateValue(kid, forKey: "kid");
        map.updateValue(uid, forKey: "uid");
        
        return map;
    }
}

@objc(CryptoProDssLib)
class CryptoProDssLib : UIViewController {
    
    private var jsPromiseResolver: RCTPromiseResolveBlock? = nil;
    private var jsPromiseRejecter: RCTPromiseRejectBlock? = nil;
    private var lastAuth: Auth? = nil;
    
    @objc
    func SdkInitialization(
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
                
                if (self.jsPromiseResolver != nil) {
                    self.jsPromiseResolver!(String(format: "inited"))
                }
            }
       }
    }
    
    @objc
    func getOperations(
        _ kid: String,
        withResolver resolve: @escaping RCTPromiseResolveBlock,
        withRejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        
        jsPromiseResolver = resolve;
        jsPromiseRejecter = reject;
        
        DispatchQueue.main.async {
            guard let rootVC = UIApplication.shared.delegate?.window??.visibleViewController, (rootVC.navigationController != nil) else {
                 reject("E_INIT", "Error getting rootViewController", NSError(domain: "", code: 200, userInfo: nil))
                 return
            }
            
            let policy = Policy();
            policy.getOperations(view: rootVC, kid: kid, type: nil, opId: nil){ operationsInfo,error  in
                
                var operations = [] as [Any];
                
                for _operation in operationsInfo?.operations ?? [] {
                    operations.append(try! DictionaryEncoder.encode(_operation))
                }
                
                if (self.jsPromiseResolver != nil) {
                    self.jsPromiseResolver!(operations)
                }
            }
       }
        
    }
    
    @objc
    func signMT(
        _ transactionId: String,
        withKid kid: String,
        withResolver resolve: @escaping RCTPromiseResolveBlock,
        withRejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        
        jsPromiseResolver = resolve;
        jsPromiseRejecter = reject;
        
        DispatchQueue.main.async {
            guard let rootVC = UIApplication.shared.delegate?.window??.visibleViewController, (rootVC.navigationController != nil) else {
                 reject("E_INIT", "Error getting rootViewController", NSError(domain: "", code: 200, userInfo: nil))
                 return
            }
            
            
            let policy = Policy();
            let sign = Sign();
            policy.getOperations(view: rootVC, kid: kid, type: nil, opId: nil){ operationsInfo,error  in
                
                var operation = nil as SDKFramework.Operation?;
                
                for _operation in operationsInfo?.operations ?? [] {
                  
                    if (transactionId == _operation.transactionId) {
                        operation = _operation;
                    }
                }
                
                sign.signMT(view: rootVC, kid: kid, operation: operation, enableMultiSelection: false, inmediateSendConfirm: true, silent: false){ approveRequestMT,error  in
                    
                    if (self.jsPromiseResolver != nil) {
                        self.jsPromiseResolver!("success")
                    }
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
    func updateStyles(
        _ resolve: @escaping RCTPromiseResolveBlock,
        withRejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        
        jsPromiseResolver = resolve;
        jsPromiseRejecter = reject;
        
        DispatchQueue.main.async {
            let policy = Policy();
         
            do {
                try policy.setPersonalisation(url: Bundle.main.url(forResource: "SDKStyles", withExtension:"json")!)
                
                if (self.jsPromiseResolver != nil) {
                    self.jsPromiseResolver!("updateStyles success")
                }
            } catch {
                if (self.jsPromiseRejecter != nil) {
                    self.jsPromiseRejecter!("cant load styles", "cant load styles", "cant load styles")
                }
            }
       }
    }
    
    
    @objc
    func getUsers(
        _ resolve: @escaping RCTPromiseResolveBlock,
        withRejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        
        jsPromiseResolver = resolve;
        jsPromiseRejecter = reject;
        
        DispatchQueue.main.async {
            var authList = [] as [DSSUser];
            do {
                authList = try Auth.getAuthList();
            } catch {
                print("getUsers error")
                print(error)
            }
            
            var list = [] as [Any]
            
            for user in authList {
                list.append(DictionaryEncoder.convertDssUser(user: user));
            }
            
            if (self.jsPromiseResolver != nil) {
                self.jsPromiseResolver!(list);
            }
       }
    }
    
    @objc
    func continueInitViaQr(
            _ kid: String,
            withResolver resolve: @escaping RCTPromiseResolveBlock,
            withRejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
            
            jsPromiseResolver = resolve;
            jsPromiseRejecter = reject;
           
            DispatchQueue.main.async {
                    
                do {
                    let rootViewController = UIApplication.shared.delegate?.window??.rootViewController

                    guard let rootVC = UIApplication.shared.delegate?.window??.visibleViewController, (rootVC.navigationController != nil) else {
                         reject("E_INIT", "Error getting rootViewController", NSError(domain: "", code: 200, userInfo: nil))
                         return
                    }
                    
                    self.lastAuth!.confirm(view: rootVC, kid: kid) { error in
                        if error != nil {
                            if (self.jsPromiseRejecter != nil) {
                                self.jsPromiseRejecter!("auth confirm - failed", "auth confirm - failed", error)
                            }
                        }
                        
                        self.lastAuth!.verify(view: rootVC, kid: kid, silent: false) { error in
                            if error != nil {
                                if (self.jsPromiseRejecter != nil) {
                                    self.jsPromiseRejecter!("auth verify - failed", "auth verify - failed", error)
                                }
                            }
                            
                            if (self.jsPromiseResolver != nil) {
                                self.jsPromiseResolver!(String(format: "success"))
                            }
                        }
                    }
                    
                    
                } catch {
                    print("continueInitViaQr error")
                    print(error)
                    
                    if (self.jsPromiseRejecter != nil) {
                        self.jsPromiseRejecter!("continueInitViaQr - error", "continueInitViaQr - error", "continueInitViaQr - error")
                    }
                       
                }
            }
    }
    
    @objc
    func initViaQr(
        _ base64: String?,
        withResolver resolve: @escaping RCTPromiseResolveBlock,
        withRejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        
        jsPromiseResolver = resolve;
        jsPromiseRejecter = reject;
       
        DispatchQueue.main.async {
                
            do {
                self.lastAuth = try Auth()
                let rootViewController = UIApplication.shared.delegate?.window??.rootViewController

                guard let rootVC = UIApplication.shared.delegate?.window??.visibleViewController, (rootVC.navigationController != nil) else {
                     reject("E_INIT", "Error getting rootViewController", NSError(domain: "", code: 200, userInfo: nil))
                     return
                }
                
                let user = DSSUser();
                let registerInfo = RegisterInfo();
                
                self.lastAuth!.scanQR(view: rootVC, base64QR: base64)  { type, error in
                    if error != nil {
//                        if (self.jsPromiseRejecter != nil) {
//                            self.jsPromiseRejecter!("scanQr - failed", "scanQr - failed", "scanQr - failed")
//                        }
                        reject("scanQr - failed", "scanQr - failed", "scanQr - failed")
                    }
               
                    self.lastAuth!.kinit(view: rootVC, dssUser: user, registerInfo: registerInfo, keyProtectionType: SDKFramework.ProtectionType.PASSWORD, activationCode: nil, password: nil) { error in
                        
                        if error != nil {
//                            if (self.jsPromiseRejecter != nil) {
//                                self.jsPromiseRejecter!("kinit - failed", "kinit - failed", "kinit - failed")
//                            }
                            reject("kinit - failed", "kinit - failed", "kinit - failed")
                        }
                        
                            resolve(String(format: "success"))
                        
//                        if (self.jsPromiseResolver != nil) {
//                            self.jsPromiseResolver!(String(format: "success"))
//                        }
                    }
                }
                
            } catch {
                print("scanQR error")
                print(error)
                
//                if (self.jsPromiseRejecter != nil) {
//                    self.jsPromiseRejecter!("scanQr - error", "scanQr - error", "scanQr - error")
//                }
                reject("scanQr - error", "scanQr - error", "scanQr - error")
                   
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


