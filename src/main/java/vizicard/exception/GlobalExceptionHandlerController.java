package vizicard.exception;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import static java.util.Arrays.asList;

@RestControllerAdvice
public class GlobalExceptionHandlerController {

  @Value("${spring.profiles.active}")
  private String activeProfile;

  @Bean
  public ErrorAttributes errorAttributes() {
    return new DefaultErrorAttributes() {
      @Override
      public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
//        return super.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults().excluding(ErrorAttributeOptions.Include.EXCEPTION));    // Hide exception field in the return object
        ErrorAttributeOptions errorAttributeOptions = ErrorAttributeOptions.defaults();
        if (!activeProfile.equals("prod")) {
          errorAttributeOptions = errorAttributeOptions.including(ErrorAttributeOptions.Include.MESSAGE);
        }
        return super.getErrorAttributes(webRequest, errorAttributeOptions);
      }
    };
  }

  @ExceptionHandler(CustomException.class)
  public void handleCustomException(HttpServletResponse res, CustomException ex) throws IOException {
    logError(ex, 1);
    res.sendError(ex.getHttpStatus().value(), ex.getMessage());
  }

  @ExceptionHandler(AccessDeniedException.class)
  public void handleAccessDeniedException(HttpServletResponse res) throws IOException {
    res.sendError(HttpStatus.FORBIDDEN.value(), "Access denied");
  }

  @ExceptionHandler(Exception.class)
  public void handleException(HttpServletResponse res, Exception ex) throws IOException {
    logError(ex, 20);
    res.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
  }

  private void logError(Exception exception, int limit) {
    System.err.println("Error: " + exception.getMessage());
    System.err.println(Joiner.on("\n").join(Iterables.limit(asList(exception.getStackTrace()), limit)));
  }

}
