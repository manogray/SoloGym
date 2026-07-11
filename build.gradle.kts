// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.myAndroidApplication) apply false
    alias(libs.plugins.myKotlinAndroid) apply false
    alias(libs.plugins.myComposeCompiler) apply false
    alias(libs.plugins.myHilt) apply false
    alias(libs.plugins.myKsp) apply false
}