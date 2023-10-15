package com.github.simonpham.verification.service.tiles

import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.github.simonpham.verification.DATA_TYPE_INT
import com.github.simonpham.verification.GLOBAL_DEMO_MODE_ALLOWED
import com.github.simonpham.verification.GLOBAL_DEMO_MODE_ON
import com.github.simonpham.verification.service.BaseTileService

/**
 * Created by Simon Pham on 5/30/18.
 * Email: simonpham.dn@gmail.com
 */

// TODO: let user customize Demo mode
class DemoModeService : BaseTileService() {

    private val STATUS_ICONS = listOf(
            "volume",
            "bluetooth",
            "location",
            "alarm",
            "zen",
            "sync",
            "tty",
            "eri",
            "mute",
            "speakerphone",
            "managed_profile"
    )

    private enum class DemoMode(val value: String) {
        ACTION_DEMO("com.android.systemui.demo"),
        EXTRA_COMMAND("command"),
        COMMAND_ENTER("enter"),
        COMMAND_EXIT("exit"),
        COMMAND_CLOCK("clock"),
        COMMAND_BATTERY("battery"),
        COMMAND_NETWORK("network"),
        COMMAND_BARS("bars"),
        COMMAND_STATUS("status"),
        COMMAND_NOTIFICATIONS("notifications"),
        COMMAND_VOLUME("volume")
    }

    override fun onStartListening() {
        super.onStartListening()
        if (Settings.Global.getInt(contentResolver, GLOBAL_DEMO_MODE_ALLOWED, 0) == 0) {
            devSettings.setGlobal(GLOBAL_DEMO_MODE_ALLOWED, "1", DATA_TYPE_INT)
        }
        refresh()
    }

    override fun refresh() {
        updateState(isFeatureEnabled())
    }

    override fun onClick() {
        if (isFeatureEnabled()) {
            stopDemoMode()
            updateState(false)
        } else {
            startDemoMode()
            updateState(true)
        }
    }

    private fun isFeatureEnabled(): Boolean {
        return devSettings.getGlobalInt(GLOBAL_DEMO_MODE_ON) == 1
    }

    private fun startDemoMode() {
        val intent = Intent(DemoMode.ACTION_DEMO.value)

        intent.putExtra(DemoMode.EXTRA_COMMAND.value, DemoMode.COMMAND_ENTER.value)
        sendBroadcast(intent)

        intent.putExtra(DemoMode.EXTRA_COMMAND.value, DemoMode.COMMAND_CLOCK.value)
        intent.putExtra("hhmm", getDeviceVersionForDemoClock())
        sendBroadcast(intent)

        intent.putExtra(DemoMode.EXTRA_COMMAND.value, DemoMode.COMMAND_NETWORK.value)
        intent.putExtra("wifi", "show")
        intent.putExtra("mobile", "show")
        intent.putExtra("sims", "1")
        intent.putExtra("nosim", "false")
        intent.putExtra("level", "4")
        intent.putExtra("datatype", "lte")
        sendBroadcast(intent)

        // Need to send this after so that the sim controller already exists.
        intent.putExtra("fully", "true")
        sendBroadcast(intent)

        intent.putExtra(DemoMode.EXTRA_COMMAND.value, DemoMode.COMMAND_BATTERY.value)
        intent.putExtra("level", "100")
        intent.putExtra("plugged", "false")
        intent.putExtra("powersave", "false")
        sendBroadcast(intent)

        intent.putExtra(DemoMode.EXTRA_COMMAND.value, DemoMode.COMMAND_STATUS.value)
        for (icon in STATUS_ICONS) {
            intent.putExtra(icon, "hide")
        }
        sendBroadcast(intent)

        intent.putExtra(DemoMode.EXTRA_COMMAND.value, DemoMode.COMMAND_NOTIFICATIONS.value)
        intent.putExtra("visible", "false")
        sendBroadcast(intent)

        devSettings.setGlobal(GLOBAL_DEMO_MODE_ON, "1", DATA_TYPE_INT)
    }

    private fun getDeviceVersionForDemoClock(): String {
        return String.format("0%s00", Build.VERSION.RELEASE.substring(0, 1))
    }

    private fun stopDemoMode() {
        val intent = Intent(DemoMode.ACTION_DEMO.value)
        intent.putExtra(DemoMode.EXTRA_COMMAND.value, DemoMode.COMMAND_EXIT.value)
        sendBroadcast(intent)
        devSettings.setGlobal(GLOBAL_DEMO_MODE_ON, "0", DATA_TYPE_INT)
    }


}