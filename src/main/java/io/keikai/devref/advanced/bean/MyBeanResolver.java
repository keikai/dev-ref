package io.keikai.devref.advanced.bean;

import io.keikai.devref.advanced.bean.MyBeanService;
import org.zkoss.xel.*;

/**
 * A example of VariableResolver to get JavaBeans
 * @author Hawk
 *
 */
public class MyBeanResolver implements VariableResolver {
	
	@Override
	public Object resolveVariable(String name) throws XelException {
		return MyBeanService.getMyBeanService().get(name);
	}
}
