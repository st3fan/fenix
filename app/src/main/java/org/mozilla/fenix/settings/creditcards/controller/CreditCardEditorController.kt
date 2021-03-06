/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.settings.creditcards.controller

import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mozilla.components.concept.storage.UpdatableCreditCardFields
import mozilla.components.service.sync.autofill.AutofillCreditCardsAddressesStorage

/**
 * [CreditCardEditorFragment] controller. An interface that handles the view manipulation of the
 * credit card editor.
 */
interface CreditCardEditorController {

    /**
     * Saves the provided credit card field into the credit card storage. Called when a user
     * taps on the save menu item or "Save" button.
     *
     * @param creditCardFields A [UpdatableCreditCardFields] record to add.
     */
    fun handleSaveCreditCard(creditCardFields: UpdatableCreditCardFields)
}

/**
 * The default implementation of [CreditCardEditorController].
 *
 * @param storage An instance of the [AutofillCreditCardsAddressesStorage] for adding and retrieving
 * credit cards.
 * @param lifecycleScope [CoroutineScope] scope to launch coroutines.
 * @param navController [NavController] used for navigation.
 * @param ioDispatcher [CoroutineDispatcher] used for executing async tasks. Defaults to [Dispatchers.IO].
 */
class DefaultCreditCardEditorController(
    private val storage: AutofillCreditCardsAddressesStorage,
    private val lifecycleScope: CoroutineScope,
    private val navController: NavController,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CreditCardEditorController {

    override fun handleSaveCreditCard(creditCardFields: UpdatableCreditCardFields) {
        lifecycleScope.launch(ioDispatcher) {
            storage.addCreditCard(creditCardFields)

            lifecycleScope.launch(Dispatchers.Main) {
                navController.popBackStack()
            }
        }
    }
}
