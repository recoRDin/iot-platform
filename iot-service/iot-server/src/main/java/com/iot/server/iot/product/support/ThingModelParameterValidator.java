package com.iot.server.iot.product.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.iot.core.log.exception.ServiceException;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public final class ThingModelParameterValidator {

    private static final int MAX_PARAMETERS = 50;

    private static final Set<String> DATA_TYPES = Set.of(
            "int32", "float", "double", "text", "bool", "enum", "date");

    private static final Pattern IDENTIFIER_PATTERN =
            Pattern.compile("^[A-Za-z][A-Za-z0-9_]*$");

    private ThingModelParameterValidator() {
    }

    public static void validateArray(JsonNode parameters, String displayName) {
        if (parameters == null || !parameters.isArray()) {
            throw new ServiceException(displayName + "必须是JSON数组");
        }
        if (parameters.size() > MAX_PARAMETERS) {
            throw new ServiceException(displayName + "不能超过" + MAX_PARAMETERS + "个");
        }

        HashSet<String> identifiers = new HashSet<>();
        for (JsonNode parameter : parameters) {
            validateParameter(parameter, identifiers, displayName);
        }
    }

    private static void validateParameter(JsonNode parameter,
                                          Set<String> identifiers,
                                          String displayName) {
        if (!parameter.isObject()) {
            throw new ServiceException("每个" + displayName + "必须是JSON对象");
        }

        String identifier = getRequiredTextField(
                parameter, "identifier", 64, displayName + "标识符");
        if (!parameter.get("identifier").asText().equals(identifier)
                || !IDENTIFIER_PATTERN.matcher(identifier).matches()) {
            throw new ServiceException(displayName + "标识符格式不正确");
        }
        if (!identifiers.add(identifier)) {
            throw new ServiceException(displayName + "标识符不能重复");
        }

        getRequiredTextField(parameter, "parameterName", 100,
                displayName + "名称");

        String dataType = getRequiredTextField(
                parameter, "dataType", 16, displayName + "数据类型");
        if (!parameter.get("dataType").asText().equals(dataType)
                || !DATA_TYPES.contains(dataType)) {
            throw new ServiceException(displayName + "数据类型不正确");
        }

        ThingModelSpecValidator.validate(dataType, parameter.get("spec"));

        JsonNode requiredNode = parameter.get("required");
        if (requiredNode == null
                || !requiredNode.isIntegralNumber()
                || !requiredNode.canConvertToInt()
                || (requiredNode.intValue() != 0 && requiredNode.intValue() != 1)) {
            throw new ServiceException(displayName + "是否必需只能是0或1");
        }

        validateOptionalTextField(
                parameter, "description", 500, displayName + "说明");

        JsonNode sortOrderNode = parameter.get("sortOrder");
        if (sortOrderNode != null && !sortOrderNode.isNull()
                && (!sortOrderNode.isIntegralNumber()
                || !sortOrderNode.canConvertToInt()
                || sortOrderNode.intValue() < 0)) {
            throw new ServiceException(displayName + "排序号不能小于0");
        }
    }

    private static String getRequiredTextField(JsonNode object,
                                               String fieldName,
                                               int maxLength,
                                               String displayName) {
        JsonNode fieldNode = object.get(fieldName);
        if (fieldNode == null || !fieldNode.isTextual()) {
            throw new ServiceException(displayName + "必须是字符串");
        }

        String value = fieldNode.asText().strip();
        if (value.isEmpty()) {
            throw new ServiceException(displayName + "不能为空");
        }
        if (value.length() > maxLength) {
            throw new ServiceException(
                    displayName + "不能超过" + maxLength + "个字符");
        }
        return value;
    }

    private static void validateOptionalTextField(JsonNode object,
                                                  String fieldName,
                                                  int maxLength,
                                                  String displayName) {
        JsonNode fieldNode = object.get(fieldName);
        if (fieldNode == null || fieldNode.isNull()) {
            return;
        }
        if (!fieldNode.isTextual()) {
            throw new ServiceException(displayName + "必须是字符串");
        }
        if (fieldNode.asText().strip().length() > maxLength) {
            throw new ServiceException(
                    displayName + "不能超过" + maxLength + "个字符");
        }
    }
}
