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
internal val leaderboard_outlined: ImageVector
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
            moveTo(4f, 19f)
            horizontalLineTo(8f)
            verticalLineTo(11f)
            horizontalLineTo(4f)
            verticalLineToRelative(8f)
            close()
            moveToRelative(6f, 0f)
            horizontalLineToRelative(4f)
            verticalLineTo(5f)
            horizontalLineTo(10f)
            verticalLineTo(19f)
            close()
            moveToRelative(6f, 0f)
            horizontalLineToRelative(4f)
            verticalLineTo(13f)
            horizontalLineTo(16f)
            verticalLineToRelative(6f)
            close()
            moveTo(2f, 21f)
            verticalLineTo(9f)
            horizontalLineTo(8f)
            verticalLineTo(3f)
            horizontalLineToRelative(8f)
            verticalLineToRelative(8f)
            horizontalLineToRelative(6f)
            verticalLineTo(21f)
            horizontalLineTo(2f)
            close()
          }
        }
        .build()
    return _leaderboard!!
  }

private var _leaderboard: ImageVector? = null
