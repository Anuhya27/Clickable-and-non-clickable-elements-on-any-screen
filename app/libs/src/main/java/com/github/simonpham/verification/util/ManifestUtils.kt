package com.github.simonpham.verification.util

import android.content.ComponentName
import android.content.pm.PackageManager
import com.github.simonpham.verification.SingletonInstances

/**
 * Created by Simon Pham on 6/10/18.
 * Email: simonpham.dn@gmail.com
 */

fun toggleComponent(component: Class<*>, state: Boolean) {
    val context = SingletonInstances.getAppContext()
    val componentName = ComponentName(context, component)
    val packageManager = SingletonInstances.getPackageManager()

    val newState = if (state) {
        PackageManager.COMPONENT_ENABLED_STATE_DISABLED
    } else {
        PackageManager.COMPONENT_ENABLED_STATE_ENABLED
    }
    packageManager.setComponentEnabledSetting(componentName, newState, PackageManager.DONT_KILL_APP)
}

fun isComponentEnabled(component: Class<*>): Boolean {
    val context = SingletonInstances.getAppContext()
    val componentName = ComponentName(context, component)
    val packageManager = SingletonInstances.getPackageManager()

    return packageManager.getComponentEnabledSetting(componentName) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
}