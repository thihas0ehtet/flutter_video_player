import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class PlatformService {
  static const platform = MethodChannel('mahar.com/exoplayer');

  static void setupChannel(BuildContext context) {
    platform.setMethodCallHandler((_) => _platformCallHandler(_, context));
  }

  static Future<dynamic> _platformCallHandler(
      MethodCall call, BuildContext context) async {
    switch (call.method) {
      case 'callBack':
        List result = call.arguments.split(',');
        String sec = result[0];
        String duration = result[1];

        debugPrint('Received timestamp from back action $sec and $duration');
        break;
      case 'notifyCurrentTimeStamp':
        String data = call.arguments;
        print('Received timestamp in second from player: $data');
        break;

      //Previous Action
      case 'previousAction':
        List result = call.arguments.split(",");
        String sec = result[0];
        String duration = result[1];
        String contentId = result[2];
        print('Received data from previousAction: $sec $duration $contentId');

        break;

      //Next Action
      case 'nextAction':
        List result = call.arguments.split(",");
        String sec = result[0];
        String duration = result[1];
        String contentId = result[2];
        print('Received data from previousAction: $sec $duration $contentId');
        break;

      default:
        print('Unrecognized method: ${call.method}');
    }
  }
}
