package com.goglezon.jautocache.common;

import com.goglezon.jautocache.model.AutoCacheModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Created by yuwenqi@goglezon.com on 2016/2/29 11:03.
 */
public class ArgumentParser {
    final static Logger logger = LoggerFactory.getLogger(ArgumentParser.class);
    private Object[] args;
    public ArgumentParser(Object[] args){
        this.args=args;
    }
    public String parse() {
        String key = "";
        if (args.length == 0) return "";
        for (Object arg : args) {
            key = key + "_" + getParameterKeySeg(arg);
        }
        return StringUtils.trimLeadingCharacter(key, '_');
    }

    /**
     * 获得单个参数的Segment，其为整个缓存Key的一部分
     *
     * @param param
     * @return
     */
    private String getParameterKeySeg(Object param) {
        String keySegment = "";
        if(param==null){
            logger.info("[Parameter Type]: null");
            return "null";
        }
        logger.info("[Parameter Type]:" + param.getClass().toString());
        if (param.getClass().isPrimitive() || LegalArgTypes.legal(param.getClass())) {
            keySegment += param;
        } else if (param.getClass().isArray()) {//可变参数
            Object[] array = (Object[]) param;
            for (Object item : array) {
                keySegment =keySegment+"_"+getParameterKeySeg(item);
            }
        } else if (param instanceof AutoCacheModel) {
            keySegment += ((AutoCacheModel) param).keyGen();
        } else if(param instanceof Map) {
            return parseMap((Map) param);
        }else if(param instanceof List){
            return parseList((List) param);
        }else {
            throw new RuntimeException("The argument must be an instance of AutoCacheModel");
        }
        return StringUtils.trimLeadingCharacter(keySegment, '_');
    }

    private String parseMap(Map map){
        Set<Map.Entry> entrySet=map.entrySet();
        List valueObject=new ArrayList();
        for(Map.Entry entry:entrySet){
            valueObject.add(entry.getValue());
        }
        return getParameterKeySeg(valueObject.toArray());
    }

    private String parseList(List list){
        return getParameterKeySeg(list.toArray());
    }

}
