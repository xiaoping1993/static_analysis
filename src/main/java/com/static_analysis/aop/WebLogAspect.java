package com.static_analysis.aop;

import java.util.HashMap;
import java.util.Map;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

/**
 * 调用接口的切面类
 * @author wangjiping
 *
 */
@Aspect
@Component
public class WebLogAspect  {
	private Logger logger = null; 
	/**
	 * 定义一个切面
	 */
	@Pointcut("execution(* com.static_analysis.controller.*.*(..))")
	public void webLog(){
		
	}
    @Before("webLog()")  
    public void doBefore(JoinPoint joinPoint) throws Throwable {  
        // 接收到请求，记录请求内容  
        Class<?> target = joinPoint.getTarget().getClass();//目标类
        logger = LoggerFactory.getLogger(target);//对应日志成员
        String clazzName = target.getName();//类名
        Object[] args = joinPoint.getArgs();//参数
        String methodName = joinPoint.getSignature().getName();//方法名
        Map<String,Object> nameAndArgs = getFieldsName(this.getClass(), clazzName, methodName, args);//获取被切参数名称及参数值
        logger.info("["+clazzName+"]["+methodName+"]["+nameAndArgs.toString()+"]");
        //获取参数名称和值
    }  
  
/*    @AfterReturning(returning = "ret", pointcut = "webLog()")  
    public void doAfterReturning(Object ret) throws Throwable {  
        // 处理完请求，返回内容  
        logger.info("response : " + ret);
    }*/  
    
    /**
     * 通过反射机制 获取被切参数名以及参数值
     *
     * @param cls
     * @param clazzName
     * @param methodName
     * @param args
     * @return
     * @throws NotFoundException
     */
    private Map<String, Object> getFieldsName(Class<?> cls, String clazzName, String methodName, Object[] args) throws NotFoundException {
        Map<String, Object> map = new HashMap<String, Object>();
        ClassPool pool = ClassPool.getDefault();
        //ClassClassPath classPath = new ClassClassPath(this.getClass());
        ClassClassPath classPath = new ClassClassPath(cls);
        pool.insertClassPath(classPath);

        CtClass cc = pool.get(clazzName);
        CtMethod cm = cc.getDeclaredMethod(methodName);
        MethodInfo methodInfo = cm.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        if (attr == null) {
        }
        int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
        for (int i = 0; i < cm.getParameterTypes().length; i++) {
            map.put(attr.variableName(i + pos), args[i]);//paramNames即参数名
        }
        return map;
    }
}
