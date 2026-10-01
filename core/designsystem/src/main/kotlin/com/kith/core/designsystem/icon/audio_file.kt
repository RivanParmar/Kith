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
internal val audio_file: ImageVector
  get() {
    if (_audio_file != null) {
      return _audio_file!!
    }
    _audio_file =
      ImageVector.Builder(
          name = "audio_file",
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
            moveTo(10.75f, 19f)
            quadToRelative(0.95f, 0f, 1.6f, -0.65f)
            reflectiveQuadTo(13f, 16.75f)
            verticalLineTo(13f)
            horizontalLineToRelative(2f)
            quadToRelative(0.43f, 0f, 0.71f, -0.29f)
            quadTo(16f, 12.43f, 16f, 12f)
            reflectiveQuadTo(15.71f, 11.29f)
            reflectiveQuadTo(15f, 11f)
            horizontalLineTo(13f)
            quadToRelative(-0.42f, 0f, -0.71f, 0.29f)
            reflectiveQuadTo(12f, 12f)
            verticalLineToRelative(2.88f)
            quadToRelative(-0.27f, -0.2f, -0.59f, -0.29f)
            reflectiveQuadTo(10.75f, 14.5f)
            quadToRelative(-0.95f, 0f, -1.6f, 0.65f)
            reflectiveQuadTo(8.5f, 16.75f)
            reflectiveQuadToRelative(0.65f, 1.6f)
            reflectiveQuadTo(10.75f, 19f)
            close()
            moveTo(6f, 22f)
            quadTo(5.18f, 22f, 4.59f, 21.41f)
            reflectiveQuadTo(4f, 20f)
            verticalLineTo(4f)
            quadTo(4f, 3.17f, 4.59f, 2.59f)
            reflectiveQuadTo(6f, 2f)
            horizontalLineToRelative(7.18f)
            quadToRelative(0.4f, 0f, 0.76f, 0.15f)
            reflectiveQuadToRelative(0.64f, 0.43f)
            lineToRelative(4.85f, 4.85f)
            quadTo(19.7f, 7.7f, 19.85f, 8.06f)
            quadTo(20f, 8.42f, 20f, 8.82f)
            verticalLineTo(20f)
            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
            reflectiveQuadTo(18f, 22f)
            horizontalLineTo(6f)
            close()
            moveTo(13f, 8f)
            verticalLineTo(4f)
            horizontalLineTo(6f)
            verticalLineTo(20f)
            horizontalLineTo(18f)
            verticalLineTo(9f)
            horizontalLineTo(14f)
            quadTo(13.58f, 9f, 13.29f, 8.71f)
            reflectiveQuadTo(13f, 8f)
            close()
            moveTo(6f, 4f)
            verticalLineTo(8f)
            quadTo(6f, 8.42f, 6f, 8.71f)
            reflectiveQuadTo(6f, 9f)
            verticalLineTo(4f)
            verticalLineTo(8f)
            quadTo(6f, 8.42f, 6f, 8.71f)
            reflectiveQuadTo(6f, 9f)
            verticalLineTo(20f)
            verticalLineTo(4f)
            close()
          }
        }
        .build()
    return _audio_file!!
  }

private var _audio_file: ImageVector? = null
