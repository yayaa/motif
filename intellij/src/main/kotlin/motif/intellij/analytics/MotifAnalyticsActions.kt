/*
 * Copyright (c) 2018-2019 Uber Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http:www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package motif.intellij.analytics

abstract class MotifAnalyticsActions {

    companion object {
        const val PROJECT_OPENED: String = "projectOpened"

        const val GRAPH_INIT: String = "graphInit"
        const val GRAPH_UPDATE: String = "graphUpdate"

        const val ANCESTOR_MENU_CLICK: String = "ancestorMenuClick"
        const val USAGE_MENU_CLICK: String = "usageMenuClick"
        const val ANCESTOR_GUTTER_CLICK: String = "ancestorGutterClick"
        const val NAVIGATION_GUTTER_CLICK: String = "navigationGutterClick"
    }
}