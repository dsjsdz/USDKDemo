package com.awish.assistant

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import cc.uling.usdk.USDK
import cc.uling.usdk.board.wz.para.BSReplyPara
import cc.uling.usdk.board.wz.para.HCReplyPara
import cc.uling.usdk.board.wz.para.IOReplyPara
import cc.uling.usdk.board.wz.para.ResetReplyPara
import cc.uling.usdk.board.wz.para.SReplyPara
import cc.uling.usdk.board.wz.para.SSReplyPara
import cc.uling.usdk.constants.CodeUtil
import cc.uling.usdk.constants.ErrorConst
import com.awish.assistant.ui.components.AddrSelectorDialog
import com.awish.assistant.ui.components.ChannelSelectorDialog
import com.awish.assistant.ui.components.FloorTypeSelectorDialog
import com.awish.assistant.ui.components.LoadingDialog
import com.awish.assistant.ui.components.Qrcode
import com.awish.assistant.ui.components.SerialPortSelectorDialog
import com.awish.assistant.ui.components.TextDivider
import com.awish.assistant.ui.templates.LogEntry
import com.awish.assistant.ui.templates.LogLevel
import com.awish.assistant.ui.templates.Logger
import com.awish.assistant.viewmodel.FloorTypeViewModel
import com.awish.assistant.viewmodel.SerialPortViewModel
import kotlinx.coroutines.launch

