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
public val star_rate: ImageVector
  get() {
    if (_star_rate != null) {
      return _star_rate!!
    }
    _star_rate =
      ImageVector.Builder(
          name = "star_rate",
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
            moveTo(9.6f, 15.65f)
            lineTo(12f, 13.8f)
            lineToRelative(2.4f, 1.85f)
            lineTo(13.5f, 12.6f)
            lineTo(15.75f, 11f)
            horizontalLineToRelative(-2.8f)
            lineTo(12f, 7.9f)
            lineTo(11.05f, 11f)
            horizontalLineTo(8.25f)
            lineToRelative(2.25f, 1.6f)
            lineTo(9.6f, 15.65f)
            close()
            moveTo(5.83f, 21f)
            lineTo(8.15f, 13.4f)
            lineTo(2f, 9f)
            horizontalLineTo(9.6f)
            lineTo(12f, 1f)
            lineToRelative(2.4f, 8f)
            horizontalLineTo(22f)
            lineToRelative(-6.15f, 4.4f)
            lineTo(18.18f, 21f)
            lineTo(12f, 16.3f)
            lineTo(5.83f, 21f)
            close()
            moveTo(12f, 11.77f)
            close()
          }
        }
        .build()
    return _star_rate!!
  }

private var _star_rate: ImageVector? = null
