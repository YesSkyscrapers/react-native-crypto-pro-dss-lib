#import <UIKit/UIKit.h>

@interface ModalAlert : NSObject

+ (bool) ask: (NSString *) wnd_header message:(NSString *)wnd_message;
+ (bool) displayMessage: (NSString *) header message: (NSString *) message;
+ (void) displayMessageOKButton: (NSString *) header message: (NSString *) message;
+ (NSUInteger) queryWith: (NSString *)question message: (NSString *) text button1: (NSString *)button1 button2: (NSString *)button2;
+ (NSUInteger) queryTwoFields: (NSString *) header message: (NSString *) text out1: (NSString **) out1 secure1: (bool) secure1 out2: (NSString **) out2 secure2: (bool) secure2;
+ (NSUInteger) queryField: (NSString *) header message: (NSString *) text out1: (NSString **) out1 secure: (bool) secure;
+ (NSUInteger) queryField: (NSString *) header message: (NSString *) text out1: (NSString **) out1 secure: (bool) secure  isSwitch: (bool) isSwitch enableCB: (bool) enableCB savePassword:(bool *) savePassword; 
+ (NSUInteger) queryWithFields: (NSString *)header message: (NSString *)text button1: (NSString *)button1 
					   button2: (NSString *)button2 out1: (NSString **) out1 secure:(bool) secure;
+ (NSUInteger) queryWithFields: (NSString *)header message: (NSString *)text button1: (NSString *)button1 
					   button2: (NSString *)button2 out1: (NSString **) out1;
+ (NSUInteger) queryWithFields: (NSString *)header message: (NSString *)text button1: (NSString *)button1
                                          button2: (NSString *)button2 out1: (NSString **) out1 secure: (bool) secure
                                          isSwitch: (bool) isSwitch enableCB: (bool) enableCB savePassword:(bool *) savePassword;
+ (NSUInteger) queryWithFieldsUseDouble: (NSString *)header message: (NSString *)text button1: (NSString *)button1
					   button2: (NSString *)button2 out1: (NSString **) out1 secure1: (bool) secure1 out2: (NSString **) out2 secure2:(bool) secure2;
+ (NSUInteger) queryWithFieldsUseDouble: (NSString *)header message: (NSString *)text button1: (NSString *)button1
					   button2: (NSString *)button2 out1: (NSString **) out1 out2: (NSString **) out2;
+ (void) alertError: (NSString *) func_name code: (long) code;

@end

@interface ModalAlertDelegate : NSObject <UIAlertViewDelegate>
{
    UIAlertView *alertView;
    int index;
}
@property int index;
- (int) show;
@end

@interface CustomAlertController : UIAlertController

- (void)show;

@end
