package io.qameta.htmlelements.handler;

import io.qameta.htmlelements.annotation.FindBy;
import io.qameta.htmlelements.context.WebElementContext;
import io.qameta.htmlelements.locator.Annotations;
import io.qameta.htmlelements.locator.DefaultElementLocator;
import io.qameta.htmlelements.proxy.Proxies;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

class HtmlElementListMethodCallHandler implements MethodCallHandler {

    @Override
    public boolean canHandle(Method method) {
        return List.class.isAssignableFrom(method.getReturnType()) && method.isAnnotationPresent(FindBy.class);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Annotations annotations = new Annotations(method, args);
        ElementLocator locator = new DefaultElementLocator((SearchContext) proxy, annotations);

        Type methodReturnType = ((ParameterizedType) annotations.getMethod()
                .getGenericReturnType()).getActualTypeArguments()[0];

        WebElementContext context = new WebElementContext((Class<?>) methodReturnType, locator);
        return Proxies.simpleProxy(method.getReturnType(), new LocatingElementListHandler(context));
    }
}
