package com.github.simonpham.verification.service.tiles

import android.service.quicksettings.Tile
import com.github.simonpham.verification.SYSPROP_DEBUG_LAYOUT
import com.github.simonpham.verification.service.BaseTileService

/**
 * Created by Simon Pham on 5/30/18.
 * Email: simonpham.dn@gmail.com
 */

class ShowLayoutBoundsService : BaseTileService() {

    override fun refresh() {
        val enabled = SystemProperties.getBoolean(SYSPROP_DEBUG_LAYOUT, false)
        qsTile.state = if (enabled) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        qsTile.updateTile()
    }

    override fun onClick() {
        devSettings.setSystemProp(SYSPROP_DEBUG_LAYOUT,
                if (qsTile.state == Tile.STATE_INACTIVE) "true" else "false", true)
        refresh()
    }
}