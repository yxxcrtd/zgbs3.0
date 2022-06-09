/*
 * 文件名：ExprTreeMath.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：加减乘除括号运算封装
 * 修改人：裴家俊
 * 修改时间：2018年12月13日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.invest.common.risktrialrds.dto;
 
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.invest.common.risktrialrds.util.Const;
import com.joyintech.tams.invest.common.risktrialrds.util.RiskUtil;
 
/**
 * 加减乘除括号运算
 * @author 裴家俊
 * @version 1.0 2018年12月13日
 * @see ExprTreeMath
 * @since 1.0
 */
public class ExprTreeMath {
	
	private static final String paramPref = "${";
	private static final String paramPrefTrf = "\\$\\{";
	private static final String paramSuff = "}";
	private static final String paramSuffTrf = "\\}";
	private static final Pattern patternOp = Pattern.compile("[*/+-]");
	private static final Pattern patternOpUnit = Pattern.compile("[()]");
	private static final Pattern patternOpNum = Pattern.compile("[0-9\\.+-/*() ]+");
	private Set<String> paramList;
    private static final Map<String, Integer> OPT_PRIORITY_MAP = new HashMap<String, Integer>();
    
    public ExprTreeMath(String expr){
    	OPT_PRIORITY_MAP.put("(", 0);
		OPT_PRIORITY_MAP.put("+", 2);
		OPT_PRIORITY_MAP.put("-", 2);
		OPT_PRIORITY_MAP.put("*", 3);
		OPT_PRIORITY_MAP.put("/", 3);
		OPT_PRIORITY_MAP.put(")", 7);
    	paramList=new HashSet<String>();
    	parseParamName(expr);
    }
    
    /**
     * 获取指标参数集合
     * @param param
     * @see
     * @since 1.0
     */
    private void parseParamName(String param) {
		if(StringUtil.isNotEmpty(param)&&param.indexOf(paramPref)!=-1&&param.indexOf(paramSuff)!=-1){
			String paramPre=param.substring(param.indexOf(paramPref)+2, param.indexOf(paramSuff));
			String exprNew=param.substring(param.indexOf(paramSuff)+1);
			paramList.add(paramPre);
			parseParamName(exprNew);
		}
	}
    
    public String replaceParamByMath(String expr,Map<String, Object> params,String indType){
    	for(String paramKey:paramList){
    		Object obj = params.get(paramKey);
		    String valueStr = obj == null ? "0" : obj.toString();
		    if(null !=obj && StringUtil.isNotEmpty(obj)&&(indType.indexOf(RiskUtil.LIMIT)!=-1 ||indType.indexOf(RiskUtil.INNER)!=-1)){
				if(RiskUtil.isInvalidDate(valueStr)) {
					long days = DateUtil.parse(valueStr).getTime() / 1000 / 3600 / 24;
					valueStr=String.valueOf(days);
				}else {
					valueStr = obj.toString();
					Double keyValue = Double.valueOf(valueStr);
					if (keyValue < 0D) {
						valueStr = "(0-" + (keyValue * -1) + ")";
					}
				}
		    }
		    //expr=expr.replaceAll(paramPrefTrf+paramKey+paramSuffTrf, params.get(paramKey)==null?paramKey:params.get(paramKey).toString());
		    expr=expr.replaceAll(paramPrefTrf+paramKey+paramSuffTrf, obj ==null?"0":valueStr);
    	}
    	return expr;
    }
    
    /**
     * 判断是否存在明细
     * @param params
     * @return
     */
    public boolean checkListByParams(Map<String, Object> params){
    	for(String paramKey:paramList){
    		if(null!=params.get(paramKey)&& params.get(paramKey) instanceof List){
    			return false;
    		}
    	}
    	return true;
    }
    
