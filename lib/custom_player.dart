import 'dart:io' show Platform;

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class CustomPlayer extends StatefulWidget {
  final String? streamUrl;

  const CustomPlayer({
    super.key,
    // this.streamUrl =
    //     "https://storage.googleapis.com/gvabox/media/samples/stock.mp4"
    this.streamUrl =
        "https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8",
    // this.streamUrl =
    //     "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
  });

  @override
  State<CustomPlayer> createState() => _CustomPlayerState();
}

class _CustomPlayerState extends State<CustomPlayer>
    with WidgetsBindingObserver {
  var viewPlayerController;
  late MethodChannel _channel;
  bool isNormalScreen = true;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    _channel = const MethodChannel('bms_video_player');
  }

  @override
  void dispose() {
    super.dispose();
    WidgetsBinding.instance.removeObserver(this);
    if (Platform.isIOS) {
      _channel.invokeMethod('pauseVideo', 'pauseVideo');
    }
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
    var x = 0.0;
    var y = 0.0;
    var width = 400.0;
    var height = isNormalScreen ? 270.0 : MediaQuery.of(context).size.height;

    BmsVideoPlayer videoPlayer = BmsVideoPlayer(
        onCreated: onViewPlayerCreated,
        x: x,
        y: y,
        width: width,
        height: height,
        streamUrl: widget.streamUrl);

    return videoPlayer;
  }

  void onViewPlayerCreated(viewPlayerController) {
    this.viewPlayerController = viewPlayerController;
  }
}

class _VideoPlayerState extends State<BmsVideoPlayer> {
  String viewType = 'PlayerView';

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      behavior: HitTestBehavior.opaque,
      child: nativeView(),
    );
  }

  nativeView() {
    if (defaultTargetPlatform == TargetPlatform.android) {
      return AndroidView(
        viewType: viewType,
        onPlatformViewCreated: onPlatformViewCreated,
        creationParams: <String, dynamic>{
          // "x": widget.x,
          // "y": widget.y,
          // "width": widget.width,
          // "height": widget.height,
          "videoURL": widget.streamUrl
        },
        creationParamsCodec: const StandardMessageCodec(),
      );
    } else {
      return UiKitView(
        viewType: viewType,
        onPlatformViewCreated: onPlatformViewCreated,
        creationParams: <String, dynamic>{
          "x": widget.x,
          "y": widget.y,
          "width": widget.width,
          "height": widget.height,
          "videoURL": widget.streamUrl
        },
        creationParamsCodec: const StandardMessageCodec(),
      );
    }
  }

  Future<void> onPlatformViewCreated(id) async {
    widget.onCreated(BmsVideoPlayerController.init(id));
  }
}

typedef BmsVideoPlayerCreatedCallback = void Function(
    BmsVideoPlayerController controller);

class BmsVideoPlayerController {
  MethodChannel? _channel;

  BmsVideoPlayerController.init(int id) {
    _channel = const MethodChannel('bms_video_player');
  }

  Future<void> loadUrl(String url) async {
    return _channel!.invokeMethod('loadUrl', url);
  }

  Future<void> pauseVideo() async {
    return _channel!.invokeMethod('pauseVideo', 'pauseVideo');
  }

  Future<void> resumeVideo() async {
    return _channel!.invokeMethod('resumeVideo', 'resumeVideo');
  }
}

class BmsVideoPlayer extends StatefulWidget {
  final BmsVideoPlayerCreatedCallback onCreated;
  final x;
  final y;
  final width;
  final height;
  final streamUrl;

  const BmsVideoPlayer({
    super.key,
    required this.onCreated,
    @required this.x,
    @required this.y,
    @required this.width,
    @required this.height,
    @required this.streamUrl,
  });

  @override
  State<StatefulWidget> createState() => _VideoPlayerState();
}
