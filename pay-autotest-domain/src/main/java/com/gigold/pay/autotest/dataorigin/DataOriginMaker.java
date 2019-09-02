package com.gigold.pay.autotest.dataorigin;

import java.util.List;
import java.util.Map;

/**
 * Created by chenkuan
 * on 16/6/21.
 */
public interface DataOriginMaker {

//    /**
//     * 获取占位符
//     * @return 返回占位符
//     */
//    String getReplaceHolder();
//
//    /**
//     * 获取最新数据
//     * @return 以字符串形式返回最新数据
//     */
//    String getData();
//
//    /**
//     * 跟新数据源
//     * @return 返回更新后的新值
//     */
//    String renewThisOrigin();
//
//    /**
//     * 将新数据插入有效数据列表
//     * @param data 新产生的数据
//     */
//    void addToAvalidList(String data);

    String process(String requestStr, Map<String, String> replacedStrs);


//    List<Class<?>> dataOrigin = DataOriginBase.getAllAssignedClass(DataOriginMaker.class);
}
