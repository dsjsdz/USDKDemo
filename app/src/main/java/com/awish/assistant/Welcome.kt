package com.awish.assistant

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Welcome(navController: NavHostController) {
  // 用 Pair<Int, String> 存储编号和文字
  val options = listOf(
    1 to "电机",
    2 to "电磁锁",
    3 to "履带"
  )

  // 记录选中的编号
  var selectedOptionId by remember { mutableIntStateOf(1) }

  var expanded by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(stringResource(id = R.string.app_name)) },
        actions = {
          IconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More options")
          }
          DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
          ) {
            DropdownMenuItem(
              text = { Text(text = stringResource(id = R.string.nav_about)) },
              onClick = {
                expanded = false
                navController.navigate("setting")
              }
            )
          }
        }
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(24.dp),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Image(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = "logo",
        modifier = Modifier
          .width(320.dp)
          .height(68.dp),
        contentScale = ContentScale.Crop
      )

      Spacer(modifier = Modifier.height(16.dp))

      Text(text = stringResource(id = R.string.app_name))

      Spacer(modifier = Modifier.height(16.dp))

      Text(text = "请选择出货类型", style = MaterialTheme.typography.titleMedium)

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        options.forEach { (id, label) ->
          Box(
            modifier = Modifier
              .width(100.dp)
              .height(40.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(
                if (selectedOptionId == id) MaterialTheme.colorScheme.primary
                else Color.Gray.copy(alpha = 0.2f)
              )
              .border(
                width = 1.dp,
                color = if (selectedOptionId == id) MaterialTheme.colorScheme.primary
                else Color.Gray,
                shape = RoundedCornerShape(8.dp)
              )
              .clickable { selectedOptionId = id },
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "$id: $label",
              color = if (selectedOptionId == id) Color.White else Color.Black
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      val selectedLabel = options.firstOrNull { it.first == selectedOptionId }?.second ?: ""
      Text(text = "当前选择：$selectedOptionId - $selectedLabel")
    }
  }
}
