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
internal val leaderboard: ImageVector
  get() {
    if (_leaderboard != null) {
      return _leaderboard!!
    }
    _leaderboard =
      ImageVector.Builder(
          name = "leaderboard",
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
            moveTo(2f, 21f)
            verticalLineTo(9f)
            horizontalLineTo(7.5f)
            verticalLineTo(21f)
            horizontalLineTo(2f)
            close()
            moveToRelative(7.25f, 0f)
            verticalLineTo(3f)
            horizontalLineToRelative(5.5f)
            verticalLineTo(21f)
            horizontalLineTo(9.25f)
            close()
            moveToRelative(7.25f, 0f)
            verticalLineTo(11f)
            horizontalLineTo(22f)
            verticalLineTo(21f)
            horizontalLineTo(16.5f)
            close()
          }
        }
        .build()
    return _leaderboard!!
  }

private var _leaderboard: ImageVector? = null
