package com.brokoli.rules

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.checks.infrastructure.TestLintTask
import java.io.File
import kotlin.system.exitProcess

@Suppress("UnstableApiUsage")
abstract class AndroidSdkLintDetectorTest : LintDetectorTest() {

    private fun getSdkHome(): File {
        val androidHome = System.getenv("ANDROID_HOME")
        if(androidHome.isNullOrBlank()) {
            System.err.println("ANDROID_HOME must be available wherever you are executing these tests. " +
                    "For example, when running Android Studio in Linux, ANDROID_HOME should be set in /etc/environment, " +
                    "because if it is only set in .bashrc, Android Studio will not have access.")
            exitProcess(1)
        }
        val file = File(androidHome)
        if(!file.exists()) {
            System.err.println("$androidHome does not exist")
            exitProcess(1)
        }
        return file
    }

    override fun lint(): TestLintTask {
        return super.lint().sdkHome(getSdkHome())
    }

}