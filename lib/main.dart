import 'package:flutter/material.dart';
import 'screens/home_screen.dart'; // make sure this path is correct

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Face Avatar Demo',
      home: HomeScreen(),
    );
  }
}
