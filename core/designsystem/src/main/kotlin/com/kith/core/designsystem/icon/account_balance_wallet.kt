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
internal val account_balance_wallet: ImageVector
  get() {
    if (_account_balance_wallet != null) {
      return _account_balance_wallet!!
    }
    _account_balance_wallet =
      ImageVector.Builder(
          name = "account_balance_wallet",
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
            moveTo(5f, 19f)
            verticalLineTo(5f)
            quadTo(5f, 5f, 5f, 6.11f)
            quadTo(5f, 7.22f, 5f, 9f)
            verticalLineToRelative(6f)
            quadToRelative(0f, 1.77f, 0f, 2.89f)
            reflectiveQuadTo(5f, 19f)
            close()
            moveToRelative(0f, 2f)
            quadTo(4.18f, 21f, 3.59f, 20.41f)
            reflectiveQuadTo(3f, 19f)
            verticalLineTo(5f)
            quadTo(3f, 4.17f, 3.59f, 3.59f)
            reflectiveQuadTo(5f, 3f)
            horizontalLineTo(19f)
            quadToRelative(0.83f, 0f, 1.41f, 0.59f)
            reflectiveQuadTo(21f, 5f)
            verticalLineTo(7.5f)
            horizontalLineTo(19f)
            verticalLineTo(5f)
            horizontalLineTo(5f)
            verticalLineTo(19f)
            horizontalLineTo(19f)
            verticalLineTo(16.5f)
            horizontalLineToRelative(2f)
            verticalLineTo(19f)
            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
            reflectiveQuadTo(19f, 21f)
            horizontalLineTo(5f)
            close()
            moveToRelative(8f, -4f)
            quadToRelative(-0.82f, 0f, -1.41f, -0.59f)
            reflectiveQuadTo(11f, 15f)
            verticalLineTo(9f)
            quadTo(11f, 8.17f, 11.59f, 7.59f)
            reflectiveQuadTo(13f, 7f)
            horizontalLineToRelative(7f)
            quadToRelative(0.83f, 0f, 1.41f, 0.59f)
            reflectiveQuadTo(22f, 9f)
            verticalLineToRelative(6f)
            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
            reflectiveQuadTo(20f, 17f)
            horizontalLineTo(13f)
            close()
            moveToRelative(7f, -2f)
            verticalLineTo(9f)
            horizontalLineTo(13f)
            verticalLineToRelative(6f)
            horizontalLineToRelative(7f)
            close()
            moveTo(17.06f, 13.06f)
            quadTo(17.5f, 12.63f, 17.5f, 12f)
            reflectiveQuadTo(17.06f, 10.94f)
            reflectiveQuadTo(16f, 10.5f)
            reflectiveQuadToRelative(-1.06f, 0.44f)
            reflectiveQuadTo(14.5f, 12f)
            reflectiveQuadToRelative(0.44f, 1.06f)
            reflectiveQuadTo(16f, 13.5f)
            reflectiveQuadToRelative(1.06f, -0.44f)
            close()
          }
        }
        .build()
    return _account_balance_wallet!!
  }

private var _account_balance_wallet: ImageVector? = null
