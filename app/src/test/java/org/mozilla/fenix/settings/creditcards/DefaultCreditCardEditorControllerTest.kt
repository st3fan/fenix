/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.settings.creditcards

import androidx.navigation.NavController
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import mozilla.components.concept.storage.UpdatableCreditCardFields
import mozilla.components.service.sync.autofill.AutofillCreditCardsAddressesStorage
import mozilla.components.support.test.rule.MainCoroutineRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mozilla.fenix.settings.creditcards.controller.DefaultCreditCardEditorController

@ExperimentalCoroutinesApi
class DefaultCreditCardEditorControllerTest {

    private val storage: AutofillCreditCardsAddressesStorage = mockk(relaxed = true)
    private val navController: NavController = mockk(relaxed = true)

    private val testCoroutineScope = TestCoroutineScope()
    private val testDispatcher = TestCoroutineDispatcher()

    private lateinit var controller: DefaultCreditCardEditorController

    @get:Rule
    val coroutinesTestRule = MainCoroutineRule(testDispatcher)

    @Before
    fun setup() {
        controller = spyk(
            DefaultCreditCardEditorController(
                storage = storage,
                lifecycleScope = testCoroutineScope,
                navController = navController,
                ioDispatcher = testDispatcher
            )
        )
    }

    @After
    fun cleanUp() {
        testCoroutineScope.cleanupTestCoroutines()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun handleSaveCreditCard() = testCoroutineScope.runBlockingTest {
        val creditCardFields = UpdatableCreditCardFields(
            billingName = "Banana Apple",
            cardNumber = "4111111111111112",
            expiryMonth = 1,
            expiryYear = 2030,
            cardType = "discover"
        )

        controller.handleSaveCreditCard(creditCardFields)

        coVerify {
            storage.addCreditCard(creditCardFields)
            navController.popBackStack()
        }
    }
}
