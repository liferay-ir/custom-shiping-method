/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.sain.commerce.custom.payment.method.money.order.internal;

import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.payment.method.CommercePaymentMethod;
import com.liferay.commerce.payment.request.CommercePaymentRequest;
import com.liferay.commerce.payment.result.CommercePaymentResult;

import java.util.Collections;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 */
@Component(
		immediate = true, property =
				{
					"service.ranking:Integer=100",
					"commerce.payment.engine.method.key=" + SainMoneyOrderCommercePaymentMethod.KEY
				},
		service = CommercePaymentMethod.class
)
public class SainMoneyOrderCommercePaymentMethod
	implements CommercePaymentMethod {

	public static final String KEY = "money-order";

	@Override
	public CommercePaymentResult completePayment(
			CommercePaymentRequest commercePaymentRequest)
		throws Exception {

		return new CommercePaymentResult(
			null, commercePaymentRequest.getCommerceOrderId(),
			CommerceOrderConstants.PAYMENT_STATUS_AUTHORIZED, false, null, null,
			Collections.emptyList(), true);
	}

	@Override
	public String getDescription(Locale locale) {
		return _defaultService.getDescription(locale);
	}

	@Override
	public String getKey() {
		return _defaultService.getKey();
	}

	@Override
	public String getName(Locale locale) {
		return _defaultService.getName(locale);
	}

	@Override
	public int getPaymentType() {
		return _defaultService.getPaymentType();
	}

	@Override
	public String getServletPath() {
		return _defaultService.getServletPath();
	}

	@Override
	public boolean isCompleteEnabled() {
		return true;
	}

	@Override
	public boolean isProcessPaymentEnabled() {
		return true;
	}

	@Override
	public CommercePaymentResult processPayment(
			CommercePaymentRequest commercePaymentRequest)
		throws Exception {
		return _defaultService.processPayment(commercePaymentRequest);
	}

	@Reference(
		target = "(component.name=com.liferay.commerce.payment.method.money.order.internal.MoneyOrderCommercePaymentMethod)"
	)
	private CommercePaymentMethod _defaultService;

}