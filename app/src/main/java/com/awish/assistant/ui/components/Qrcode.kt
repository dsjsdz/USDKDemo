package com.awish.assistant.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.awish.assistant.R

@Composable
fun Qrcode() {
  TextDivider(
    text = stringResource(id = R.string.share_sns),
  )
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(bottom = 16.dp, top = 8.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Image(
      painter = painterResource(id = R.drawable.logo),
      contentDescription = "",
      modifier = Modifier
        .fillMaxWidth(fraction = 8f / 12f)
        .padding(16.dp),
      contentScale = ContentScale.Fit
    )
  }
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(bottom = 16.dp, top = 8.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Image(
      painter = painterResource(id = R.drawable.qrcode),
      contentDescription = "",
      modifier = Modifier
        .fillMaxWidth(fraction = 8f / 12f)
        .aspectRatio(1f)
        .padding(16.dp),
      contentScale = ContentScale.Fit
    )
    Text(
      text = stringResource(id = R.string.follow_us),
      style = MaterialTheme.typography.titleMedium,
      color = Color.Gray,
    )
  }
}
