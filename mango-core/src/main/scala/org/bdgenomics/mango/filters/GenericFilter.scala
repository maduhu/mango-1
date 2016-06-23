/**
 * Licensed to Big Data Genomics (BDG) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The BDG licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bdgenomics.mango.filters

import org.apache.spark.rdd.RDD
import org.bdgenomics.adam.models.ReferenceRegion

/**
 * Generic filters for anything that can be filtered and binned by ReferenceRegion
 */
object GenericFilter {

  def filterByDensity(window: Long, threshold: Long, rdd: RDD[(ReferenceRegion, Any)], highDensity: Boolean = true): RDD[(ReferenceRegion, Long)] = {
    // reformat regions to fit window size
    val densities = rdd.map(r => (ReferenceRegion(r._1.referenceName, r._1.start / window * window, r._1.start / window * window + window - 1), 1L))
      .reduceByKey(_ + _) // reduce by region to count

    // filter by threshold based on whether we are filtering by high or low densities
    highDensity match {
      case true => densities.filter(_._2 >= threshold)
      case _    => densities.filter(_._2 <= threshold)
    }
  }

}
