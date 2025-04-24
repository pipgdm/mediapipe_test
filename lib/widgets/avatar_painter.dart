import 'package:flutter/material.dart';

class AvatarPainter extends CustomPainter {
  final List<dynamic>? landmarks;

  AvatarPainter(this.landmarks);

  @override
  void paint(Canvas canvas, Size size) {
    if (landmarks == null) return;

    final paint = Paint()
      ..color = Colors.green
      ..style = PaintingStyle.fill;

    for (var point in landmarks!) {
      final x = (point['x'] as double) * size.width;
      final y = (point['y'] as double) * size.height;
      canvas.drawCircle(Offset(x, y), 3.5, paint);
    }
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => true;
}
