//
//  MiniChartViewManager.m
//  react-native-simple-grid
//
//  Created by Vittorio Cellucci on 2022-10-22.
//

#import <React/RCTViewManager.h>

#import <Foundation/Foundation.h>
#import <Foundation/Foundation.h>
#import "React/RCTViewManager.h"
#import "React/RCTUIManager.h"

@interface RCT_EXTERN_MODULE(MiniChartViewManager, RCTViewManager)
RCT_EXPORT_VIEW_PROPERTY(rowData, NSDictionary*)
RCT_EXPORT_VIEW_PROPERTY(colData, NSDictionary*)
@end
