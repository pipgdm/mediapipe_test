// lib/main.dart
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:flutter/services.dart';
import 'dart:io';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Photo Upload Demo',
      home: PhotoUploadScreen(),
    );
  }
}

class PhotoUploadScreen extends StatefulWidget {
  @override
  _PhotoUploadScreenState createState() => _PhotoUploadScreenState();
}

class _PhotoUploadScreenState extends State<PhotoUploadScreen> {
  File? _image;

  Future<void> _pickImage() async {
    final picker = ImagePicker();
    final pickedFile = await picker.pickImage(source: ImageSource.gallery);

    if (pickedFile != null) {
      setState(() => _image = File(pickedFile.path));
      await _sendToNative(pickedFile.path); // 👈 Add this line

    }
  }

  Future<void> _sendToNative(String imagePath) async {
    const platform = MethodChannel('mediapipe_bridge/channel');
    try {
      final result = await platform.invokeMethod('processImage', {'path': imagePath});

      if (result is Map) {
        final path = result['path'] as String?;
        final message = result['message'] as String?;

        if (path != null) {
          setState(() {
            _image = File(path); // show landmarked image
          });
        }

        if (message != null) {
          print("🧠 Native says: $message");
        }
      }

    } catch (e) {
      print('Error: $e');
    }
  }


  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Upload a Photo')),
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
          ],
        ),
      ),
    );
  }
}
