package com.sain.commerce.custom.shiping.api;


import com.liferay.commerce.checkout.web.constants.CommerceCheckoutWebKeys;
import com.liferay.commerce.checkout.web.util.CommerceCheckoutStep;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.model.*;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.service.CommerceShippingMethodLocalService;
import com.liferay.commerce.util.CommerceShippingEngineRegistry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;

/**
 * @author jafari
 */
@Component(
        immediate = true,
        property = {
                "service.ranking:Integer=100",
                "commerce.checkout.step.name=" + SainCustomShipingMethod.NAME,
                "commerce.checkout.step.order:Integer=20"
        },
        service = CommerceCheckoutStep.class
)

public class SainCustomShipingMethod implements CommerceCheckoutStep {

    public static final String NAME = "shipping-method";

    @Override
    public String getLabel(Locale locale) {
        return _defaultService.getLabel(locale);
    }

    @Override
    public String getName() {
        return _defaultService.getName();
    }

    @Override
    public boolean isActive(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse)
            throws Exception {

        CommerceOrder commerceOrder = (CommerceOrder) httpServletRequest.getAttribute(
                CommerceCheckoutWebKeys.COMMERCE_ORDER);
        CommerceContext commerceContext =
                (CommerceContext) httpServletRequest.getAttribute(
                        CommerceWebKeys.COMMERCE_CONTEXT);
        ThemeDisplay themeDisplay = (ThemeDisplay) httpServletRequest.getAttribute(
                WebKeys.THEME_DISPLAY);
        CommerceAddress shippingAddress = commerceOrder.getShippingAddress();

        if (Validator.isNotNull(shippingAddress)) {
            List<CommerceShippingMethod> commerceShippingMethodList = _commerceShippingMethodLocalService.getCommerceShippingMethods(
                    commerceOrder.getGroupId(), shippingAddress.getCommerceCountryId(),
                    true);
            for (int i = 0; i < commerceShippingMethodList.size(); i++) {
                CommerceShippingMethod commerceShippingMethod = commerceShippingMethodList.get(i);
                CommerceShippingEngine commerceShippingEngine =
                        _commerceShippingEngineRegistry.getCommerceShippingEngine(
                                commerceShippingMethod.getEngineKey());

                List<CommerceShippingOption> commerceShippingOptions =
                        commerceShippingEngine.getCommerceShippingOptions(
                                commerceContext, commerceOrder, themeDisplay.getLocale());

                for (CommerceShippingOption commerceShippingOption :
                        commerceShippingOptions) {

                    if (commerceShippingOption.getAmount().intValue() > 0) {
                        return true;
                    } else {

                        _commerceOrderLocalService.updateShippingMethod(
                                commerceOrder.getCommerceOrderId(), commerceShippingMethod.getCommerceShippingMethodId(),
                                commerceShippingOption.getName(), commerceShippingOption.getAmount(), commerceContext);

                        return false;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean isOrder() {
        return _defaultService.isOrder();
    }

    @Override
    public boolean isSennaDisabled() {
        return _defaultService.isSennaDisabled();
    }

    @Override
    public boolean isVisible(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        return _defaultService.isVisible(httpServletRequest, httpServletResponse);
    }

    @Override
    public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
        _defaultService.processAction(actionRequest, actionResponse);
    }

    @Override
    public void render(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {

        _defaultService.render(httpServletRequest, httpServletResponse);
    }

    @Override
    public boolean showControls(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return _defaultService.showControls(httpServletRequest, httpServletResponse);
    }

    @Reference(target = "(component.name=com.liferay.commerce.checkout.web.internal.util.ShippingMethodCommerceCheckoutStep)")

    private CommerceCheckoutStep _defaultService;

    @Reference
    private JSPRenderer _jspRenderer;

    @Reference(target = "(osgi.web.symbolicname=com.sain.custom.shiping)")

    private ServletContext _servletContext;

    @Reference
    private CommerceOrderLocalService _commerceOrderLocalService;

    @Reference
    private CommerceShippingMethodLocalService
            _commerceShippingMethodLocalService;

    @Reference
    private CommerceShippingEngineRegistry _commerceShippingEngineRegistry;


}