    /**
     * 生成List集合map
     * @param params 参数集合
     * @return 返回计算结果
     * @throws Exception 
     */
    @SuppressWarnings("unchecked")
	public List<Map<String, Object>> productListByParams(Map<String, Object> params){
    	List<Map<String, Object>> returnMapList=new ArrayList<Map<String, Object>>();
    	List<Object> listTotal=new ArrayList<Object>();
    	int listCount=0;
    	List<String> listParKey=new ArrayList<String>();
    	for(String paramKey:paramList){
    		//params.get(paramKey)==null?paramKey:params.get(paramKey).toString());
    		if(null!=params.get(paramKey)&& params.get(paramKey) instanceof List){
    			listCount++;
    			listTotal.add(params.get(paramKey));
    			listParKey.add(paramKey);
    		}
    	}
    	if(listCount>1){
    		//保留各个参数LIST的资产代码
    		List<List<String>> listAstCodeTotal=new ArrayList<List<String>>();
    		for(Object listMx:listTotal){
        		if(listMx instanceof List){
        			List<String> listAstCode=new ArrayList<String>();
        			((List) listMx).forEach(item-> {if(null!=((Map<String, Object>) item).get(Const.keyAstCode)){
        				listAstCode.add(((Map<String, Object>) item).get(Const.keyAstCode).toString());
        			}
        			});
        			if(listAstCode.size()>0){
        				listAstCodeTotal.add(listAstCode);
        			}
        		}
        	}
    		List<String> listAstCode1=new ArrayList<String>();
    		if(listAstCodeTotal.size()>0){
    			listAstCode1=listAstCodeTotal.get(0).stream().distinct().collect(Collectors.toList());
    			for(int m=1;m<listAstCodeTotal.size();m++){
    				List<String> copareList= listAstCodeTotal.get(m).stream().distinct().collect(Collectors.toList());
    				listAstCode1 = listAstCode1.stream().filter(item -> copareList.contains(item)).collect(Collectors.toList());
    			}
    		}
    		//循环所有List集合的资产代码相同的合集
    		if(listAstCode1.size()>0){
    			for(String astCode:listAstCode1){
    				Map<String, Object> listMap=new HashMap<String, Object>();
    				for(String paramKey:paramList){
    					if(listParKey.contains(paramKey)){
	    					 Object astSingleDto=((List)params.get(paramKey)).stream().filter(item->((Map)item).get(Const.keyAstCode).equals(astCode)).collect(Collectors.toList());
	    					 if(null!=astSingleDto){
		    					 Map<String,Object> totalDtoMap=(Map<String,Object>)((List)astSingleDto).get(0);
		     					 listMap.put(paramKey, totalDtoMap.get(Const.keyValue));//赋实际值
		     					 listMap.put(Const.parAstCode, totalDtoMap.get(Const.keyAstCode));//赋资产代码
		     					 if(null!=totalDtoMap.get(Const.keyAstName))
		     						 listMap.put(Const.parAstName, totalDtoMap.get(Const.keyAstName));//赋资产名称
	    					 }
    					}
    					else{
        					listMap.put(paramKey, params.get(paramKey));
        				}
    				}
    				returnMapList.add(listMap);
    			}
    		}
    		
    	}
    	else if(listCount==1){
    		for(Object totalDto:(List<Object>)listTotal.get(0)){
    			Map<String, Object> listMap=new HashMap<String, Object>();
    			for(String paramKey:paramList){
    				if(listParKey.contains(paramKey)){
    					Map<String,Object> totalDtoMap=(Map<String,Object>)totalDto;
    					listMap.put(paramKey, totalDtoMap.get(Const.keyValue));//赋实际值
    					listMap.put(Const.parAstCode, totalDtoMap.get(Const.keyAstCode));//赋资产代码
    					if(null!=totalDtoMap.get(Const.keyAstName))
    						listMap.put(Const.parAstName, totalDtoMap.get(Const.keyAstName));//赋资产名称
    				}
    				else{
    					listMap.put(paramKey, params.get(paramKey));
    				}
    			}
    			returnMapList.add(listMap);
    		}
    	}
    	else{
    		returnMapList.add(params);
    	}
    	return returnMapList;
    }
    /**
     * 输入加减乘除表达式字符串，返回计算结果
     * @param expression 表达式字符串
     * @return 返回计算结果
     * @throws Exception 
     */
    public  Object calc(String expression){
        Stack<String> optStack = new Stack<>(); // 运算符栈
        Stack<BigDecimal> numStack = new Stack<>(); // 数值栈，数值以BigDecimal存储计算
        StringBuilder curNumBuilder = new StringBuilder(16); // 当前正在读取中的数值字符
        //ZGBSS-211 信用等级在AA+以下的债券 参数会触发计算 start
        if(expression.indexOf("||")!=-1) {
        	return expression;
        }
        //ZGBSS-211 信用等级在AA+以下的债券 参数会触发计算 end
        if(! patternOp.matcher(expression).find()) {
        	  if(! patternOpUnit.matcher(expression).find()) {
        		  return expression;
        	  }
        	  else{
        	  	//判断禁投项中内容带括号的比如计划类(利率型)
				  if(!Pattern.compile("[0-9]*").matcher(expression).matches()) {
					  return expression;
				  }
        		  return expression.replace("(", "").replace(")", "");
        	  }
		}
        else{
        	 if(!patternOpNum.matcher(expression).find()) {
        		throw new IllegalArgumentException("cannot get number from parameters for expression");
        	}
        	 //判断是否包含汉字
			if(Pattern.compile("[\u4e00-\u9fa5]").matcher(expression).find()){
				return expression;
			}
        }
        boolean kxjsf=false;
        // 逐个读取字符，并根据运算符判断参与何种计算
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c != ' ') { // 空白字符直接丢弃掉
                if ((c >= '0' && c <= '9') || c == '.'|| c=='E'||c=='e') {
                    curNumBuilder.append(c); // 持续读取一个数值的各个字符
                    if(c=='E'||c=='e'){
                    	kxjsf=true;
                    }
                    else{
                    	kxjsf=false;
                    }
                } else {
                	 if(c=='-'&&kxjsf){
                		 curNumBuilder.append(c); // 持续读取一个数值的各个字符
                		 kxjsf=false;
                		 continue;
                	 }
                    if (curNumBuilder.length() > 0) {
                        // 如果追加器有值，说明之前读取的字符是数值，而且此时已经完整读取完一个数值
                        numStack.push(new BigDecimal(curNumBuilder.toString()));
                        curNumBuilder.delete(0, curNumBuilder.length());
                    }
                	
                    String curOpt = String.valueOf(c);
                    if (optStack.empty()) {
                        // 运算符栈栈顶为空则直接入栈
                        optStack.push(curOpt);
                    } else {
                        if (curOpt.equals("(")) {
                            // 当前运算符为左括号，直接入运算符栈
                            optStack.push(curOpt);
                        } else if (curOpt.equals(")")) {
                            // 当前运算符为右括号，触发括号内的字表达式进行计算
                            directCalc(optStack, numStack, true);
                        } else {
                            // 当前运算符为加减乘除之一，要与栈顶运算符比较，判断是否要进行一次二元计算
                            compareAndCalc(optStack, numStack, curOpt);
                        }
                    }
                }
            }
        }
 
        // 表达式不是以等号结尾的场景
        if (curNumBuilder.length() > 0) {
    	    // 如果追加器有值，说明之前读取的字符是数值，而且此时已经完整读取完一个数值
            numStack.push(new BigDecimal(curNumBuilder.toString()));
    	}
        //---add by 裴家俊  2019-03-26-----------------
        if(optStack.isEmpty()){
        	return numStack.pop().doubleValue();
        }
      //---add by 裴家俊  2019-03-26-----------------
        directCalc(optStack, numStack, false);
        return numStack.pop().doubleValue();
    }
    
    /**
     * 拿当前运算符和栈顶运算符对比，如果栈顶运算符优先级高于或同级于当前运算符，
     * 则执行一次二元运算（递归比较并计算），否则当前运算符入栈
     * @param optStack 运算符栈
     * @param numStack 数值栈
     * @param curOpt 当前运算符
     */
    public static void compareAndCalc(Stack<String> optStack, Stack<BigDecimal> numStack, 
    		String curOpt) {
        // 比较当前运算符和栈顶运算符的优先级
        String peekOpt = optStack.peek();
        int priority = getPriority(peekOpt, curOpt);
        if (priority == -1 || priority == 0) {
            // 栈顶运算符优先级大或同级，触发一次运算
            String opt = optStack.pop(); // 当前参与计算运算符
            BigDecimal num2 = numStack.pop(); // 当前参与计算数值2
            BigDecimal num1 = numStack.pop(); // 当前参与计算数值1
            BigDecimal bigDecimal = floatPointCalc(opt, num1, num2);
            
            // 计算结果当做操作数入栈
            numStack.push(bigDecimal);
            
            // 运算完栈顶还有运算符，则还需要再次触发一次比较判断是否需要再次计算
            if (optStack.empty()) {
                optStack.push(curOpt);
            } else {
                compareAndCalc(optStack, numStack, curOpt);
            }
        } else {
            // 当前运算符优先级高，则直接入栈
            optStack.push(curOpt);
        }
    }
    
    /**
     * 遇到右括号和等号执行的连续计算操作（递归计算）
     * @param optStack 运算符栈
     * @param numStack 数值栈
     * @param isBracket true表示为括号类型计算
     */
    public static void directCalc(Stack<String> optStack, Stack<BigDecimal> numStack, 
    		boolean isBracket) {
    	//----update by 裴家俊 20200508 处理(a)这种计算模式 start ----
        if (isBracket) {
            if ("(".equals(optStack.peek())) {
                // 括号类型则遇左括号停止计算，同时将左括号从栈中移除
                optStack.pop();
                return;
            }
        }
        String opt = optStack.pop(); // 计算运算符
        BigDecimal num2 = numStack.pop(); // 计算数值2
        BigDecimal num1 = numStack.pop(); // 计算数值1
        BigDecimal bigDecimal = floatPointCalc(opt, num1, num2);
        
        // 计算结果当做操作数入栈
        numStack.push(bigDecimal);
        
        if (isBracket) {
            if ("(".equals(optStack.peek())) {
                // 括号类型则遇左括号停止计算，同时将左括号从栈中移除
                optStack.pop();
            } else {
                directCalc(optStack, numStack, isBracket);
            }
        } else {
            if (!optStack.empty()) {
                // 等号类型只要栈中还有运算符就继续计算
                directCalc(optStack, numStack, isBracket);
            }
        }
		//----update by 裴家俊 20200508 处理(a)这种计算模式 end ----
    }
    
    /**
     * 高精度计算
     * @param opt
     * @param num1
     * @param num2
     * @return
     */
    public static BigDecimal floatPointCalc(String opt, BigDecimal bigDecimal1, 
    		BigDecimal bigDecimal2) {
        BigDecimal resultBigDecimal = new BigDecimal(0);
        switch (opt) {
            case "+":
                resultBigDecimal = bigDecimal1.add(bigDecimal2);
                break;
            case "-":
                resultBigDecimal = bigDecimal1.subtract(bigDecimal2);
                break;
            case "*":
                resultBigDecimal = bigDecimal1.multiply(bigDecimal2);
                break;
            case "/":
                resultBigDecimal = bigDecimal1.divide(bigDecimal2, 10, BigDecimal.ROUND_HALF_DOWN); 
                break;
            default:
                break;
        }
        return resultBigDecimal;
    }
    
    /**
     * priority = 0  表示两个运算符同级别
     * priority = 1  第二个运算符级别高，负数则相反
     */
    public static int getPriority(String opt1, String opt2) {
        int priority = OPT_PRIORITY_MAP.get(opt2) - OPT_PRIORITY_MAP.get(opt1);
        return priority;
    }
 
	public Set<String> getParamList() {
		return paramList;
	}

	public void setParamList(Set<String> paramList) {
		this.paramList = paramList;
	}
    
}
