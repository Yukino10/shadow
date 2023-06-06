package com.shadow.web.model.resolver;

import com.shadow.web.utils.ReturnMessage;
import org.apache.ibatis.exceptions.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 异常捕获
 * 原本只是用作参数异常的补货和处理，但其实可以全局补货异常然后统一处理
 * TODO 补货没有被处理的异常，然后统一返回，这样前端就不会显示系统的曝出的异常信息
 */

@Component
public class BindExceptionResolver implements HandlerExceptionResolver, Ordered {
    private static Logger log = LoggerFactory.getLogger(BindExceptionResolver.class);

    /**
     * @return 返回非null时，后面的HandlerExceptionResolver将不再执行
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        List<String> errorMsg = new ArrayList<>();
        if (ex instanceof MethodArgumentNotValidException) {
            //实体类中参数校验异常
            BindingResult bindingResult = ((MethodArgumentNotValidException) ex).getBindingResult();
            bindingResult.getFieldErrors().forEach(fieldError -> errorMsg.add(fieldError.getDefaultMessage()));
        } else if (ex instanceof ConstraintViolationException) {
            //方法中参数校验异常
            Set<ConstraintViolation<?>> constraintViolations = ((ConstraintViolationException) ex).getConstraintViolations();
            constraintViolations.forEach(violation -> errorMsg.add(violation.getMessage()));
        } else if (ex instanceof javax.persistence.PersistenceException || ex instanceof PersistenceException) {
            String message = ex.getMessage();
            errorMsg.add(message);
        } else {
            log.error("捕获到没有还没有被处理的异常：" + ex.getMessage());
        }
        if (!errorMsg.isEmpty()) {
            ServletOutputStream out;
            try {
                out = response.getOutputStream();
                ReturnMessage.return_message.Builder builder = ReturnMessage.return_message.newBuilder();
                String msg = errorMsg.get(0);
                builder.setMsg(new String(msg.getBytes(), "utf-8"));
                builder.setMsgCode(2);
                ReturnMessage.return_message message = builder.build();
                message.writeTo(out);
            } catch (UnsupportedEncodingException e) {
                log.error(String.format("BindExceptionResolver failed: UnsupportedEncodingException: %s", e));
            } catch (IOException e) {
                log.error(String.format("BindExceptionResolver failed: IOException: %s", e));
            }
            return new ModelAndView();
        } else {
            return null;
        }
    }

    @Override
    public int getOrder() {
        // 表示此HandlerExceptionResolver的优先级最高
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
