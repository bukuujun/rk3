# 实验2-3：构建 Android CameraX 应用

## 一、实验目的

- 掌握 Android CameraX 拍照功能的基本用法。
- 掌握 Android CameraX 视频捕捉功能的基本用法。
- 了解 CameraX 预览（Preview）、拍照（ImageCapture）、录像（VideoCapture）和图像分析（ImageAnalysis）等核心用例。
- 进一步熟悉 Kotlin 语言在 Android 开发中的应用。

## 二、实验环境

- 开发工具：Android Studio Arctic Fox (2020.3.1) 或更高版本
- 编程语言：Kotlin
- 最低 SDK：API 21 (Android 5.0)
- 测试设备：Android 真机（建议带摄像头）或模拟器（需支持 Camera）
- 依赖库：CameraX (版本 1.5.0-alpha06 或更新)

## 三、实验内容与步骤

### 3.1 创建项目

1. 打开 Android Studio，点击 **New Project**。
2. 选择 **Empty Activity** 模板。
3. 配置项目：
   - **Name**：`CameraXApp`（或自定义）
   - **Package name**：`com.example.cameraxapp`
   - **Language**：选择 **Kotlin**
   - **Minimum SDK**：选择 **API 21**
4. 点击 **Finish**，等待项目构建完成。

### 3.2 添加 CameraX 依赖

在模块级 `build.gradle.kts`（或 `build.gradle`）文件中添加 CameraX 依赖：

```kotlin
dependencies {
    val camerax_version = "1.5.0-alpha06"
    
    // CameraX 核心库
    implementation("androidx.camera:camera-core:${camerax_version}")
    implementation("androidx.camera:camera-camera2:${camerax_version}")
    
    // 生命周期支持
    implementation("androidx.camera:camera-lifecycle:${camerax_version}")
    
    // 视频录制
    implementation("androidx.camera:camera-video:${camerax_version}")
    
    // CameraX 视图组件
    implementation("androidx.camera:camera-view:${camerax_version}")
    
    // ML Kit 视觉集成（可选）
    implementation("androidx.camera:camera-mlkit-vision:${camerax_version}")
    
    // 扩展库（可选）
    implementation("androidx.camera:camera-extensions:${camerax_version}")
}
```
同步 Gradle 以下载依赖。

### 3.3 添加相机权限

在 `AndroidManifest.xml` 中添加相机权限：

```xml
<uses-permission android:name\="android.permission.CAMERA" />
<uses-feature android:name\="android.hardware.camera" android:required\="true" />
```

### 3.4 设计布局（XML）

创建 `activity_main.xml` 布局文件：

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout 
    xmlns:android\="http://schemas.android.com/apk/res/android"
    xmlns:app\="http://schemas.android.com/apk/res-auto"
    android:layout\_width\="match\_parent"
    android:layout\_height\="match\_parent"\>
    <!-- 相机预览视图 -->
    <androidx.camera.view.PreviewView
        android:id\="@+id/previewView"
        android:layout\_width\="match\_parent"
        android:layout\_height\="match\_parent" />
    <!-- 控制按钮区域 -->
    <LinearLayout
        android:layout\_width\="match\_parent"
        android:layout\_height\="wrap\_content"
        android:orientation\="horizontal"
        android:gravity\="center"
        android:layout\_marginBottom\="32dp"
        app:layout\_constraintBottom\_toBottomOf\="parent"\>
        <Button
            android:id\="@+id/captureButton"
            android:layout\_width\="wrap\_content"
            android:layout\_height\="wrap\_content"
            android:text\="拍照" />
        <Button
            android:id\="@+id/recordButton"
            android:layout\_width\="wrap\_content"
            android:layout\_height\="wrap\_content"
            android:text\="录像" />
    </LinearLayout\>
</androidx.constraintlayout.widget.ConstraintLayout\>
```

### 3.5 编写 MainActivity.kt

#### 3.5.1 权限请求

```kotlin
private val REQUEST\_CODE\_PERMISSIONS \= 10
private val REQUIRED\_PERMISSIONS \= arrayOf(Manifest.permission.CAMERA)
private fun allPermissionsGranted() \= REQUIRED\_PERMISSIONS.all {
    ContextCompat.checkSelfPermission(baseContext, it) \== PackageManager.PERMISSION\_GRANTED
}
private fun requestPermissions() {
    ActivityCompat.requestPermissions(this, REQUIRED\_PERMISSIONS, REQUEST\_CODE\_PERMISSIONS)
}
override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String\>,
    grantResults: IntArray
) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode \== REQUEST\_CODE\_PERMISSIONS) {
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            Toast.makeText(this, "需要相机权限", Toast.LENGTH\_SHORT).show()
        }
    }
}
```

#### 3.5.2 启动相机预览

```kotlin

