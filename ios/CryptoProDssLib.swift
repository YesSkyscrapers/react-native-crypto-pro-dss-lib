// CryptoProDssLib.swift

import Foundation


@objc(CryptoProDssLib)
class CryptoProDssLib : NSObject {
    
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
    
    @objc
    func initViaQr(
        _ stringArgument: String,
        withNumberArgument numberArgument: NSNumber,
        withResolver resolve: @escaping RCTPromiseResolveBlock,
        withRejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        
        jsPromiseResolver = resolve;
        jsPromiseRejecter = reject;
        
        
       
        DispatchQueue.main.async {
                
            if (self.jsPromiseResolver != nil) {
                self.jsPromiseResolver!(String(format: "numberArgument: %@ stringArgument: %@", numberArgument, stringArgument))
            }
                      
        }
    }
}
