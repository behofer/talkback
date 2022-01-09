/*
 * Copyright (C) 2021 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.android.accessibility.talkback.imagecaption;

import android.accessibilityservice.AccessibilityService;
import android.graphics.Bitmap;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.annotation.NonNull;
import com.google.android.accessibility.talkback.imagecaption.RequestList.Request;
import com.google.android.accessibility.utils.AccessibilityNodeInfoUtils;
import com.google.android.accessibility.utils.StringBuilderUtils;
import com.google.android.accessibility.utils.screencapture.ScreenshotCapture;

/** A request to take screenshot. */
public class ScreenshotCaptureRequest implements Request {

  /** A listener to be invoked when taking screenshot is finished. */
  public interface OnFinishListener {
    /** Called when taking screenshot is finished. */
    void onFinish(AccessibilityNodeInfoCompat node, Bitmap bitmap);
  }

  private final AccessibilityService service;
  private final AccessibilityNodeInfoCompat node;
  @NonNull private final OnFinishListener onFinishListener;

  public ScreenshotCaptureRequest(
      AccessibilityService service,
      AccessibilityNodeInfoCompat node,
      @NonNull OnFinishListener onFinishListener) {
    this.service = service;
    // Copies node which must be recycled when the request is finished.
    this.node = AccessibilityNodeInfoCompat.obtain(node);
    this.onFinishListener = onFinishListener;
  }

  @Override
  public void perform() {
    ScreenshotCapture.takeScreenshot(
        service,
        (screenCapture, isFormatSupported) -> {
          onFinishListener.onFinish(node, screenCapture);
          AccessibilityNodeInfoUtils.recycleNodes(node);
        });
  }

  @Override
  public void recycle() {}

  @Override
  public String toString() {
    return getClass().getSimpleName()
        + "= "
        + StringBuilderUtils.joinFields(StringBuilderUtils.optionalSubObj("node", node));
  }
}
