# TFLClassify：智能图像分类 APP

本项目是一个基于 **CameraX** + **TensorFlow Lite** 的 Android 图像分类应用，实现了实时相机预览与花卉识别功能。通过本实验，你将理解移动端视觉 AI 应用的完整流水线，掌握 MVVM 架构、数据绑定、模型部署等关键技能。

## 实验目标

- 理解 Android 端图像分类应用的基本流水线（摄像头帧 → 预处理 → 推理 → 状态更新 → UI 刷新）
- 区分 CameraX 中 `Preview` 与 `ImageAnalysis` 的职责
- 掌握 `ViewModel`、`LiveData`、`RecyclerView` 与 `DataBinding` 的配合使用
- 学会集成 `.tflite` 模型并进行硬件加速（GPU Delegate）

## 项目来源与版本

- GitHub 仓库：[https://github.com/hoitab/TFLClassify.git](https://github.com/hoitab/TFLClassify.git)
- 实验起点：`start` 模块  
- 参考实现：`finish` 模块（运行后理解效果，再回到 `start` 补全 TODO）

## 环境要求

- Android Studio (最新稳定版)
- 真机或模拟器（建议真机，需要摄像头权限）
- 最低 SDK 版本：项目配置中指定（通常 API 21+）

## 项目结构概览
TFLClassify/
├── start/ # 实验起点代码（需要补全 TODO）
│ ├── src/main/java/.../MainActivity.kt
│ ├── src/main/java/.../ImageAnalyzer.kt
│ ├── src/main/ml/ # 存放 .tflite 模型文件
│ └── res/layout/ # UI 布局文件
├── finish/ # 完整可运行参考代码
└── README.md

## 开始步骤

1. **克隆仓库**  
   ```bash
   git clone https://github.com/hoitab/TFLClassify.git、
# 用 Android Studio 打开项目

选择 `Open an existing project` → 定位到克隆的目录 → 选择 `start` 模块作为起点。

## 同步 Gradle

确保 `build.gradle` 中开启了 `dataBinding` 和 `mlModelBinding`：

```gradle
android {
    ...
    buildFeatures {
        dataBinding true
        mlModelBinding true
    }
}

2. **Android Studio 打开项目**  
选择 Open an existing project → 定位到克隆的目录 → 选择 start 模块作为起点。


3. **同步 Gradle**  
确保 build.gradle 中开启了 dataBinding 和 mlModelBinding：

```gradle
android {
    ...
    buildFeatures {
        dataBinding true
        mlModelBinding true
    }
}

4. **运行应用**  
连接真机，授予相机权限，预览画面应正常显示，但识别列表暂无结果。

各 TODO 的具体实现提示
TODO 1：初始化模型
```kotlin
private val flowerModel: FlowerModel by lazy {
    FlowerModel.newInstance(context, Model.Options.Builder().build())
}
注意：需要导入 org.tensorflow.lite.task.vision.classifier.Classifier 等相关类。

TODO 2：图像转换
```kotlin
val bitmap = ImageUtils.toBitmap(imageProxy)
val tfImage = TensorImage.fromBitmap(bitmap)
ImageUtils 是项目提供的辅助类，无需自己编写。

TODO 3：模型推理与排序
```kotlin
val outputs = flowerModel.process(tfImage)
    .probabilityAsCategoryList
    .sortedByDescending { it.score }
    .take(MAX_RESULT_DISPLAY)
TODO 4：转换为 Recognition 对象
```kotlin
val items = outputs.map { Recognition(it.label, it.score) }
// 通过 ViewModel 将 items 发送给 UI
TODO 5：添加 GPU Delegate 依赖
在 app/build.gradle 中添加：

```gradle
implementation 'org.tensorflow:tensorflow-lite-gpu:2.5.0'
TODO 6：设备自适应配置
```kotlin
val compatList = CompatibilityList()
val options = if (compatList.isDelegateSupportedOnThisDevice) {
    Model.Options.Builder().setDevice(Model.Device.GPU).build()
} else {
    Model.Options.Builder().setNumThreads(4).build()
}


关键知识点解析
1. CameraX 流水线
Preview：仅负责画面显示，不参与推理。

ImageAnalysis：每一帧都会回调 analyze() 方法，执行模型推理。

生命周期绑定：通过 ProcessCameraProvider.bindToLifecycle() 自动释放相机资源。

2. 数据流与 UI 更新
ViewModel 持有 LiveData<List<Recognition>>

ImageAnalyzer 分析完一帧后更新 ViewModel 中的 LiveData

MainActivity 观察 LiveData，并通过 RecognitionAdapter.submitList() 刷新 RecyclerView

3. 性能优化要点
在 analyze() 末尾务必调用 imageProxy.close()，否则 CameraX 无法继续送帧。

减少列表闪烁：在 RecyclerView 上关闭 itemAnimator。

硬件加速：优先尝试 GPU Delegate，不支持的设备回退到多线程 CPU。

4. ML Model Binding
启用 mlModelBinding true 后，.tflite 文件会自动生成对应的访问类（如 FlowerModel），无需手动编写 Interpreter。



参考资料
项目仓库：TFLClassify GitHub（内含 start/finish 模块）

官方文档：

CameraX ImageAnalysis

LiveData 指南

Data Binding

LiteRT (TensorFlow Lite)

Android Studio ML Model Binding