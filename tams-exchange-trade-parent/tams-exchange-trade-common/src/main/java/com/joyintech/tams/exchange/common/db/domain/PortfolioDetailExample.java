package com.joyintech.tams.exchange.common.db.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author swwan
 * @date 2022/5/10 - 22:29
 */
public class PortfolioDetailExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public PortfolioDetailExample() {
        oredCriteria = new ArrayList<>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("ID is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("ID is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(String value) {
            addCriterion("ID =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(String value) {
            addCriterion("ID <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(String value) {
            addCriterion("ID >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(String value) {
            addCriterion("ID >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(String value) {
            addCriterion("ID <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(String value) {
            addCriterion("ID <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLike(String value) {
            addCriterion("ID like", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotLike(String value) {
            addCriterion("ID not like", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<String> values) {
            addCriterion("ID in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<String> values) {
            addCriterion("ID not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(String value1, String value2) {
            addCriterion("ID between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(String value1, String value2) {
            addCriterion("ID not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andBusiTypeIsNull() {
            addCriterion("BUSI_TYPE is null");
            return (Criteria) this;
        }

        public Criteria andBusiTypeIsNotNull() {
            addCriterion("BUSI_TYPE is not null");
            return (Criteria) this;
        }

        public Criteria andBusiTypeEqualTo(String value) {
            addCriterion("BUSI_TYPE =", value, "busiType");
            return (Criteria) this;
        }

        public Criteria andBusiTypeNotEqualTo(String value) {
            addCriterion("BUSI_TYPE <>", value, "busiType");
            return (Criteria) this;
        }

        public Criteria andBusiTypeGreaterThan(String value) {
            addCriterion("BUSI_TYPE >", value, "busiType");
            return (Criteria) this;
        }

        public Criteria andBusiTypeGreaterThanOrEqualTo(String value) {
            addCriterion("BUSI_TYPE >=", value, "busiType");
            return (Criteria) this;
        }

        public Criteria andBusiTypeLessThan(String value) {
            addCriterion("BUSI_TYPE <", value, "busiType");
            return (Criteria) this;
        }

        public Criteria andBusiTypeLessThanOrEqualTo(String value) {
            addCriterion("BUSI_TYPE <=", value, "busiType");
            return (Criteria) this;
        }

        public Criteria andBusiTypeLike(String value) {
            addCriterion("BUSI_TYPE like", value, "busiType");
            return (Criteria) this;
        }

        public Criteria andBusiTypeNotLike(String value) {
            addCriterion("BUSI_TYPE not like", value, "busiType");
            return (Criteria) this;
        }

        public Criteria andBusiTypeIn(List<String> values) {
            addCriterion("BUSI_TYPE in", values, "busiType");
            return (Criteria) this;
        }

        public Criteria andBusiTypeNotIn(List<String> values) {
            addCriterion("BUSI_TYPE not in", values, "busiType");
            return (Criteria) this;
        }

        public Criteria andBusiTypeBetween(String value1, String value2) {
            addCriterion("BUSI_TYPE between", value1, value2, "busiType");
            return (Criteria) this;
        }

        public Criteria andBusiTypeNotBetween(String value1, String value2) {
            addCriterion("BUSI_TYPE not between", value1, value2, "busiType");
            return (Criteria) this;
        }

        public Criteria andBusiIdIsNull() {
            addCriterion("BUSI_ID is null");
            return (Criteria) this;
        }

        public Criteria andBusiIdIsNotNull() {
            addCriterion("BUSI_ID is not null");
            return (Criteria) this;
        }

        public Criteria andBusiIdEqualTo(String value) {
            addCriterion("BUSI_ID =", value, "busiId");
            return (Criteria) this;
        }

        public Criteria andBusiIdNotEqualTo(String value) {
            addCriterion("BUSI_ID <>", value, "busiId");
            return (Criteria) this;
        }

        public Criteria andBusiIdGreaterThan(String value) {
            addCriterion("BUSI_ID >", value, "busiId");
            return (Criteria) this;
        }

        public Criteria andBusiIdGreaterThanOrEqualTo(String value) {
            addCriterion("BUSI_ID >=", value, "busiId");
            return (Criteria) this;
        }

        public Criteria andBusiIdLessThan(String value) {
            addCriterion("BUSI_ID <", value, "busiId");
            return (Criteria) this;
        }

        public Criteria andBusiIdLessThanOrEqualTo(String value) {
            addCriterion("BUSI_ID <=", value, "busiId");
            return (Criteria) this;
        }

        public Criteria andBusiIdLike(String value) {
            addCriterion("BUSI_ID like", value, "busiId");
            return (Criteria) this;
        }

        public Criteria andBusiIdNotLike(String value) {
            addCriterion("BUSI_ID not like", value, "busiId");
            return (Criteria) this;
        }

        public Criteria andBusiIdIn(List<String> values) {
            addCriterion("BUSI_ID in", values, "busiId");
            return (Criteria) this;
        }

        public Criteria andBusiIdNotIn(List<String> values) {
            addCriterion("BUSI_ID not in", values, "busiId");
            return (Criteria) this;
        }

        public Criteria andBusiIdBetween(String value1, String value2) {
            addCriterion("BUSI_ID between", value1, value2, "busiId");
            return (Criteria) this;
        }

        public Criteria andBusiIdNotBetween(String value1, String value2) {
            addCriterion("BUSI_ID not between", value1, value2, "busiId");
            return (Criteria) this;
        }

        public Criteria andBusiStatusIsNull() {
            addCriterion("BUSI_STATUS is null");
            return (Criteria) this;
        }

        public Criteria andBusiStatusIsNotNull() {
            addCriterion("BUSI_STATUS is not null");
            return (Criteria) this;
        }

        public Criteria andBusiStatusEqualTo(String value) {
            addCriterion("BUSI_STATUS =", value, "busiStatus");
            return (Criteria) this;
        }

        public Criteria andBusiStatusNotEqualTo(String value) {
            addCriterion("BUSI_STATUS <>", value, "busiStatus");
            return (Criteria) this;
        }

        public Criteria andBusiStatusGreaterThan(String value) {
            addCriterion("BUSI_STATUS >", value, "busiStatus");
            return (Criteria) this;
        }

        public Criteria andBusiStatusGreaterThanOrEqualTo(String value) {
            addCriterion("BUSI_STATUS >=", value, "busiStatus");
            return (Criteria) this;
        }

        public Criteria andBusiStatusLessThan(String value) {
            addCriterion("BUSI_STATUS <", value, "busiStatus");
            return (Criteria) this;
        }

        public Criteria andBusiStatusLessThanOrEqualTo(String value) {
            addCriterion("BUSI_STATUS <=", value, "busiStatus");
            return (Criteria) this;
        }

        public Criteria andBusiStatusLike(String value) {
            addCriterion("BUSI_STATUS like", value, "busiStatus");
            return (Criteria) this;
        }

        public Criteria andBusiStatusNotLike(String value) {
            addCriterion("BUSI_STATUS not like", value, "busiStatus");
            return (Criteria) this;
        }

        public Criteria andBusiStatusIn(List<String> values) {
            addCriterion("BUSI_STATUS in", values, "busiStatus");
            return (Criteria) this;
        }

        public Criteria andBusiStatusNotIn(List<String> values) {
            addCriterion("BUSI_STATUS not in", values, "busiStatus");
            return (Criteria) this;
        }

        public Criteria andBusiStatusBetween(String value1, String value2) {
            addCriterion("BUSI_STATUS between", value1, value2, "busiStatus");
            return (Criteria) this;
        }

        public Criteria andBusiStatusNotBetween(String value1, String value2) {
            addCriterion("BUSI_STATUS not between", value1, value2, "busiStatus");
            return (Criteria) this;
        }

        public Criteria andPortfolioIdIsNull() {
            addCriterion("PORTFOLIO_ID is null");
            return (Criteria) this;
        }

        public Criteria andPortfolioIdIsNotNull() {
            addCriterion("PORTFOLIO_ID is not null");
            return (Criteria) this;
        }

        public Criteria andPortfolioIdEqualTo(String value) {
            addCriterion("PORTFOLIO_ID =", value, "portfolioId");
            return (Criteria) this;
        }

        public Criteria andPortfolioIdNotEqualTo(String value) {
            addCriterion("PORTFOLIO_ID <>", value, "portfolioId");
            return (Criteria) this;
        }

        public Criteria andPortfolioIdGreaterThan(String value) {
            addCriterion("PORTFOLIO_ID >", value, "portfolioId");
            return (Criteria) this;
        }

        public Criteria andPortfolioIdGreaterThanOrEqualTo(String value) {
            addCriterion("PORTFOLIO_ID >=", value, "portfolioId");
            return (Criteria) this;
        }

        public Criteria andPortfolioIdLessThan(String value) {
            addCriterion("PORTFOLIO_ID <", value, "portfolioId");
            return (Criteria) this;
        }

        public Criteria andPortfolioIdLessThanOrEqualTo(String value) {
            addCriterion("PORTFOLIO_ID <=", value, "portfolioId");
            return (Criteria) this;
        }

        public Criteria andPortfolioIdLike(String value) {
            addCriterion("PORTFOLIO_ID like", value, "portfolioId");
            return (Criteria) this;
        }

        public Criteria andPortfolioIdNotLike(String value) {
            addCriterion("PORTFOLIO_ID not like", value, "portfolioId");
            return (Criteria) this;
        }

        public Criteria andPortfolioIdIn(List<String> values) {
            addCriterion("PORTFOLIO_ID in", values, "portfolioId");
            return (Criteria) this;
        }

        public Criteria andPortfolioIdNotIn(List<String> values) {
            addCriterion("PORTFOLIO_ID not in", values, "portfolioId");
            return (Criteria) this;
        }

        public Criteria andPortfolioIdBetween(String value1, String value2) {
            addCriterion("PORTFOLIO_ID between", value1, value2, "portfolioId");
            return (Criteria) this;
        }

        public Criteria andPortfolioIdNotBetween(String value1, String value2) {
            addCriterion("PORTFOLIO_ID not between", value1, value2, "portfolioId");
            return (Criteria) this;
        }

        public Criteria andPortfolioNameIsNull() {
            addCriterion("PORTFOLIO_NAME is null");
            return (Criteria) this;
        }

        public Criteria andPortfolioNameIsNotNull() {
            addCriterion("PORTFOLIO_NAME is not null");
            return (Criteria) this;
        }

        public Criteria andPortfolioNameEqualTo(String value) {
            addCriterion("PORTFOLIO_NAME =", value, "portfolioName");
            return (Criteria) this;
        }

        public Criteria andPortfolioNameNotEqualTo(String value) {
            addCriterion("PORTFOLIO_NAME <>", value, "portfolioName");
            return (Criteria) this;
        }

        public Criteria andPortfolioNameGreaterThan(String value) {
            addCriterion("PORTFOLIO_NAME >", value, "portfolioName");
            return (Criteria) this;
        }

        public Criteria andPortfolioNameGreaterThanOrEqualTo(String value) {
            addCriterion("PORTFOLIO_NAME >=", value, "portfolioName");
            return (Criteria) this;
        }

        public Criteria andPortfolioNameLessThan(String value) {
            addCriterion("PORTFOLIO_NAME <", value, "portfolioName");
            return (Criteria) this;
        }

        public Criteria andPortfolioNameLessThanOrEqualTo(String value) {
            addCriterion("PORTFOLIO_NAME <=", value, "portfolioName");
            return (Criteria) this;
        }

        public Criteria andPortfolioNameLike(String value) {
            addCriterion("PORTFOLIO_NAME like", value, "portfolioName");
            return (Criteria) this;
        }

        public Criteria andPortfolioNameNotLike(String value) {
            addCriterion("PORTFOLIO_NAME not like", value, "portfolioName");
            return (Criteria) this;
        }

        public Criteria andPortfolioNameIn(List<String> values) {
            addCriterion("PORTFOLIO_NAME in", values, "portfolioName");
            return (Criteria) this;
        }

        public Criteria andPortfolioNameNotIn(List<String> values) {
            addCriterion("PORTFOLIO_NAME not in", values, "portfolioName");
            return (Criteria) this;
        }

        public Criteria andPortfolioNameBetween(String value1, String value2) {
            addCriterion("PORTFOLIO_NAME between", value1, value2, "portfolioName");
            return (Criteria) this;
        }

        public Criteria andPortfolioNameNotBetween(String value1, String value2) {
            addCriterion("PORTFOLIO_NAME not between", value1, value2, "portfolioName");
            return (Criteria) this;
        }

        public Criteria andTargetAmtIsNull() {
            addCriterion("TARGET_AMT is null");
            return (Criteria) this;
        }

        public Criteria andTargetAmtIsNotNull() {
            addCriterion("TARGET_AMT is not null");
            return (Criteria) this;
        }

        public Criteria andTargetAmtEqualTo(BigDecimal value) {
            addCriterion("TARGET_AMT =", value, "targetAmt");
            return (Criteria) this;
        }

        public Criteria andTargetAmtNotEqualTo(BigDecimal value) {
            addCriterion("TARGET_AMT <>", value, "targetAmt");
            return (Criteria) this;
        }

        public Criteria andTargetAmtGreaterThan(BigDecimal value) {
            addCriterion("TARGET_AMT >", value, "targetAmt");
            return (Criteria) this;
        }

        public Criteria andTargetAmtGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("TARGET_AMT >=", value, "targetAmt");
            return (Criteria) this;
        }

        public Criteria andTargetAmtLessThan(BigDecimal value) {
            addCriterion("TARGET_AMT <", value, "targetAmt");
            return (Criteria) this;
        }

        public Criteria andTargetAmtLessThanOrEqualTo(BigDecimal value) {
            addCriterion("TARGET_AMT <=", value, "targetAmt");
            return (Criteria) this;
        }

        public Criteria andTargetAmtIn(List<BigDecimal> values) {
            addCriterion("TARGET_AMT in", values, "targetAmt");
            return (Criteria) this;
        }

        public Criteria andTargetAmtNotIn(List<BigDecimal> values) {
            addCriterion("TARGET_AMT not in", values, "targetAmt");
            return (Criteria) this;
        }

        public Criteria andTargetAmtBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("TARGET_AMT between", value1, value2, "targetAmt");
            return (Criteria) this;
        }

        public Criteria andTargetAmtNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("TARGET_AMT not between", value1, value2, "targetAmt");
            return (Criteria) this;
        }

        public Criteria andTargetNumIsNull() {
            addCriterion("TARGET_NUM is null");
            return (Criteria) this;
        }

        public Criteria andTargetNumIsNotNull() {
            addCriterion("TARGET_NUM is not null");
            return (Criteria) this;
        }

        public Criteria andTargetNumEqualTo(BigDecimal value) {
            addCriterion("TARGET_NUM =", value, "targetNum");
            return (Criteria) this;
        }

        public Criteria andTargetNumNotEqualTo(BigDecimal value) {
            addCriterion("TARGET_NUM <>", value, "targetNum");
            return (Criteria) this;
        }

        public Criteria andTargetNumGreaterThan(BigDecimal value) {
            addCriterion("TARGET_NUM >", value, "targetNum");
            return (Criteria) this;
        }

        public Criteria andTargetNumGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("TARGET_NUM >=", value, "targetNum");
            return (Criteria) this;
        }

        public Criteria andTargetNumLessThan(BigDecimal value) {
            addCriterion("TARGET_NUM <", value, "targetNum");
            return (Criteria) this;
        }

        public Criteria andTargetNumLessThanOrEqualTo(BigDecimal value) {
            addCriterion("TARGET_NUM <=", value, "targetNum");
            return (Criteria) this;
        }

        public Criteria andTargetNumIn(List<BigDecimal> values) {
            addCriterion("TARGET_NUM in", values, "targetNum");
            return (Criteria) this;
        }

        public Criteria andTargetNumNotIn(List<BigDecimal> values) {
            addCriterion("TARGET_NUM not in", values, "targetNum");
            return (Criteria) this;
        }

        public Criteria andTargetNumBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("TARGET_NUM between", value1, value2, "targetNum");
            return (Criteria) this;
        }

        public Criteria andTargetNumNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("TARGET_NUM not between", value1, value2, "targetNum");
            return (Criteria) this;
        }

        public Criteria andTargetTypeIsNull() {
            addCriterion("TARGET_TYPE is null");
            return (Criteria) this;
        }

        public Criteria andTargetTypeIsNotNull() {
            addCriterion("TARGET_TYPE is not null");
            return (Criteria) this;
        }

        public Criteria andTargetTypeEqualTo(String value) {
            addCriterion("TARGET_TYPE =", value, "targetType");
            return (Criteria) this;
        }

        public Criteria andTargetTypeNotEqualTo(String value) {
            addCriterion("TARGET_TYPE <>", value, "targetType");
            return (Criteria) this;
        }

        public Criteria andTargetTypeGreaterThan(String value) {
            addCriterion("TARGET_TYPE >", value, "targetType");
            return (Criteria) this;
        }

        public Criteria andTargetTypeGreaterThanOrEqualTo(String value) {
            addCriterion("TARGET_TYPE >=", value, "targetType");
            return (Criteria) this;
        }

        public Criteria andTargetTypeLessThan(String value) {
            addCriterion("TARGET_TYPE <", value, "targetType");
            return (Criteria) this;
        }

        public Criteria andTargetTypeLessThanOrEqualTo(String value) {
            addCriterion("TARGET_TYPE <=", value, "targetType");
            return (Criteria) this;
        }

        public Criteria andTargetTypeLike(String value) {
            addCriterion("TARGET_TYPE like", value, "targetType");
            return (Criteria) this;
        }

        public Criteria andTargetTypeNotLike(String value) {
            addCriterion("TARGET_TYPE not like", value, "targetType");
            return (Criteria) this;
        }

        public Criteria andTargetTypeIn(List<String> values) {
            addCriterion("TARGET_TYPE in", values, "targetType");
            return (Criteria) this;
        }

        public Criteria andTargetTypeNotIn(List<String> values) {
            addCriterion("TARGET_TYPE not in", values, "targetType");
            return (Criteria) this;
        }

        public Criteria andTargetTypeBetween(String value1, String value2) {
            addCriterion("TARGET_TYPE between", value1, value2, "targetType");
            return (Criteria) this;
        }

        public Criteria andTargetTypeNotBetween(String value1, String value2) {
            addCriterion("TARGET_TYPE not between", value1, value2, "targetType");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}