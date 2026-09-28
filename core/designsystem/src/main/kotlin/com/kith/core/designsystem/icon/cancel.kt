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
internal val cancel: ImageVector
  get() {
    if (_cancel != null) {
      return _cancel!!
    }
    _cancel =
      ImageVector.Builder(
          name = "cancel",
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
            moveTo(12f, 2f)
            curveTo(6.47f, 2f, 2f, 6.47f, 2f, 12f)
            reflectiveCurveTo(6.47f, 22f, 12f, 22f)
            reflectiveCurveTo(22f, 17.53f, 22f, 12f)
            reflectiveCurveTo(17.53f, 2f, 12f, 2f)
            close()
            moveTo(17f, 15.59f)
            lineTo(15.59f, 17f)
            lineTo(12f, 13.41f)
            lineTo(8.41f, 17f)
            lineTo(7f, 15.59f)
            lineTo(10.59f, 12f)
            lineTo(7f, 8.41f)
            lineTo(8.41f, 7f)
            lineTo(12f, 10.59f)
            lineTo(15.59f, 7f)
            lineTo(17f, 8.41f)
            lineTo(13.41f, 12f)
            lineTo(17f, 15.59f)
            close()
          }
        }
        .build()
    return _cancel!!
  }

private var _cancel: ImageVector? = null
