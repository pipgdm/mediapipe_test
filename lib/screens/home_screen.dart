import 'dart:io';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import '../services/native_bridge.dart';
import '../widgets/avatar_painter.dart';

class HomeScreen extends StatefulWidget {
  @override
  _HomeScreenState createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  File? _image;
  List<dynamic>? _landmarks;

  Future<void> _pickImage() async {
    final picker = ImagePicker();
    final pickedFile = await picker.pickImage(source: ImageSource.gallery);

    if (pickedFile != null) {
      final result = await NativeBridge.processImage(pickedFile.path);

      if (result != null) {
        print("🧠 Full native result: $result"); // 👈 Debug log

        final path = result['path'] as String?;
        final landmarks = result['landmarks'] as List<dynamic>?;

        if (path != null) {
          setState(() {
            _image = File(path);
            _landmarks = landmarks;
          });
        }
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Face Avatar Demo')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            _image != null
                ? Image.file(_image!, height: 200)
                : Text('No image selected.'),
            SizedBox(height: 20),
            ElevatedButton(
              onPressed: _pickImage,
              child: Text('Upload Photo'),
            ),
            if (_landmarks != null)
              Expanded(
                child: CustomPaint(
                  painter: AvatarPainter(_landmarks),
                  child: Container(),
                ),
              ),
          ],
        ),
      ),
    );
  }
}
