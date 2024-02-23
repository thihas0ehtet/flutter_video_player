import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class CustomPlayer extends StatefulWidget {
  final String streamUrl;

  const CustomPlayer({super.key, required this.streamUrl});

  @override
  State<CustomPlayer> createState() => _CustomPlayerState();
}

class _CustomPlayerState extends State<CustomPlayer>
    with WidgetsBindingObserver {
  var viewPlayerController;
  late MethodChannel channel;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    channel = const MethodChannel('native_player');
    channel.setMethodCallHandler((_) => _platformCallHandler(_, context));
  }

  static Future<dynamic> _platformCallHandler(
      MethodCall call, BuildContext context) async {
    switch (call.method) {
      case 'notifyCurrentTimeStamp':
        String data = call.arguments;
        print('Received timestamp in second from player: $data');
        break;
      case 'playNextVideo':
        String contentId = call.arguments;
        print('Received contentID to play player: $contentId');
        break;
      case 'callBack':
        Navigator.of(context).pop();
        break;
      default:
        print('Unrecognized method: ${call.method}');
    }
  }

  @override
  void dispose() {
    super.dispose();
    WidgetsBinding.instance.removeObserver(this);
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    switch (state) {
      case AppLifecycleState.resumed:
        viewPlayerController.resumeVideo();
        break;
      case AppLifecycleState.paused:
        viewPlayerController.pauseVideo();
        break;
      default:
        break;
    }
  }

  @override
  Widget build(BuildContext context) {
    NativePlayer nativePlayer = NativePlayer(
        onCreated: onViewPlayerCreated, streamUrl: widget.streamUrl);

    return nativePlayer;
  }

  void onViewPlayerCreated(viewPlayerController) {
    this.viewPlayerController = viewPlayerController;
  }
}

class _VideoPlayerState extends State<NativePlayer> {
  @override
  Widget build(BuildContext context) {
    return AndroidView(
      viewType: 'PlayerView',
      onPlatformViewCreated: onPlatformViewCreated,
      creationParams: <String, dynamic>{
        "videoURL": widget.streamUrl,
        'contentId': '1256',
        'videoName': 'Testing Video (Big Bunny)',
        'userNumber': '12',
        'previousId': '3',
        'nextId': '15',
        'currentTimestamp': 200.0,
        'adsStreaming': const [
          {
            'adsStreamingUrl': 'https://srv.myanmarads.net/vast?z=90014',
            'adsStartTime': '0'
          },
          {
            'adsStreamingUrl': 'https://srv.myanmarads.net/vast?z=90014',
            'adsStartTime': '60'
          }
        ],
      },
      creationParamsCodec: const StandardMessageCodec(),
    );
  }

  Future<void> onPlatformViewCreated(id) async {
    widget.onCreated(NativePlayerController.init(id));
  }
}

typedef NativePlayerCreatedCallback = void Function(
    NativePlayerController controller);

class NativePlayerController {
  MethodChannel? channel;

  NativePlayerController.init(int id) {
    channel = const MethodChannel('native_player');
  }

  Future<void> loadUrl(String url) async {
    return channel!.invokeMethod('loadUrl', url);
  }

  Future<void> pauseVideo() async {
    return channel!.invokeMethod('pauseVideo', 'pauseVideo');
  }

  Future<void> resumeVideo() async {
    return channel!.invokeMethod('resumeVideo', 'resumeVideo');
  }
}

class NativePlayer extends StatefulWidget {
  final NativePlayerCreatedCallback onCreated;

  final String streamUrl;

  const NativePlayer({
    super.key,
    required this.onCreated,
    required this.streamUrl,
  });

  @override
  State<StatefulWidget> createState() => _VideoPlayerState();
}
