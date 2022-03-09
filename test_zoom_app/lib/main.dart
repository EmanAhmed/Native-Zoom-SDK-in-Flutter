import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(title: 'Zoom App'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({Key? key, required this.title}) : super(key: key);
  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  static const platform = MethodChannel('samples.flutter.dev/battery');

  void _joinZoom() {
    platform.invokeMethod('joinZoom', <String, String>{
      'meetingId': '99335630926',
      'password': 'M0hGZGJRbTNmQzlJN3BabmpuZXNZZz09',
      'name': 'Eman Ahmed'
    });
  }

  Future<void> _initZoom() async {
    try {
      final String result = await platform.invokeMethod('initZoom');
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(
        content: Text('Done'),
      ));
    } on PlatformException catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(
        content: Text("Failed"),
      ));
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        children: <Widget>[
          ElevatedButton(onPressed: _initZoom, child: Text("Initialize Zoom")),
          ElevatedButton(onPressed: _joinZoom, child: Text("Join Meeting")),
        ],
      ),
      // This trailing comma makes auto-formatting nicer for build methods.
    );
  }
}
