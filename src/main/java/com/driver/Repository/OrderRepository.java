package com.driver.Repository;

import com.driver.DeliveryPartner;
import com.driver.Order;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class OrderRepository {

    private HashMap<String,Order> orderMap;
    private HashMap<String,DeliveryPartner> deliveryPartnerMap;
    private HashMap<String, String> orderPartnerPairMap;

    private HashMap<String,List<String>> partnerOrderListMap;



    public OrderRepository(){
        orderMap=new HashMap<>();
        deliveryPartnerMap=new HashMap<>();
        orderPartnerPairMap=new HashMap<>();
        partnerOrderListMap=new HashMap<>();
    }


    public void addOrder(Order order){

        orderMap.put(order.getId(),order);
    }

    public void addPartner(String partnerId){

        DeliveryPartner partner=new DeliveryPartner(partnerId);
        deliveryPartnerMap.put(partnerId,partner);
    }

    public void addOrderPartnerPair(String orderId,String partnerId){

        if(partnerOrderListMap.containsKey(partnerId)){
            List<String> orders=partnerOrderListMap.get(partnerId);
            orders.add(orderId);
            partnerOrderListMap.put(partnerId,orders);
        }
        orderPartnerPairMap.put(orderId,partnerId);
    }

    public Order getOrderById(String orderId){
        return orderMap.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId){
        return deliveryPartnerMap.get(partnerId);
    }

    public Integer getOrderCountByPartnerId(String partnerId){
        Integer count=0;
        if(partnerOrderListMap.containsKey(partnerId)){
            List<String> orders=partnerOrderListMap.get(partnerId);
            count=orders.size();
        }
        return count;
    }

    public List<String> getOrdersByPartnerId(String partnerId){

        List<String> orderList=new ArrayList<>();
        if(partnerOrderListMap.containsKey(partnerId)){
            orderList=partnerOrderListMap.get(partnerId);
        }
        return orderList;
    }

    public List<String> getAllOrders(){
        List<String> orders=new ArrayList<>();
        for(String key:orderMap.keySet()){
            orders.add(key);
        }
        return orders;
    }

    public Integer getCountOfUnassignedOrders(){
        Integer unassignedOrders=0;
        for(String key:orderMap.keySet()){
            if(!orderPartnerPairMap.containsKey(key)){
                unassignedOrders++;
            }
        }
        return unassignedOrders;
    }

    public void deleteOrderById(String orderId){
        if(orderMap.containsKey(orderId)){
            orderMap.remove(orderId);
        }
        if(orderPartnerPairMap.containsKey(orderId)){
            String partner=orderPartnerPairMap.get(orderId);
            List<String> orders=partnerOrderListMap.get(partner);
            orders.remove(orderId);
            partnerOrderListMap.put(partner,orders);
            orderPartnerPairMap.remove(orderId);
        }
    }

    public void deletePartnerById(String partnerId){

        List<String> orders=new ArrayList<>();
        if(partnerOrderListMap.containsKey(partnerId)){
            orders=partnerOrderListMap.get(partnerId);
            for(String order:orders){
                orderPartnerPairMap.remove(order);
            }
            partnerOrderListMap.remove(partnerId);
        }

        if(deliveryPartnerMap.containsKey(partnerId)){
            deliveryPartnerMap.remove(partnerId);
        }
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId){

        Integer lastDelivery=0;

        if(partnerOrderListMap.containsKey(partnerId)){
            List<String> ordersList=partnerOrderListMap.get(partnerId);
            for(String order:ordersList){
                Order neworder=orderMap.get(order);
                Integer currTime=neworder.getDeliveryTime();
                lastDelivery=Math.max(lastDelivery,currTime);
            }
        }
        Integer hour=lastDelivery/60;
        Integer min=lastDelivery%60;

        String hourTime=String.valueOf(hour);
        String minTime=String.valueOf(min);
        if(hour<10){
            hourTime="0"+hourTime;
        }
        if(min<10){
            minTime="0"+minTime;
        }
        String time=hourTime+":"+minTime;
        return time;

    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time,String partnerId){
        Integer count=0;

        Integer hour=Integer.valueOf(time.substring(0,2));
        Integer min=Integer.valueOf(time.substring(3));
        Integer timeLimit=hour*60+min;

        if(partnerOrderListMap.containsKey(partnerId)){
            List<String> orders=partnerOrderListMap.get(partnerId);
            for(String order:orders){
                if(orderMap.containsKey(order)){
                    Order currOrder=orderMap.get(order);
                    if(currOrder.getDeliveryTime()>timeLimit){
                        count++;
                    }
                }
            }
        }
        return count;
    }
}
