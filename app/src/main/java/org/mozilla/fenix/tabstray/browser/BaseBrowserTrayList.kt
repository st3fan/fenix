/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.tabstray.browser

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import mozilla.components.feature.tabs.tabstray.TabsFeature
import org.mozilla.fenix.ext.components
import org.mozilla.fenix.tabstray.TabsTrayInteractor
import org.mozilla.fenix.tabstray.TabsTrayStore
import org.mozilla.fenix.tabstray.TrayItem
import org.mozilla.fenix.tabstray.ext.filterFromConfig

abstract class BaseBrowserTrayList @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr), TrayItem {

    /**
     * The browser tab types we would want to show.
     */
    enum class BrowserTabType { NORMAL, PRIVATE }

    /**
     * A configuration for classes that extend [BaseBrowserTrayList].
     */
    data class Configuration(val browserTabType: BrowserTabType)

    abstract val configuration: Configuration

    lateinit var interactor: TabsTrayInteractor
    lateinit var tabsTrayStore: TabsTrayStore

    private val tabsFeature by lazy {
        // NB: The use cases here are duplicated because there isn't a nicer
        // way to share them without a better dependency injection solution.
        val selectTabUseCase = SelectTabUseCaseWrapper(
            context.components.analytics.metrics,
            context.components.useCases.tabsUseCases.selectTab
        ) {
            interactor.navigateToBrowser()
        }

        val removeTabUseCase = RemoveTabUseCaseWrapper(
            context.components.analytics.metrics
        ) { sessionId ->
            interactor.tabRemoved(sessionId)
        }

        TabsFeature(
            adapter as TabsAdapter,
            context.components.core.store,
            selectTabUseCase,
            removeTabUseCase,
            { it.filterFromConfig(configuration) },
            { }
        )
    }

    private val swipeToDelete by lazy {
        SwipeToDeleteBinding(tabsTrayStore)
    }

    private val touchHelper by lazy {
        TabsTouchHelper(
            observable = adapter as TabsAdapter,
            onViewHolderTouched = { swipeToDelete.isSwipeable },
            onViewHolderDraw = { context.components.settings.listTabView }
        )
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        tabsFeature.start()
        swipeToDelete.start()

        touchHelper.attachToRecyclerView(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        tabsFeature.stop()
        swipeToDelete.stop()

        touchHelper.attachToRecyclerView(null)
    }
}
