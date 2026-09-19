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
internal val person: ImageVector
  get() {
    if (_person != null) {
      return _person!!
    }
    _person =
      ImageVector.Builder(
          name = "person",
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
            moveTo(9.18f, 10.83f)
            quadTo(8f, 9.65f, 8f, 8f)
            reflectiveQuadTo(9.18f, 5.18f)
            reflectiveQuadTo(12f, 4f)
            reflectiveQuadToRelative(2.83f, 1.18f)
            reflectiveQuadTo(16f, 8f)
            reflectiveQuadToRelative(-1.17f, 2.82f)
            reflectiveQuadTo(12f, 12f)
            reflectiveQuadTo(9.18f, 10.83f)
            close()
            moveTo(4f, 20f)
            verticalLineTo(17.2f)
            quadTo(4f, 16.35f, 4.44f, 15.64f)
            quadTo(4.88f, 14.93f, 5.6f, 14.55f)
            quadTo(7.15f, 13.77f, 8.75f, 13.39f)
            reflectiveQuadTo(12f, 13f)
            reflectiveQuadToRelative(3.25f, 0.39f)
            reflectiveQuadToRelative(3.15f, 1.16f)
            quadToRelative(0.72f, 0.38f, 1.16f, 1.09f)
            reflectiveQuadTo(20f, 17.2f)
            verticalLineTo(20f)
            horizontalLineTo(4f)
            close()
          }
        }
        .build()
    return _person!!
  }

private var _person: ImageVector? = null
