/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.settings.creditcards.controller

import androidx.navigation.NavController
import org.mozilla.fenix.settings.creditcards.CreditCardsManagementFragmentDirections

/**
 * [CreditCardsManagementFragment] controller. An interface that handles the view manipulation of
 * the credit cards manager triggered by the Interactor.
 */
interface CreditCardsManagementController {

    /**
     * @see [CreditCardsManagementInteractor.onSelectCreditCard]
     */
    fun handleCreditCardClicked()
}

/**
 * The default implementation of [CreditCardsManagementController].
 */
class DefaultCreditCardsManagementController(
    private val navController: NavController
) : CreditCardsManagementController {

    override fun handleCreditCardClicked() {
        navController.navigate(
            CreditCardsManagementFragmentDirections
                .actionCreditCardsManagementFragmentToCreditCardEditorFragment()
        )
    }
}
