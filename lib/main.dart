import 'package:connectivity_plus/connectivity_plus.dart';
import 'package:flutter/material.dart';
import 'package:video_player/custom_player.dart';
import 'package:video_player/my_player_view.dart';
import 'package:http/http.dart' as http;
import 'package:public_ip/public_ip.dart' as publicIP;

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Player',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: const MyHomePage(title: 'Flutter Player'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
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
        // The response body is the IP in plain text, so just      // return it as-is.      return response.body;
      } else {
        // The request failed with a non-200 code      // The ipify.org API has a lot of guaranteed uptime       // promises, so this shouldn't ever actually happen.      print(response.statusCode);
        print(response.body);
        return "Error ";
      }
    } catch (e) {
      // Request failed due to an error, most likely because
      // the phone isn't connected to the internet.
      print(e);
      return "Error";
    }
  }

  getConnectivity() {
    Connectivity()
        .onConnectivityChanged
        .listen((ConnectivityResult result) async {
      String ip = await getPublicIP();
      print(ip);
      setState(() {
        publicIPAddress = ip;
        _connectivityResult = result;
      });
      // getIP();
    });
  }

  getIP() async {
    String ip = await publicIP.getState();
    print(ip);

    setState(() {
      publicIPAddress = ip;
    });

    // print(ip);
    // print("getASN");
    // print(await publicIP.getASN());
    // print("getISP");
    // print(await publicIP.getISP());
    // print("getCountry");
    // print(await publicIP.getCountry());
    // print("getState");
    // print(await publicIP.getState());
    // print("getLatitude");
    // print(await publicIP.getLatitude());
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
        title: Text(widget.title),
      ),
      body: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          ElevatedButton(
              onPressed: () => getConnectivity(), child: const Text("Show IP")),
          const SizedBox(
            height: 20,
          ),
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
        ],
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => Navigator.of(context).push(
            MaterialPageRoute(builder: (context) => const CustomPlayer())),
        tooltip: 'Play',
        child: const Icon(Icons.play_arrow),
      ),
    );
  }
}
