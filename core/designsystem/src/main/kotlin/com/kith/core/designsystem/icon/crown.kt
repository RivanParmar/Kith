package com.kith.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("CheckReturnValue")
internal val crown: ImageVector
  get() {
    if (_crown != null) {
      return _crown!!
    }
    _crown =
      ImageVector.Builder(
          name = "crown",
          defaultWidth = 24.dp,
          defaultHeight = 24.dp,
          viewportWidth = 24f,
          viewportHeight = 24f,
        )
        .apply {
          path(
            fill = SolidColor(Color.Black),
            fillAlpha = 1f,
            stroke = null,
            strokeAlpha = 1f,
            strokeLineWidth = 1f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Bevel,
            strokeLineMiter = 1f,
            pathFillType = PathFillType.NonZero,
          ) {
            moveTo(6f, 20f)
            quadTo(5.58f, 20f, 5.29f, 19.71f)
            quadTo(5f, 19.43f, 5f, 19f)
            reflectiveQuadTo(5.29f, 18.29f)
            reflectiveQuadTo(6f, 18f)
            horizontalLineTo(18f)
            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
            reflectiveQuadTo(19f, 19f)
            reflectiveQuadToRelative(-0.29f, 0.71f)
            reflectiveQuadTo(18f, 20f)
            horizontalLineTo(6f)
            close()
            moveTo(6.7f, 16.5f)
            quadToRelative(-0.72f, 0f, -1.29f, -0.48f)
            reflectiveQuadTo(4.73f, 14.83f)
            lineToRelative(-1f, -6.35f)
            quadToRelative(-0.05f, 0f, -0.11f, 0.01f)
            reflectiveQuadTo(3.5f, 8.5f)
            quadTo(2.88f, 8.5f, 2.44f, 8.06f)
            reflectiveQuadTo(2f, 7f)
            reflectiveQuadTo(2.44f, 5.94f)
            reflectiveQuadTo(3.5f, 5.5f)
            reflectiveQuadTo(4.56f, 5.94f)
            reflectiveQuadTo(5f, 7f)
            quadTo(5f, 7.18f, 4.96f, 7.32f)
            reflectiveQuadTo(4.88f, 7.6f)
            lineTo(8f, 9f)
            lineTo(11.13f, 4.72f)
            quadTo(10.85f, 4.52f, 10.68f, 4.2f)
            reflectiveQuadTo(10.5f, 3.5f)
            quadToRelative(0f, -0.63f, 0.44f, -1.06f)
            reflectiveQuadTo(12f, 2f)
            reflectiveQuadToRelative(1.06f, 0.44f)
            reflectiveQuadTo(13.5f, 3.5f)
            quadToRelative(0f, 0.38f, -0.17f, 0.7f)
            reflectiveQuadTo(12.88f, 4.72f)
            lineTo(16f, 9f)
            lineTo(19.13f, 7.6f)
            quadTo(19.08f, 7.47f, 19.04f, 7.32f)
            reflectiveQuadTo(19f, 7f)
            quadTo(19f, 6.38f, 19.44f, 5.94f)
            reflectiveQuadTo(20.5f, 5.5f)
            reflectiveQuadToRelative(1.06f, 0.44f)
            reflectiveQuadTo(22f, 7f)
            reflectiveQuadTo(21.56f, 8.06f)
            reflectiveQuadTo(20.5f, 8.5f)
            quadToRelative(-0.05f, 0f, -0.11f, -0.01f)
            quadTo(20.33f, 8.48f, 20.28f, 8.48f)
            lineToRelative(-1f, 6.35f)
            quadToRelative(-0.13f, 0.72f, -0.69f, 1.2f)
            reflectiveQuadTo(17.3f, 16.5f)
            horizontalLineTo(6.7f)
            close()
            moveToRelative(0f, -2f)
            horizontalLineTo(17.3f)
            lineToRelative(0.65f, -4.18f)
            lineToRelative(-1.15f, 0.5f)
            quadToRelative(-0.65f, 0.28f, -1.32f, 0.1f)
            reflectiveQuadToRelative(-1.1f, -0.75f)
            lineTo(12f, 6.9f)
            lineTo(9.63f, 10.17f)
            quadTo(9.2f, 10.75f, 8.53f, 10.93f)
            reflectiveQuadTo(7.2f, 10.83f)
            lineTo(6.05f, 10.33f)
            lineTo(6.7f, 14.5f)
            close()
            moveToRelative(5.3f, 0f)
            close()
          }
        }
        .build()
    return _crown!!
  }

private var _crown: ImageVector? = null
