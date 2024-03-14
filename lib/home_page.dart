import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:video_player/platform_service.dart';

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  // Define the method channel
  static const platform = MethodChannel('mahar.com/exoplayer');

  // Function to show the native view
  static Future<void> showExoPlayerView({int timestamp = 0}) async {
    try {
      await platform.invokeMethod('PlayerView', {
        'streamUrl':
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
        'userNumber': 'F13KD',
        'videoName': 'Testing Video (Big Bunny)',
        'currentTimestamp': timestamp.toString(),
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
      });
    } on PlatformException catch (e) {
      debugPrint("Failed to show native view: '${e.message}'.");
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        title: const Text("Player"),
      ),
      body: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 20),
            child: Center(
              child: Column(
                children: [
                  ElevatedButton(
                      onPressed: () {
                        showExoPlayerView(timestamp: 200);
                        PlatformService.setupChannel(context);
                      },
                      child: const Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Icon(Icons.play_arrow),
                          SizedBox(
                            width: 10,
                          ),
                          Text("Play")
                        ],
                      )),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}
