package com.emotionfriend.feature.express

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview as CameraXPreview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat

/**
 * CameraX live preview surface.
 *
 * Binds both the [Preview] and [ImageCapture] use cases so the caller can
 * trigger in-memory captures via [imageCapture]. Captured frames must be
 * discarded immediately via [ImageProxy.close()] — this composable never
 * writes images to disk or any persistent store.
 *
 * Camera selector priority: front → rear. If neither is available,
 * [onCameraError] is invoked so the caller can show a fallback UI.
 */
@Composable
fun CameraPreview(
    imageCapture : ImageCapture,
    modifier     : Modifier = Modifier,
    onCameraError: () -> Unit = {}
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        factory  = { ctx ->
            val previewView          = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener(
                {
                    val cameraProvider = cameraProviderFuture.get()
                    val previewUseCase = CameraXPreview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    fun tryBind(selector: CameraSelector): Boolean = try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            selector,
                            previewUseCase,
                            imageCapture
                        )
                        true
                    } catch (_: Exception) {
                        false
                    }

                    val bound = tryBind(CameraSelector.DEFAULT_FRONT_CAMERA)
                            || tryBind(CameraSelector.DEFAULT_BACK_CAMERA)

                    if (!bound) onCameraError()
                },
                ContextCompat.getMainExecutor(ctx)
            )

            previewView
        },
        modifier = modifier
    )
}
