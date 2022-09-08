#import <React/RCTViewManager.h>

#import <Foundation/Foundation.h>
#import <Foundation/Foundation.h>
#import "React/RCTViewManager.h"
#import "React/RCTUIManager.h"

@interface RCT_EXTERN_MODULE(ReactNativeStraightTableViewManager, RCTViewManager)
RCT_EXPORT_VIEW_PROPERTY(theme, NSDictionary*)
RCT_EXPORT_VIEW_PROPERTY(size, NSDictionary*)
RCT_EXPORT_VIEW_PROPERTY(cols, NSDictionary*)
RCT_EXPORT_VIEW_PROPERTY(rows, NSDictionary*)
RCT_EXPORT_VIEW_PROPERTY(containerWidth, NSNumber*)
RCT_EXPORT_VIEW_PROPERTY(clearSelections, NSString*)
RCT_EXPORT_VIEW_PROPERTY(onEndReached, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onSelectionsChanged, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onHeaderPressed, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onDoubleTap, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(freezeFirstColumn, BOOL)
RCT_EXPORT_VIEW_PROPERTY(cellContentStyle, NSDictionary*)
RCT_EXPORT_VIEW_PROPERTY(headerContentStyle, NSDictionary*)
RCT_EXPORT_VIEW_PROPERTY(isDataView, BOOL)
RCT_EXPORT_VIEW_PROPERTY(isPercent, BOOL)
RCT_EXPORT_VIEW_PROPERTY(name, NSString*)
@end
