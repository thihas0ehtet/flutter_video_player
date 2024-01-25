import 'package:connectivity_plus/connectivity_plus.dart';
import 'package:flutter/material.dart';
import 'package:video_player/custom_player.dart';
import 'package:http/http.dart' as http;
import 'package:video_player/player.dart';

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
              onPressed: () => Navigator.of(context).push(
                  MaterialPageRoute(builder: (context) => const CustomPlayer())),
              child: const Icon(Icons.play_arrow)),
          const SizedBox(
            height: 40,
          ),
        ],
      ),
    );
  }
}