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
internal val password: ImageVector
  get() {
    if (_password != null) {
      return _password!!
    }
    _password =
      ImageVector.Builder(
          name = "password",
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
            moveTo(2f, 19f)
            verticalLineTo(17f)
            horizontalLineTo(22f)
            verticalLineToRelative(2f)
            horizontalLineTo(2f)
            close()
            moveTo(3.15f, 12.95f)
            lineTo(1.85f, 12.2f)
            lineTo(2.7f, 10.7f)
            horizontalLineTo(1f)
            verticalLineTo(9.2f)
            horizontalLineTo(2.7f)
            lineTo(1.85f, 7.75f)
            lineTo(3.15f, 7f)
            lineTo(4f, 8.45f)
            lineTo(4.85f, 7f)
            lineToRelative(1.3f, 0.75f)
            lineTo(5.3f, 9.2f)
            horizontalLineTo(7f)
            verticalLineToRelative(1.5f)
            horizontalLineTo(5.3f)
            lineToRelative(0.85f, 1.5f)
            lineToRelative(-1.3f, 0.75f)
            lineTo(4f, 11.45f)
            lineToRelative(-0.85f, 1.5f)
            close()
            moveToRelative(8f, 0f)
            lineTo(9.85f, 12.2f)
            lineTo(10.7f, 10.7f)
            horizontalLineTo(9f)
            verticalLineTo(9.2f)
            horizontalLineToRelative(1.7f)
            lineTo(9.85f, 7.75f)
            lineTo(11.15f, 7f)
            lineTo(12f, 8.45f)
            lineTo(12.85f, 7f)
            lineToRelative(1.3f, 0.75f)
            lineTo(13.3f, 9.2f)
            horizontalLineTo(15f)
            verticalLineToRelative(1.5f)
            horizontalLineTo(13.3f)
            lineToRelative(0.85f, 1.5f)
            lineToRelative(-1.3f, 0.75f)
            lineTo(12f, 11.45f)
            lineToRelative(-0.85f, 1.5f)
            close()
            moveToRelative(8f, 0f)
            lineTo(17.85f, 12.2f)
            lineTo(18.7f, 10.7f)
            horizontalLineTo(17f)
            verticalLineTo(9.2f)
            horizontalLineToRelative(1.7f)
            lineTo(17.85f, 7.75f)
            lineTo(19.15f, 7f)
            lineTo(20f, 8.45f)
            lineTo(20.85f, 7f)
            lineToRelative(1.3f, 0.75f)
            lineTo(21.3f, 9.2f)
            horizontalLineTo(23f)
            verticalLineToRelative(1.5f)
            horizontalLineTo(21.3f)
            lineToRelative(0.85f, 1.5f)
            lineToRelative(-1.3f, 0.75f)
            lineTo(20f, 11.45f)
            lineToRelative(-0.85f, 1.5f)
            close()
          }
        }
        .build()
    return _password!!
  }

private var _password: ImageVector? = null
