// CryptoProDssLib.swift

import Foundation

@objc(CryptoProDssLib)
class CryptoProDssLib : NSObject {
  @objc(sampleMethod:numberParameter:callback:)
  func sampleMethod(stringArgument: String, numberArgument: NSNumber, callback: RCTResponseSenderBlock) -> Void {
    // TODO: Implement some actually useful functionality
    callback([String(format: "numberArgument: %@ stringArgument: %@", numberArgument, stringArgument)])
  }
}
