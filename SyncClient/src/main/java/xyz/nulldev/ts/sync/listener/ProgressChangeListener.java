/*
 * Copyright 2016 Andy Bao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.nulldev.ts.sync.listener;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 27/08/16
 *
 * The listener called when the progress of a synchronization operation changes
 */
public interface ProgressChangeListener {
    /**
     * The progress of a sync operation changed
     * @param isComplete Whether or not the sync operation has completed
     * @param details The progress details
     */
    void onProgressChange(boolean isComplete, String details);
}
