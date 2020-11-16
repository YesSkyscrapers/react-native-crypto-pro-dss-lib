// CryptoProDssLib.swift

import Foundation
import SDKFramework

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
                 
            if (self.jsPromiseRejecter != nil) {
                self.jsPromiseRejecter!("Unimplemented", "IOS doesnt require first initialization", nil)
            }
                       
       }
        
    }
    
    override func viewDidLoad() {
            super.viewDidLoad()
          

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
                // шаг 1.
                let rootViewController = UIApplication.shared.delegate?.window??.rootViewController
                let root = UIApplication.shared.keyWindow?.rootViewController
                //root?.present(self, animated: true, completion: nil)
                
            
                
                auth.scanQR(view: root!, base64QR: base642) { type, error in
                    print("scanQR returned")
                    // проверка наличия ошибки (если error равен nil, то функция завершилась успешно, иначе - продолжение сценария невозможен)
                // Ожидается, что ‘type’ будет равен строке ‘Kinit’
                    print("base64", base642)
                    print("type", type)
                    print("error",error)
                    
                    if (self.jsPromiseResolver != nil) {
                        self.jsPromiseResolver!(String(format: "since ok"))
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


