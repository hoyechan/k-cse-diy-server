package com.knucse.diy.common.exception;

import com.knucse.diy.common.util.api.ApiErrorResult;
import com.knucse.diy.common.util.api.ApiResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class BaseExceptionHandler<T extends Throwable> {

	protected ResponseEntity<ApiErrorResult> handleException(T exception, HttpStatus status, String errorCode) {
		log.debug(exception.getMessage(), exception);
		return ResponseEntity.status(status)
			.body(ApiResponseUtil.error(status, errorCode));
	}
}
