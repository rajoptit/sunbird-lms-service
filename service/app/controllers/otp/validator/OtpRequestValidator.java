package controllers.otp.validator;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.models.util.StringFormatter;
import org.sunbird.common.request.BaseRequestValidator;
import org.sunbird.common.request.Request;
import org.sunbird.common.responsecode.ResponseCode;
import org.sunbird.common.responsecode.ResponseMessage;

public class OtpRequestValidator extends BaseRequestValidator {

  public void validateGenerateOtpRequest(Request otpRequest) {
    commonValidation(otpRequest, false);
    validateTemplateId(otpRequest);
  }

 private void validateTemplateId(Request otpRequest) {
   String templateId = (String) otpRequest.getRequest().get(JsonKey.TEMPLATE_ID);
   if(StringUtils.isNotBlank(templateId) && !templateId.equalsIgnoreCase(JsonKey.TEMPLATE_ID_VALUE) && !templateId.equalsIgnoreCase(JsonKey.WARD_LOGIN_OTP_TEMPLATE_ID)) {
    throw new ProjectCommonException(
            ResponseCode.invalidIdentifier.getErrorCode(),
            ProjectUtil.formatMessage(
                    ResponseMessage.Message.INVALID_PARAMETER_VALUE, templateId, JsonKey.TEMPLATE_ID),
            ResponseCode.SERVER_ERROR.getResponseCode());
   }
 }

 public void validateVerifyOtpRequest(Request otpRequest) {
    commonValidation(otpRequest, true);
  }

  private void commonValidation(Request otpRequest, boolean isOtpMandatory) {
    validateParam(
        (String) otpRequest.getRequest().get(JsonKey.KEY),
        ResponseCode.mandatoryParamsMissing,
        JsonKey.KEY);
    validateParam(
        (String) otpRequest.getRequest().get(JsonKey.TYPE),
        ResponseCode.mandatoryParamsMissing,
        JsonKey.TYPE);
    if (isOtpMandatory) {
      validateParam(
          (String) otpRequest.getRequest().get(JsonKey.OTP),
          ResponseCode.mandatoryParamsMissing,
          JsonKey.OTP);
    }
    validateTypeAndKey(otpRequest);
  }

  private void validateTypeAndKey(Request otpRequest) {
    Map<String, Object> requestMap = otpRequest.getRequest();
    String userId = (String) requestMap.get(JsonKey.USER_ID);
    String type = (String) requestMap.get(JsonKey.TYPE);
    String key = (String) requestMap.get(JsonKey.KEY);
    validateType(type);
    if (StringUtils.isBlank(userId)) {
      if (JsonKey.EMAIL.equalsIgnoreCase(type)) {
        validateEmail(key);
      } else if (JsonKey.PHONE.equalsIgnoreCase(type)) {
        validatePhone(key);
      }
    }
  }

  private void validateType(String type) {
    List<String> allowedTypes =
        new ArrayList<String>(
            Arrays.asList(
                JsonKey.EMAIL, JsonKey.PHONE, JsonKey.PREV_USED_EMAIL, JsonKey.PREV_USED_PHONE,JsonKey.RECOVERY_EMAIL,JsonKey.RECOVERY_PHONE));
    if (!allowedTypes.contains(type)) {
      ProjectCommonException.throwClientErrorException(
          ResponseCode.invalidValue,
          ProjectUtil.formatMessage(
              ResponseCode.invalidValue.getErrorMessage(),
              JsonKey.TYPE,
              type,
              String.join(
                  StringFormatter.COMMA,
                  JsonKey.EMAIL,
                  JsonKey.PHONE,
                  JsonKey.PREV_USED_EMAIL,
                  JsonKey.PREV_USED_PHONE,JsonKey.RECOVERY_EMAIL,JsonKey.RECOVERY_PHONE)));
    }
  }
}
