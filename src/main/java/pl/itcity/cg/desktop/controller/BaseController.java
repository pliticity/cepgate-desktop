package pl.itcity.cg.desktop.controller;

import java.util.Locale;

import javax.annotation.Resource;

import org.springframework.context.MessageSource;

/**
 * Base controller
 * @author Michal Adamczyk
 */
public abstract class BaseController {
    @Resource
    protected MessageSource messageSource;

    protected String getMessage(String code) {
        return messageSource.getMessage(code, new Object[]{}, code, Locale.getDefault());
    }

    protected String getMessage(String code, Object[] params){
        return messageSource.getMessage(code,params,code,Locale.getDefault());
    }
}
