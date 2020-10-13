// CryptoProDssLib.m

#import <React/RCTBridge.h>

@interface RCT_EXTERN_MODULE(CryptoProDssLib, NSObject)


RCT_EXTERN_METHOD(firstInitialization:
                  (RCTPromiseResolveBlock)resolve
                  withRejecter: (RCTPromiseRejectBlock)reject
)

RCT_EXTERN_METHOD(initViaQr:
                  (NSString *)stringArgument
                  withNumberArgument:(nonnull NSNumber *)numberArgument
                  withResolver: (RCTPromiseResolveBlock)resolve
                  withRejecter: (RCTPromiseRejectBlock)reject
)

@end
