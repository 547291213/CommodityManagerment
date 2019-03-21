package com.example.commoditymanagerment.RxBus;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class RxBus {

    private volatile static RxBus instance ;
    private final Subject<Object> mBus ;

    private RxBus(){
        mBus = PublishSubject.create().toSerialized() ;
    }

    public static RxBus getInstance(){
        if (instance == null){
            synchronized (RxBus.class){
                if (instance == null ){
                    instance = new RxBus() ;
                }
            }
        }

        return instance ;
    }

    /**
     * 发送事件
     * @param object
     */
    public void post(Object object){
        mBus.onNext(object);
    }


    /**
     * 接收事件
     */
    public <T>Observable<T> tObservable(final Class<T>  eventType){
        return mBus.ofType(eventType) ;
    }

    /**
     * 判断是否有订阅者
     */
    public boolean hasObservable(){
        return mBus.hasObservers() ;
    }

    /**
     * 充值
     */

    public void reSet(){
        instance = null ;
    }

}