private lateinit var previewView: PreviewView
private lateinit var imageCapture: ImageCapture
private lateinit var videoCapture: VideoCapture<Recorder\>
private var isRecording \= false
private fun startCamera() {
    val cameraProviderFuture \= ProcessCameraProvider.getInstance(this)
    cameraProviderFuture.addListener({
        val cameraProvider \= cameraProviderFuture.get()
        // 预览用例
        val preview \= Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }
        // 拍照用例
        imageCapture \= ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE\_MODE\_MINIMIZE\_LATENCY)
            .build()
        // 录像用例
        val recorder \= Recorder.Builder()
            .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
            .build()
        videoCapture \= VideoCapture.withOutput(recorder)
        // 选择前置或后置摄像头
        val cameraSelector \= CameraSelector.DEFAULT\_BACK\_CAMERA
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageCapture, videoCapture
            )
        } catch (e: Exception) {
            Log.e(TAG, "相机绑定失败", e)
        }
    }, ContextCompat.getMainExecutor(this))
}
```

#### 3.5.3 实现拍照功能

```kotlin
private fun takePhoto() {
    val imageCapture \= imageCapture ?: return
    val photoFile \= File(
        getExternalFilesDir(Environment.DIRECTORY\_PICTURES),
        "IMG\_${System.currentTimeMillis()}.jpg"
    )
    val outputOptions \= ImageCapture.OutputFileOptions.Builder(photoFile).build()
    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(this),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                Toast.makeText(this@MainActivity, "照片已保存: ${photoFile.absolutePath}", Toast.LENGTH\_SHORT).show()
                // 可选：添加到图库
                MediaStore.Images.Media.insertImage(
                    contentResolver,
                    photoFile.absolutePath,
                    photoFile.name,
                    null
                )
            }
            override fun onError(exception: ImageCaptureException) {
                Toast.makeText(this@MainActivity, "拍照失败: ${exception.message}", Toast.LENGTH\_SHORT).show()
            }
        }
    )
}
```
#### 3.5.4 实现录像功能

```kotlin

private fun startRecording() {
    val videoFile \= File(
        getExternalFilesDir(Environment.DIRECTORY\_MOVIES),
        "VID\_${System.currentTimeMillis()}.mp4"
    )
    val outputOptions \= VideoCapture.OutputFileOptions.Builder(videoFile).build()
    videoCapture.output \= Recorder.Builder().build()
    videoCapture.startRecording(
        outputOptions,
        ContextCompat.getMainExecutor(this),
        object : VideoCapture.OnVideoSavedCallback {
            override fun onVideoSaved(output: VideoCapture.OutputFileResults) {
                Toast.makeText(this@MainActivity, "视频已保存: ${videoFile.absolutePath}", Toast.LENGTH\_SHORT).show()
                isRecording \= false
                updateRecordButtonText()
            }
            override fun onError(exception: VideoCaptureException) {
                Toast.makeText(this@MainActivity, "录像失败: ${exception.message}", Toast.LENGTH\_SHORT).show()
                isRecording \= false
                updateRecordButtonText()
            }
        }
    )
}
private fun stopRecording() {
    videoCapture.stopRecording()
}
private fun updateRecordButtonText() {
    recordButton.text \= if (isRecording) "停止录像" else "开始录像"
}
// 按钮点击事件
captureButton.setOnClickListener { takePhoto() }
recordButton.setOnClickListener {
    if (isRecording) {
        stopRecording()
    } else {
        startRecording()
    }
}
```

### 3.6 完整 MainActivity.kt 代码结构

```kotlin

class MainActivity : AppCompatActivity() {
    private lateinit var previewView: PreviewView
    private lateinit var imageCapture: ImageCapture
    private lateinit var videoCapture: VideoCapture<Recorder\>
    private var isRecording \= false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity\_main)
        previewView \= findViewById(R.id.previewView)
        val captureButton \= findViewById<Button\>(R.id.captureButton)
        val recordButton \= findViewById<Button\>(R.id.recordButton)
        captureButton.setOnClickListener { takePhoto() }
        recordButton.setOnClickListener { toggleRecording() }
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }
    }
    // ... 上述所有函数实现
}
```

## 四、实验结果
点击拍照，保存图片至相册
![图片](./pic/p1.png)

点击录像开始/停止，保存视频
![图片](./pic/p2.png)

## 五、实验总结

通过本次实验，我掌握了以下内容：

1.  **CameraX 架构**：理解了 CameraX 作为 Jetpack 库的核心优势——简化相机开发、兼容性好、生命周期自动管理。
2.  **四大用例**：
    
    -   **Preview**：实时显示摄像头画面，是相机应用的基础。
    -   **ImageCapture**：实现高质量拍照功能，支持图片保存和回调。
    -   **VideoCapture**：实现视频录制，支持开始/停止控制。
    -   **ImageAnalysis**（扩展）：支持实时帧分析，为 AI 推理奠定基础。
3.  **权限处理**：掌握了 Android 运行时权限请求的标准流程。
4.  **Kotlin 特性**：进一步熟练了 Kotlin 的扩展函数、高阶函数（监听器）、空安全等特性。

CameraX 是构建 Android AI 视觉应用的关键组件，本次实验为后续集成 LiteRT 或 ML Kit 进行实时图像识别打下了坚实的基础。

## 六、参考资料

-   [Kotlin 官方网站](https://kotlinlang.org/)
-   [CameraX 概览](https://developer.android.com/training/camerax)
-   [CameraX 使用入门](https://developer.android.com/training/camerax/get-started)
-   [CameraX 官方示例代码](https://github.com/android/camera-samples)

## 七、附件与代码仓库

-   本实验完整代码已上传至 GitHub：  
    [https://github.com/your-username/exp2-3-camerax-app](https://github.com/your-username/exp2-3-camerax-app)