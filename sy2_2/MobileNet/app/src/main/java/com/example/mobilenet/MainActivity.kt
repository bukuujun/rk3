package com.example.mobilenet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobilenet.ui.theme.MobileNetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MobileNetTheme {
                AIAppLayout()
            }
        }
    }
}


@Composable
fun AIAppLayout(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopBar()
        Spacer(modifier = Modifier.height(16.dp))

        CameraPreviewArea()
        Spacer(modifier = Modifier.height(20.dp))

        ResultCard()
        Spacer(modifier = Modifier.height(20.dp))

        ButtonPanel()
    }
}


@Composable
fun TopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.primary)
    ) {
        Text(
            text = "LiteRT AI Demo",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )

        IconButton(
            onClick = { /*菜单点击事件*/ },
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_more),
                contentDescription = "更多选项",
                tint = Color.White
            )
        }
    }
}
@Composable
fun CameraPreviewArea(){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(
                color = Color(0xFF263238),
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ){
        Column (horizontalAlignment = Alignment.CenterHorizontally){
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_camera),
                contentDescription = "相机预览",
                modifier = Modifier.size(64.dp),
                tint = Color.White.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Camera Preview",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp
            )
        }
    }

}

@Composable
fun ResultCard(){
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ){
            InfoRow(label = "Model:", value = "MobileNet")
            Spacer(modifier = Modifier.height(8.dp))

            InfoRow(label = "Result:", value = "Cat")
            Spacer(modifier = Modifier.height(8.dp))

            InfoRow(label = "Confidence:", value = "96.2%")
            Spacer(modifier = Modifier.height(8.dp))
            InfoRow(label = "Time:", value = "28 ms")
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, isHighlight: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = if (isHighlight) 16.sp else 14.sp,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal,
            color = if (isHighlight) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ButtonPanel() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 蓝色按钮
            ActionButton(
                text = "拍照识别",
                icon = android.R.drawable.ic_menu_camera,
                modifier = Modifier.weight(1f),
                onClick = { /* 拍照逻辑 */ },
                color = Color(0xFF2196F3)  // 蓝色
            )

            // 绿色按钮
            ActionButton(
                text = "相册导入",
                icon = android.R.drawable.ic_menu_gallery,
                modifier = Modifier.weight(1f),
                onClick = { /* 相册逻辑 */ },
                color = Color(0xFF4CAF50)  // 绿色
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 紫色按钮
            ActionButton(
                text = "切换模型",
                icon = android.R.drawable.ic_menu_manage,
                modifier = Modifier.weight(1f),
                onClick = { /* 切换模型逻辑 */ },
                color = Color(0xFF9C27B0)  // 紫色
            )

            // 红色按钮
            ActionButton(
                text = "清空结果",
                icon = android.R.drawable.ic_menu_delete,
                modifier = Modifier.weight(1f),
                onClick = { /* 清空逻辑 */ },
                color = Color(0xFFF44336)  // 红色
            )
        }
    }
}

// 操作按钮组件（增加 color 参数，默认保持原主题色）
@Composable
fun ActionButton(
    text: String,
    icon: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.primary  // 新增颜色参数，默认为主题主色
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,              // 使用传入的颜色
            contentColor = Color.White
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = text,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, fontSize = 14.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AIAppLayoutPreview() {
    MobileNetTheme{
        AIAppLayout()
    }
}



