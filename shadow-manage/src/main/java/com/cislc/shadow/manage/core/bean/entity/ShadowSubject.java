package com.cislc.shadow.manage.core.bean.entity;

import com.cislc.shadow.manage.core.bean.field.DatabaseField;
import com.cislc.shadow.manage.core.bean.field.EntityField;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ShadowSubject
 * @Description 影子被观察者
 * @Date 2019/10/15 14:45
 * @author szh
 **/
public class ShadowSubject {

    private List<ShadowObserver> observers = new ArrayList<>();

    /**
     * @Description 增加观察者
     * @param o 观察者
     * @author szh
     * @Date 2019/6/5 20:30
     */
    public void addObserver(ShadowObserver o) {
        this.observers.add(o);
    }

    /**
     * @Description 删除观察者
     * @param o 观察者
     * @author szh
     * @Date 2019/6/5 20:30
     */
    public void  delObserver(ShadowObserver o) {
        this.observers.remove(o);
    }

    /**
     * @Description 通知观察者
     * @param data 更改数据
     * @param field 实体变化属性
     * @author szh
     * @Date 2019/6/5 20:30
     */
    protected void notifyObservers(DatabaseField data, EntityField field) {
        for (ShadowObserver o : observers) {
            o.onFieldUpdate(data, field);
        }
    }

}
