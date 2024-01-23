import 'dart:io';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

const myPlayView = "MyPlayerView";

class MyPlayerView extends StatefulWidget {
  const MyPlayerView({super.key});

  @override
  State<MyPlayerView> createState() => _MyPlayerViewState();
}

class _MyPlayerViewState extends State<MyPlayerView> {
  final Map<String, dynamic> creationParams = <String, dynamic>{};

  @override
  void initState() {
    super.initState();
    creationParams['url'] =
        "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
  }

  @override
  Widget build(BuildContext context) {
    return Platform.isAndroid
        ? AndroidView(
            viewType: myPlayView,
            layoutDirection: TextDirection.ltr,
            creationParams: creationParams,
            creationParamsCodec: const StandardMessageCodec(),
          )
        : UiKitView(
            viewType: myPlayView,
            layoutDirection: TextDirection.ltr,
            creationParams: creationParams,
            creationParamsCodec: const StandardMessageCodec(),
          );
  }
}
