#import <React/RCTViewManager.h>

#import <Foundation/Foundation.h>
#import <Foundation/Foundation.h>
#import "React/RCTViewManager.h"
#import "React/RCTUIManager.h"

@interface RCT_EXTERN_MODULE(ReactNativeStraightTableViewManager, RCTViewManager)
RCT_EXPORT_VIEW_PROPERTY(theme,NSDictionary*)
RCT_EXPORT_VIEW_PROPERTY(cols,NSDictionary*)
RCT_EXPORT_VIEW_PROPERTY(rows,NSDictionary*)

@end
