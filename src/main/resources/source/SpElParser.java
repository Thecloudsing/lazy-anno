package com.luoan.spel;


import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;

public class SpElParser {
    private static final SpelExpressionParser spelExpressionParser = new SpelExpressionParser();

    public static String out(String spel, Map<String, Object> map) {
        Expression expression = spelExpressionParser.parseExpression(spel, new TemplateParserContext("${", "}"));
        EvaluationContext evaluationContext = new StandardEvaluationContext();
        map.forEach(evaluationContext::setVariable);
        return expression.getValue(evaluationContext, String.class);
    }
}
