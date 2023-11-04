package com.popos.core.notifications

import javax.inject.Inject

/**
 * Implementation of [Notifier] which does nothing. Useful for tests and previews.
 */
class NoOpNotifier @Inject constructor() : Notifier {
    override fun showDataDeletionNotification() = Unit

    override fun showReportGenerationNotification() = Unit
}