data class Option(
  var commid: String?,
  var addr: Int,
  var isDc: Boolean,
  var isLp: Boolean,
  var code: Int?,
  var floorType: Int?,
  var isOpened: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Welcome(navController: NavHostController) {
  var option by remember {
    mutableStateOf(
      Option(
        commid = "/dev/ttyS0",
        addr = 1,
        isDc = false,
        isLp = false,
        code = 0,
        floorType = 1,
        isOpened = false
      )
    )
  }
  var board by remember { mutableStateOf(USDK.getInstance().create(option.commid)) }

  val context = LocalContext.current
  var visibleOfFloorTypeModel by remember { mutableStateOf(false) }
  var visibleOfAddrModel by remember { mutableStateOf(false) }
  var visibleOfChannelModel by remember { mutableStateOf(false) }
  var visibleOfSerialPortModel by remember { mutableStateOf(false) }
  var isLoading by remember { mutableStateOf(false) }

  val ftvm: FloorTypeViewModel = hiltViewModel()
  val spvm: SerialPortViewModel = hiltViewModel()
  val serialPaths: List<String> = spvm.serialPaths

  var expanded by remember { mutableStateOf(false) }

  val appName = stringResource(id = R.string.app_name)
  val appVersion = BuildConfig.VERSION_NAME
  val title = "$appName v$appVersion"

  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  val scope = rememberCoroutineScope()

  val logs = remember { mutableStateListOf<LogEntry>() }

  // 首次进入进行加载
  LaunchedEffect(Unit) {
    logs.add(
      LogEntry.new(
        message = context.getString(R.string.welcome_message, title),
        info = LogLevel.INFO
      )
    )
  }

  ModalNavigationDrawer(
    drawerState = drawerState,
    drawerContent = {
      ModalDrawerSheet {
        Text(
          text = title,
          modifier = Modifier.padding(16.dp),
          style = MaterialTheme.typography.titleMedium
        )
        HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
        NavigationDrawerItem(
          label = { Text(stringResource(id = R.string.nav_about)) },
          selected = false,
          onClick = {
            scope.launch { drawerState.close() }
            expanded = false
            navController.navigate("about")
          }
        )

        Spacer(modifier = Modifier.weight(1f))

        Qrcode()
      }
    }
  ) {
    Scaffold(
      topBar = {
        CenterAlignedTopAppBar(
          navigationIcon = {
            IconButton(onClick = {
              scope.launch {
                if (drawerState.isClosed) {
                  drawerState.open()
                } else {
                  drawerState.close()
                }
              }
            }) {
              Icon(
                imageVector = if (drawerState.isOpen) Icons.AutoMirrored.Filled.MenuOpen else Icons.Default.Menu,
                contentDescription = if (drawerState.isOpen) "Close menu" else "Open menu"
              )
            }
          },
          title = { Text("$appName v$appVersion") },
          colors = TopAppBarDefaults.topAppBarColors(
            navigationIconContentColor = Color.White, // 左边图标颜色
            containerColor = Color(0xFF424ea0), // 背景颜色
            titleContentColor = Color.White,     // 文本颜色
            actionIconContentColor = Color.White     // 右边图标颜色
          ),
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
                  navController.navigate("about")
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
      ) {
        // 第一半部分
        Column(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(innerPadding)
            .padding(8.dp)
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(
              modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
            ) {
              ExposedDropdownMenuBox(
                expanded = false,
                onExpandedChange = {
                  visibleOfSerialPortModel = true
                },
                modifier = Modifier.fillMaxWidth()
              ) {
                OutlinedTextField(
                  value = option.commid ?: stringResource(R.string.label_select),
                  onValueChange = { },
                  label = {
                    Text(
                      if (option.commid != null) stringResource(R.string.serial_path) else stringResource(
                        R.string.select_serial_path
                      )
                    )
                  },
                  readOnly = true,
                  modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                  singleLine = true,
                )
              }
            }
            Button(
              onClick = {
                if (board.EF_Opened()) {
                  board.EF_CloseDev()
                }
                board = USDK.getInstance().create(option.commid)
                isLoading = true
                val resp = board.EF_OpenDev(option.commid, 9600)
                if (resp != ErrorConst.MDB_ERR_NO_ERR) {
                  logs.add(
                    LogEntry.new(
                      context.getString(R.string.open_serial_path_fail),
                      LogLevel.DANGER
                    )
                  )
                  return@Button
                }

                HCReplyPara(option.addr).apply {
                  board.ReadHardwareConfig(this)
                }.apply {
                  if (this.isOK) {
                    logs.add(
                      LogEntry.new(
                        context.getString(
                          R.string.open_serial_path_success, option.commid
                        ), LogLevel.SUCCESS
                      )
                    )
                    option = option.copy(isOpened = board.EF_Opened())
                    logs.add(
                      LogEntry.new(
                        context.getString(
                          R.string.read_config_success, this.row, this.column, this.version
                        ),
                        LogLevel.SUCCESS
                      )
                    )
                  } else {
                    if (board.EF_Opened()) {
                      board.EF_CloseDev()
                      option = option.copy(isOpened = false)
                    }
                    logs.add(
                      LogEntry.new(
                        context.getString(
                          R.string.open_serial_path_fail_check
                        ), LogLevel.DANGER
                      )
                    )
                  }
                  isLoading = false
                }
              },
            ) {
              Text(stringResource(R.string.btn_open_serial))
            }
            if (option.isOpened) {
              Button(
                onClick = {
                  if (board.EF_Opened()) {
                    val resp = board.EF_CloseDev()
                    if (resp == 0) {
                      option = option.copy(isOpened = false)
                      logs.add(
                        LogEntry.new(
                          context.getString(R.string.close_serial_success, option.commid),
                          LogLevel.SUCCESS
                        )
                      )
                    } else {
                      logs.add(
                        LogEntry.new(
                          context.getString(R.string.close_serial_fail),
                          LogLevel.DANGER
                        )
                      )
                    }
                  } else {
                    logs.add(
                      LogEntry.new(
                        context.getString(R.string.serial_not_open),
                        LogLevel.DANGER
                      )
                    )
                  }
                },
                colors = ButtonDefaults.buttonColors(
                  containerColor = MaterialTheme.colorScheme.secondary
                ),
              ) {
                Text(stringResource(R.string.btn_close_serial))
              }
            }
          }

          if (visibleOfSerialPortModel) {
            SerialPortSelectorDialog(
              serialPaths = serialPaths,
              defaultValue = option.commid,
              onSelect = { selected ->
                option.commid = selected
                visibleOfSerialPortModel = false
                logs.add(
                  LogEntry.new(
                    context.getString(R.string.select_serial_port_path, option.commid),
                    LogLevel.WARN
                  )
                )
              }
            )
          }

          if (visibleOfFloorTypeModel) {
            FloorTypeSelectorDialog(
              options = ftvm.options,
              defaultValue = option.floorType,
              onSelect = { selected ->
                option = option.copy(floorType = selected.id)
                visibleOfFloorTypeModel = false
                logs.add(
                  LogEntry.new(
                    context.getString(R.string.floor_type_value, selected.title),
                    LogLevel.WARN
                  )
                )
              }
            )
          }

          if (visibleOfAddrModel) {
            AddrSelectorDialog(
              defaultValue = option.addr,
              onSelect = { selected ->
                option = option.copy(addr = selected)
                visibleOfAddrModel = false
                logs.add(
                  LogEntry.new(
                    context.getString(R.string.board_addr_value, option.addr),
                    LogLevel.WARN
                  )
                )
              }
            )
          }

          if (visibleOfChannelModel) {
            ChannelSelectorDialog(
              defaultValue = option.code,
              onSelect = { selected ->
                option = option.copy(code = selected)
                visibleOfChannelModel = false
                logs.add(
                  LogEntry.new(
                    context.getString(R.string.channel_value, option.code),
                    LogLevel.WARN
                  )
                )
              }
            )
          }

          LoadingDialog(isLoading = isLoading)

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              enabled = option.isOpened,
              onClick = {
                IOReplyPara(option.addr).apply {
                  board.GetIOStatus(this)
                }.apply {
                  if (this.isOK) {
                    logs.add(
                      LogEntry.new(
                        context.getString(
                          R.string.read_io_status_success,
                          this.io0,
                          this.io1,
                          this.io2,
                          this.io3,
                          this.io4,
                          this.io5,
                          this.io6,
                          this.io7,
                          this.io8,
                          this.io9,
                          this.io10,
                          this.io11,
                          this.io12
                        ),
                        LogLevel.SUCCESS
                      )
                    )
                  } else {
                    logs.add(
                      LogEntry.new(
                        context.getString(R.string.read_io_status_fail),
                        LogLevel.DANGER
                      )
                    )
                  }
                }
              },
              modifier = Modifier.weight(1f)
            ) {
              Text(stringResource(R.string.read_io_status))
            }

            Button(
              enabled = option.isOpened,
              onClick = {
                HCReplyPara(option.addr).apply {
                  board.ReadHardwareConfig(this)
                }.apply {
                  if (this.isOK) {
                    logs.add(
                      LogEntry.new(
                        context.getString(
                          R.string.read_board_hardware_version_success,
                          this.version
                        ),
                        LogLevel.SUCCESS
                      )
                    )
                  } else {
                    logs.add(
                      LogEntry.new(
                        context.getString(R.string.read_board_hardware_version_fail),
                        LogLevel.DANGER
                      )
                    )
                  }
                }
              },
              modifier = Modifier.weight(1f)
            ) {
              Text(stringResource(R.string.board_hardware_version))
            }
          }

          TextDivider(stringResource(R.string.btn_shipment_test), padding = 12.dp)

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Row(
              modifier = Modifier.weight(1.5f),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Checkbox(
                checked = option.isDc,
                onCheckedChange = {
                  option = option.copy(isDc = it)

                  val status =
                    if (it) context.getString(R.string.status_on) else context.getString(R.string.status_off)
                  logs.add(
                    LogEntry.new(
                      context.getString(
                        R.string.dc_status,
                        status
                      ),
                      if (it) LogLevel.SUCCESS else LogLevel.DANGER
                    )
                  )
                },
                enabled = option.isOpened,
              )
              Text(
                stringResource(R.string.is_dc)
              )
            }

            Row(
              modifier = Modifier.weight(1.5f),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Checkbox(
                checked = option.isLp,
                onCheckedChange = {
                  option = option.copy(isLp = it)
                  val status =
                    if (it) context.getString(R.string.status_on) else context.getString(R.string.status_off)
                  logs.add(
                    LogEntry.new(
                      context.getString(R.string.lp_status, status),
                      if (it) LogLevel.SUCCESS else LogLevel.DANGER
                    )
                  )
                },
                enabled = option.isOpened,
              )
              Text(
                stringResource(R.string.is_lp)
              )
            }

            Button(
              modifier = Modifier.weight(2f),
              enabled = option.isOpened,
              onClick = {
                ResetReplyPara(option.addr).apply {
                  board.ResetLift(this)
                }.apply {
                  if (!this.isOK) {
                    logs.add(
                      LogEntry.new(
                        context.getString(R.string.reset_lift_fail),
                        LogLevel.DANGER
                      )
                    )
                  }
                }
                logs.add(
                  LogEntry.new(
                    context.getString(R.string.reset_lift_success),
                    LogLevel.SUCCESS
                  )
                )
              },
            ) {
              Text(stringResource(R.string.reset_lift))
            }
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(
              modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
            ) {
              ExposedDropdownMenuBox(
                expanded = false,
                onExpandedChange = {
                  visibleOfAddrModel = true
                },
                modifier = Modifier.fillMaxWidth()
              ) {
                OutlinedTextField(
                  value = "${option.addr}",
                  onValueChange = { },
                  label = {
                    Text(
                      if (option.floorType != null) stringResource(R.string.select_addr) else stringResource(
                        R.string.select_addr
                      )
                    )
                  },
                  readOnly = true,
                  modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                  singleLine = true,
                )
              }
            }

            Box(
              modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
            ) {
              ExposedDropdownMenuBox(
                expanded = false,
                onExpandedChange = {
                  visibleOfChannelModel = true
                },
                modifier = Modifier.fillMaxWidth()
              ) {
                OutlinedTextField(
                  value = if (option.code != null) "${option.code}" else stringResource(R.string.label_select),
                  onValueChange = { },
                  label = {
                    Text(
                      if (option.code != null) stringResource(R.string.channel_slot) else stringResource(
                        R.string.select_channel
                      )
                    )
                  },
                  readOnly = true,
                  modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                  singleLine = true,
                )
              }
            }

            Box(
              modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
            ) {
              ExposedDropdownMenuBox(
                expanded = false,
                onExpandedChange = {
                  visibleOfFloorTypeModel = true
                },
                modifier = Modifier.fillMaxWidth()
              ) {
                OutlinedTextField(
                  value = ftvm.options.find { it.id == option.floorType }?.title
                    ?: stringResource(R.string.label_select),
                  onValueChange = { },
                  label = {
                    Text(
                      if (option.floorType != null) stringResource(R.string.floor_type) else stringResource(
                        R.string.select_floor_type
                      )
                    )
                  },
                  readOnly = true,
                  modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                  singleLine = true,
                )
              }
            }

            Button(
              enabled = option.isOpened,
              onClick = {
                SReplyPara(
                  option.addr,
                  option.code!!,
                  option.floorType!!,
                  option.isDc,
                  option.isLp
                ).apply {
                  board.Shipment(this)
                }.apply {
                  if (this.isOK) {
                    logs.add(
                      LogEntry.new(
                        context.getString(R.string.shipment_success),
                        LogLevel.SUCCESS
                      )
                    )
                  } else {
                    logs.add(
                      LogEntry.new(
                        context.getString(R.string.shipment_fail),
                        LogLevel.DANGER
                      )
                    )
                  }
                }
              },
              modifier = Modifier.weight(1f)
            ) {
              Text(stringResource(R.string.btn_shipment))
            }

            Button(
              enabled = option.isOpened,
              onClick = {
                val para = SSReplyPara(option.addr).apply { board.GetShipmentStatus(this) }.apply {
                  if (!this.isOK) {
                    logs.add(
                      LogEntry.new(
                        context.getString(
                          R.string.query_shipment_status_fail
                        ),
                        LogLevel.DANGER
                      )
                    )
                  }
                }

                val info = when (para.runStatus) {
                  0 -> LogLevel.INFO
                  1 -> LogLevel.WARN
                  2 -> LogLevel.SUCCESS
                  else -> LogLevel.ERROR
                }
                logs.add(
                  LogEntry.new(
                    context.getString(
                      R.string.query_shipment_status_success,
                      para.runStatus,
                      CodeUtil.getXYStatusMsg(para.runStatus),
                      para.faultCode,
                      CodeUtil.getFaultMsg(para.faultCode)
                    ),
                    info
                  )
                )
              },
              modifier = Modifier.weight(1f)
            ) {
              Text(stringResource(R.string.query_shipment_status))
            }
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Spacer(modifier = Modifier.weight(3f))
            Button(
              onClick = {
                if (!board.EF_Opened() && !option.isOpened) {
                  logs.add(
                    LogEntry.new(
                      context.getString(R.string.open_serial_first),
                      LogLevel.DANGER
                    )
                  )
                  return@Button
                }

                if (option.floorType != 2) {
                  logs.add(
                    LogEntry.new(
                      context.getString(R.string.electromagnetic_first),
                      LogLevel.DANGER
                    )
                  )
                  return@Button
                }

                logs.add(
                  LogEntry.new(
                    context.getString(R.string.querying_box_status, option.code),
                    LogLevel.INFO
                  )
                )
                val para =
                  BSReplyPara(option.addr, option.code!!).apply { board.GetBoxStatus(this) }
                    .apply {
                      if (!this.isOK) {
                        logs.add(
                          LogEntry.new(
                            context.getString(R.string.query_box_status_fail),
                            LogLevel.DANGER
                          )
                        )
                        return@Button
                      }
                    }

                val info = when (para.status) {
                  0 -> LogLevel.SUCCESS
                  else -> LogLevel.ERROR
                }
                logs.add(
                  LogEntry.new(
                    context.getString(
                      R.string.query_box_status_success,
                      para.status,
                      para.no
                    ),
                    info
                  )
                )
              },
              modifier = Modifier.weight(2f),
              enabled = option.floorType == 2
            ) {
              Text(stringResource(R.string.query_box_status))
            }
          }

        }

        Spacer(modifier = Modifier.height(8.dp)) // 可选间隔

        // 第二半部分
        Logger(logs) {
          logs.clear()
          logs.add(
            LogEntry.new(
              message = context.getString(R.string.welcome_message, title),
              info = LogLevel.INFO
            )
          )
        }
      }
    }
  }

}
