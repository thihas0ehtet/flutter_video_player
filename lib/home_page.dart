import 'package:connectivity_plus/connectivity_plus.dart';
import 'package:flutter/material.dart';
import 'package:video_player/custom_player.dart';
import 'package:http/http.dart' as http;

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  String publicIPAddress = "";
  ConnectivityResult _connectivityResult = ConnectivityResult.none;

  @override
  void initState() {
    super.initState();
    getConnectivity();
  }

  getPublicIP() async {
    try {
      const url = 'https://api.ipify.org';
      var response = await http.get(Uri.parse(url));
      if (response.statusCode == 200) {
        return response.body;
      } else {
        return "Error";
      }
    } catch (e) {
      return "Error";
    }
  }

  getConnectivity() {
    Connectivity()
        .onConnectivityChanged
        .listen((ConnectivityResult result) async {
      String ip = await getPublicIP();
      setState(() {
        publicIPAddress = ip;
        _connectivityResult = result;
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    String connectionStatus = "";

    switch (_connectivityResult) {
      case ConnectivityResult.mobile:
        connectionStatus = "Mobile data connection is being used.";
        break;
      case ConnectivityResult.wifi:
        connectionStatus = "Wi-Fi connection is being used.";
        break;
      case ConnectivityResult.bluetooth:
        connectionStatus = "Bluetooth connection is being used.";
        break;
      case ConnectivityResult.ethernet:
        connectionStatus = "Ethernet connection is being used.";
        break;
      case ConnectivityResult.other:
        connectionStatus = "Other connection is being used.";
        break;
      case ConnectivityResult.vpn:
        connectionStatus = "Vpn connection is being used.";
        break;
      case ConnectivityResult.none:
        connectionStatus = "No connection.";
        publicIPAddress = "No IP Address";
        break;
    }

    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        title: const Text("Player"),
      ),
      body: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          const Center(
            child: Text(
              "Connection Status:",
              style: TextStyle(fontSize: 18),
            ),
          ),
          Center(
            child: Text(
              connectionStatus,
              style: const TextStyle(
                fontSize: 24,
                fontWeight: FontWeight.bold,
              ),
              textAlign: TextAlign.center,
            ),
          ),
          const SizedBox(
            height: 20,
          ),
          Center(
            child: Text(
              publicIPAddress,
              style: const TextStyle(
                fontSize: 24,
                fontWeight: FontWeight.bold,
              ),
              textAlign: TextAlign.center,
            ),
          ),
          const SizedBox(
            height: 40,
          ),
          ElevatedButton(
              onPressed: () => Navigator.of(context).push(MaterialPageRoute(
                  builder: (context) => const CustomPlayer(
                        streamUrl:
                            // "https://mahar-movie.akamaized.net/tkt-0044/playlist.m3u8?hdnea=st=1708268850~exp=1708279650~acl=/*~hmac=16d2e55cceae44fdf548869f260f64b957982ca94713391357c4dfb663f58b97"
                            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                        //  "https://storage.googleapis.com/gvabox/media/samples/stock.mp4",
                        //  "https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8",
                      ))),
              child: const Icon(Icons.play_arrow)),
          const SizedBox(
            height: 40,
          ),
        ],
      ),
    );
  }
}
