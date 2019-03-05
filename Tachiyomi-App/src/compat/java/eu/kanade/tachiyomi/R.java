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

package eu.kanade.tachiyomi;

import xyz.nulldev.androidcompat.res.RCompat;

/**
 * Fake resource reference class.
 */
public class R extends RCompat {
    // TODO Eventually generate these from strings.xml
    public static class string {
        public static int app_name = sres("TachiWeb");
        public static int reading = sres("Reading");
        public static int completed = sres("Completed");
        public static int dropped = sres("Dropped");
        public static int on_hold = sres("On hold");
        public static int plan_to_read = sres("Plan to read");
        public static int repeating = sres("Re-reading");

        public static int download_queue_error = sres("An error occurred while downloading chapters. You can try again in the downloads section");
        public static int download_notifier_text_only_wifi = sres("No wifi connection available");
        public static int download_notifier_no_network = sres("No network connection available");

        public static int local_source = sres("Local manga");

        public static int source_not_installed = sres("Source not installed: %1$s");

        public static int pref_theme_key = sres("pref_theme_key");

        public static int pref_rotation_type_key = sres("pref_rotation_type_key");

        public static int pref_enable_transitions_key = sres("pref_enable_transitions_key");
        public static int pref_show_page_number_key = sres("pref_show_page_number_key");
        public static int pref_fullscreen_key = sres("pref_fullscreen_key");
        public static int pref_keep_screen_on_key = sres("pref_keep_screen_on_key");
        public static int pref_custom_brightness_key = sres("pref_custom_brightness_key");
        public static int pref_custom_brightness_value_key = sres("pref_custom_brightness_value_key");
        public static int pref_color_filter_key = sres("pref_color_filter_key");
        public static int pref_color_filter_value_key = sres("pref_color_filter_value_key");
        public static int pref_default_viewer_key = sres("pref_default_viewer_key");
        public static int pref_image_scale_type_key = sres("pref_image_scale_type_key");
        public static int pref_image_decoder_key = sres("pref_image_decoder_key");
        public static int pref_zoom_start_key = sres("pref_zoom_start_key");
        public static int pref_reader_theme_key = sres("pref_reader_theme_key");
        public static int pref_crop_borders_key = sres("pref_crop_borders_key");
        public static int pref_read_with_tapping_key = sres("pref_read_with_tapping_key");
        public static int pref_read_with_volume_keys_key = sres("pref_read_with_volume_keys_key");
        public static int pref_library_columns_portrait_key = sres("pref_library_columns_portrait_key");
        public static int pref_library_columns_landscape_key = sres("pref_library_columns_landscape_key");
        public static int pref_update_only_non_completed_key = sres("pref_update_only_non_completed_key");
        public static int pref_auto_update_manga_sync_key = sres("pref_auto_update_manga_sync_key");
        public static int pref_ask_update_manga_sync_key = sres("pref_ask_update_manga_sync_key");
        public static int pref_last_catalogue_source_key = sres("pref_last_catalogue_source_key");
        public static int pref_last_used_category_key = sres("pref_last_used_category_key");
        public static int pref_display_catalogue_as_list = sres("pref_display_catalogue_as_list");
        public static int pref_source_languages = sres("pref_source_languages");
        public static int pref_download_directory_key = sres("pref_download_directory_key");
        public static int pref_download_slots_key = sres("pref_download_slots_key");
        public static int pref_download_only_over_wifi_key = sres("pref_download_only_over_wifi_key");
        public static int pref_remove_after_read_slots_key = sres("pref_remove_after_read_slots_key");
        public static int pref_remove_after_marked_as_read_key = sres("pref_remove_after_marked_as_read_key");
        public static int pref_library_update_interval_key = sres("pref_library_update_interval_key");
        public static int pref_library_update_restriction_key = sres("pref_library_update_restriction_key");
        public static int pref_library_update_categories_key = sres("pref_library_update_categories_key");
        public static int pref_filter_downloaded_key = sres("pref_filter_downloaded_key");
        public static int pref_filter_unread_key = sres("pref_filter_unread_key");
        public static int pref_library_sorting_mode_key = sres("pref_library_sorting_mode_key");
        public static int pref_enable_automatic_updates_key = sres("pref_enable_automatic_updates_key");
        public static int pref_start_screen_key = sres("pref_start_screen_key");
        public static int pref_download_new_key = sres("pref_download_new_key");
        public static int pref_download_new_categories_key = sres("pref_download_new_categories_key");
        public static int pref_display_library_as_list = sres("pref_display_library_as_list");
        public static int pref_language_key = sres("pref_language_key");
    }
    public static class drawable {
        public static int al = dres("/al.png");
        public static int kitsu = dres("/kitsu.png");
        public static int mal = dres("/mal.png");
    }
}
