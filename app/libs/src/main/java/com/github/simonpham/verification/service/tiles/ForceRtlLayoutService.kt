package com.github.simonpham.verification.service.tiles

import android.service.quicksettings.Tile
import com.github.simonpham.verification.SYSPROP_DEBUG_FORCE_RTL
import com.github.simonpham.verification.service.BaseTileService


/**
 * Created by Simon Pham on 5/30/18.
 * Email: simonpham.dn@gmail.com
 */

class ForceRtlLayoutService : BaseTileService() {

    override fun refresh() {
        val enabled = SystemProperties.getInt(SYSPROP_DEBUG_FORCE_RTL, 0) == 1
        qsTile.state = if (enabled) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        qsTile.updateTile()
    }


    override fun onClick() {
        devSettings.setSystemProp(SYSPROP_DEBUG_FORCE_RTL,
                if (qsTile.state == Tile.STATE_INACTIVE) "1" else "0", true)
        refresh()
    }
}