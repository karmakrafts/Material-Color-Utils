/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.karma.material.dynamiccolor;

import io.karma.material.utils.MathUtils;

/**
 * A class containing a value that changes with the io.karma.material.contrast level.
 *
 * <p>Usually represents the io.karma.material.contrast requirements for a dynamic color on its background. The four
 * values correspond to values for io.karma.material.contrast levels -1.0, 0.0, 0.5, and 1.0, respectively.
 */
public final class ContrastCurve {
  /** Value for io.karma.material.contrast level -1.0 */
  private final double low;

  /** Value for io.karma.material.contrast level 0.0 */
  private final double normal;

  /** Value for io.karma.material.contrast level 0.5 */
  private final double medium;

  /** Value for io.karma.material.contrast level 1.0 */
  private final double high;

  /**
   * Creates a `ContrastCurve` object.
   *
   * @param low Value for io.karma.material.contrast level -1.0
   * @param normal Value for io.karma.material.contrast level 0.0
   * @param medium Value for io.karma.material.contrast level 0.5
   * @param high Value for io.karma.material.contrast level 1.0
   */
  public ContrastCurve(double low, double normal, double medium, double high) {
    this.low = low;
    this.normal = normal;
    this.medium = medium;
    this.high = high;
  }

  /**
   * Returns the value at a given io.karma.material.contrast level.
   *
   * @param contrastLevel The io.karma.material.contrast level. 0.0 is the default (normal); -1.0 is the lowest; 1.0
   *     is the highest.
   * @return The value. For io.karma.material.contrast ratios, a number between 1.0 and 21.0.
   */
  public double get(double contrastLevel) {
    if (contrastLevel <= -1.0) {
      return this.low;
    } else if (contrastLevel < 0.0) {
      return MathUtils.lerp(this.low, this.normal, (contrastLevel - -1) / 1);
    } else if (contrastLevel < 0.5) {
      return MathUtils.lerp(this.normal, this.medium, (contrastLevel - 0) / 0.5);
    } else if (contrastLevel < 1.0) {
      return MathUtils.lerp(this.medium, this.high, (contrastLevel - 0.5) / 0.5);
    } else {
      return this.high;
    }
  }
}
