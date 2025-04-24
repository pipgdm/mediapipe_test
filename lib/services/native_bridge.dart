import 'package:flutter/services.dart';

class NativeBridge {
  static const _channel = MethodChannel('mediapipe_bridge/channel');

  static Future<Map<String, dynamic>?> processImage(String imagePath) async {
    try {
      final result = await _channel.invokeMethod('processImage', {'path': imagePath});
      if (result is Map) {
        return Map<String, dynamic>.from(result);
      }
    } catch (e) {
      print('Native error: $e');
    }
    return null;
  }
}
