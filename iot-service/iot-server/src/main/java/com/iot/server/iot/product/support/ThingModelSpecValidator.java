package com.iot.server.iot.product.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.iot.core.log.exception.ServiceException;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public final class ThingModelSpecValidator {

    private static final Set<String> DATA_TYPES = Set.of(
            "int32", "float", "double", "text", "bool", "enum", "date");

    private ThingModelSpecValidator() {
    }

    public static void validate(String dataType, JsonNode spec) {
        if (dataType == null || !DATA_TYPES.contains(dataType)) {
            throw new ServiceException("数据类型不正确");
        }
        if (spec == null || !spec.isObject()) {
            throw new ServiceException("数据规格必须是JSON对象");
        }

        switch (dataType) {
            case "int32" -> validateNumberSpec(spec, true);
            case "float", "double" -> validateNumberSpec(spec, false);
            case "text" -> validateTextSpec(spec);
            case "bool" -> validateBoolSpec(spec);
            case "enum" -> validateEnumSpec(spec);
            case "date" -> validateDateSpec(spec);
            default -> throw new ServiceException("不支持的数据类型");
        }
    }

    private static void validateNumberSpec(JsonNode spec, boolean integerOnly) {
        JsonNode minNode = spec.get("min");
        JsonNode maxNode = spec.get("max");
        JsonNode stepNode = spec.get("step");

        if (minNode == null || maxNode == null || stepNode == null) {
            throw new ServiceException("数值类型必须配置min、max和step");
        }

        if (integerOnly) {
            if (!minNode.isIntegralNumber()
                    || !maxNode.isIntegralNumber()
                    || !stepNode.isIntegralNumber()) {
                throw new ServiceException("int32类型的min、max和step必须是整数");
            }
            if (!minNode.canConvertToInt()
                    || !maxNode.canConvertToInt()
                    || !stepNode.canConvertToInt()) {
                throw new ServiceException("int32类型的数值超出整数范围");
            }
        } else if (!minNode.isNumber()
                || !maxNode.isNumber()
                || !stepNode.isNumber()) {
            throw new ServiceException("数值类型的min、max和step必须是数字");
        }

        BigDecimal min = minNode.decimalValue();
        BigDecimal max = maxNode.decimalValue();
        BigDecimal step = stepNode.decimalValue();

        if (min.compareTo(max) > 0) {
            throw new ServiceException("最小值不能大于最大值");
        }
        if (step.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("步长必须大于0");
        }

        validateOptionalTextField(spec, "unit", 50, "单位");
        validateOptionalTextField(spec, "unitName", 50, "单位名称");
    }

    private static void validateTextSpec(JsonNode spec) {
        JsonNode maxLengthNode = spec.get("maxLength");
        if (maxLengthNode == null
                || !maxLengthNode.isIntegralNumber()
                || !maxLengthNode.canConvertToInt()) {
            throw new ServiceException("text类型的maxLength必须是整数");
        }

        int maxLength = maxLengthNode.intValue();
        if (maxLength < 1 || maxLength > 10240) {
            throw new ServiceException("text类型的maxLength必须在1到10240之间");
        }
    }

    private static void validateBoolSpec(JsonNode spec) {
        String falseText = getRequiredTextField(
                spec, "falseText", 50, "falseText");
        String trueText = getRequiredTextField(
                spec, "trueText", 50, "trueText");
        if (falseText.equals(trueText)) {
            throw new ServiceException("bool类型的falseText和trueText不能相同");
        }
    }

    private static void validateEnumSpec(JsonNode spec) {
        JsonNode itemsNode = spec.get("items");
        if (itemsNode == null || !itemsNode.isArray() || itemsNode.isEmpty()) {
            throw new ServiceException("enum类型必须配置非空items数组");
        }

        HashSet<Integer> values = new HashSet<>();
        for (JsonNode item : itemsNode) {
            if (!item.isObject()) {
                throw new ServiceException("enum类型的每个枚举项必须是JSON对象");
            }

            JsonNode valueNode = item.get("value");
            if (valueNode == null
                    || !valueNode.isIntegralNumber()
                    || !valueNode.canConvertToInt()) {
                throw new ServiceException("枚举项value必须是整数");
            }
            if (!values.add(valueNode.intValue())) {
                throw new ServiceException("枚举项value不能重复");
            }
            getRequiredTextField(item, "label", 50, "枚举项label");
        }
    }

    private static void validateDateSpec(JsonNode spec) {
        if (!spec.isEmpty()) {
            throw new ServiceException("date类型当前只支持空的数据规格");
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
