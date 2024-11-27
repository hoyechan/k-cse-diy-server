package com.knucse.diy.common.exception.handler;

import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.knucse.diy.common.exception.BaseExceptionHandler;
import com.knucse.diy.common.exception.ExceptionHandlerOrder;
import com.knucse.diy.common.exception.support.custom._CustomException;
import com.knucse.diy.common.util.api.ApiErrorResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
@Order(ExceptionHandlerOrder.CUSTOM_EXCEPTION_HANDLER)
public class CustomExceptionHandler extends BaseExceptionHandler<_CustomException> {

	@ExceptionHandler(_CustomException.class)
	public ResponseEntity<ApiErrorResult> handleCustomException(_CustomException exception) {
		return handleException(exception, exception.getHttpStatus(), exception.getErrorCode());
	}
}
