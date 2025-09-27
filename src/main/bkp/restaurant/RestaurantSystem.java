package com.test;

/*

Restaurant
-restaurantId
-averageRating

Order
-orderIid
-foodItemId
-restaurantId

Rating
-orderId
-rating

SortStrategy
+List<String> top(int count,List<Order> orders)

RatingsObserver
+updateRatings(rating,orders)

FoodItemRatingsObserver
+updateRatings(rating,orders)

RestaurantRatingObserver
+updateRatings(rating,orders)

RatingSystem
+addObserver
+removeObserver
+notifyObserver

+init
+orderFood
+rateOrder
+getTopRestaurantsByFood
+getTopRestaurants

*/

import lombok.Data;

import java.util.*;

@Data
class Restaurant {
    private final String restaurantId;
    private Double rating;
    private Integer numberOfRatings;
    public Restaurant(String restaurantId){
        this.restaurantId = restaurantId;
        this.rating = 0.0;
        this.numberOfRatings = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Restaurant that = (Restaurant) o;
        return Objects.equals(restaurantId, that.restaurantId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(restaurantId);
    }
}

@Data
class Order {
    private final String orderId;
    private final String foodItemId;
    private final String restaurantId;

    public Order(String orderId, String foodItemId, String restaurantId) {
        this.orderId = orderId;
        this.foodItemId = foodItemId;
        this.restaurantId = restaurantId;
    }
}

@Data
class Rating{
    private final Integer rating;
    private final String orderId;

    public Rating(Integer rating, String orderId) {
        this.rating = rating;
        this.orderId = orderId;
    }
}


class SortStrategy implements Comparator<Restaurant> {
    @Override
    public int compare(Restaurant o1, Restaurant o2) {
        if(o2.getRating().compareTo(o1.getRating()) == 0){
            return o1.getRestaurantId().compareTo(o2.getRestaurantId());
        }
        return o2.getRating().compareTo(o1.getRating());
    }
}

interface RatingObserver{
    void updateRating(Rating r);
}

class RestaurantRatingObserver implements RatingObserver{
    private final TreeSet<Restaurant> restaurantTreeSet;
    private final Map<String,Restaurant> restaurantMap;
    private final Map<String,Order> orderMap;

    RestaurantRatingObserver(TreeSet<Restaurant> restaurantTreeSet,Map<String,Restaurant> restaurantMap,Map<String,Order> orderMap){
        this.restaurantTreeSet = restaurantTreeSet;
        this.restaurantMap = restaurantMap;
        this.orderMap = orderMap;
    }

    public void updateRating(Rating r){
        Order order = orderMap.get(r.getOrderId());
        if(order!=null){
            // update restaurant rating
            Restaurant restaurant = restaurantMap.get(order.getRestaurantId());
            if(restaurant.getNumberOfRatings()==0) {
                restaurant.setRating((double)r.getRating());
            }
            else{
                double rating = (restaurant.getRating()*restaurant.getNumberOfRatings()+r.getRating())/(restaurant.getNumberOfRatings()+1);
                Double finalRating = (double)((int)((rating+0.05)*10))/10.0;
                restaurant.setRating(finalRating);
            }
            restaurant.setNumberOfRatings(restaurant.getNumberOfRatings()+1);
            restaurantMap.put(restaurant.getRestaurantId(),restaurant);
            restaurantTreeSet.remove(restaurant);
            restaurantTreeSet.add(restaurant);
            // update restaurant rating for food
        }else {
            System.out.println("Order Invalid for given order : " + r.getOrderId());
        }
    }
}

class RestaurantFoodRatingObserver implements RatingObserver{
    private final Map<String,TreeSet<Restaurant>> restaurantFoodTreeSet;
    private final Map<String,Order> orderMap;
    private final Map<String,Map<String,Restaurant>> foodRestaurantMap;

    RestaurantFoodRatingObserver(Map<String,TreeSet<Restaurant>> restaurantFoodTreeSet,Map<String,Order> orderMap,Map<String,Map<String,Restaurant>> foodRestaurantMap){
        this.restaurantFoodTreeSet = restaurantFoodTreeSet;
        this.orderMap = orderMap;
        this.foodRestaurantMap = foodRestaurantMap;
    }

    public void updateRating(Rating r){
        Order order = orderMap.get(r.getOrderId());
        if(order!=null){
            // update restaurant rating for food
            Map<String,Restaurant> restaurantMap = foodRestaurantMap.computeIfAbsent(order.getFoodItemId(),a -> new HashMap<>());
            Restaurant res = restaurantMap.computeIfAbsent(order.getRestaurantId(),a->new Restaurant(order.getRestaurantId()));
            restaurantFoodTreeSet.putIfAbsent(order.getFoodItemId(),new TreeSet<>(new SortStrategy()));
            restaurantFoodTreeSet.get(order.getFoodItemId()).remove(res);
            if(res.getNumberOfRatings()==0) {
                res.setRating((double)r.getRating());
            }
            else{
                double rating = (res.getRating()*res.getNumberOfRatings()+r.getRating())/(res.getNumberOfRatings()+1);
                Double finalRating = (double)((int)((rating+0.05)*10))/10.0;
                res.setRating(finalRating);
            }
            restaurantMap.put(order.getRestaurantId(),res);
            res.setNumberOfRatings(res.getNumberOfRatings()+1);
            restaurantFoodTreeSet.get(order.getFoodItemId()).add(res);
        }else {
            System.out.println("Order invalid for given order : " + r.getOrderId());
        }
    }
}

interface ObserverSystem{
    public void addObserver(RatingObserver o);
    public void removeObserver(RatingObserver o);
    public void notifyObserver();
}

class RestaurantObserverSystem implements ObserverSystem{
    private final List<RatingObserver> observerList = new ArrayList<>();
    private Rating rating;

   public void updateRating(Rating r){
        this.rating = r;
        notifyObserver();
    }

    public void addObserver(RatingObserver o){
        observerList.add(o);
    }

    public void removeObserver(RatingObserver o){
        observerList.remove(o);
    }

    public void notifyObserver(){
        for(RatingObserver o:observerList){
            o.updateRating(rating);
        }
    }
}

class Helper {
    public void printMessage(String message){
        System.out.println(message);
    }
}

class FoodOrderSystem {

    private final TreeSet<Restaurant> restaurantTreeSet = new TreeSet<>(new SortStrategy());
    private final Map<String,Map<String,Restaurant>> foodRestaurantMap = new HashMap<>();
    private final Map<String,TreeSet<Restaurant>> foodRestaurantTreeSet = new HashMap<>();
    private final Map<String,Order> orderMap = new HashMap<>();
    private final Map<String,Restaurant> restaurantMap = new HashMap<>();
    private Helper helper;
    RestaurantObserverSystem observerSystem = new RestaurantObserverSystem();
    RatingObserver restaurantRatingObserver = new RestaurantRatingObserver(restaurantTreeSet,restaurantMap,orderMap);
    RatingObserver restaurantFoodRatingObserver = new RestaurantFoodRatingObserver(foodRestaurantTreeSet,orderMap,foodRestaurantMap);

    public void init(Helper helper){
       this.helper = helper;
       this.observerSystem.addObserver(restaurantFoodRatingObserver);
       this.observerSystem.addObserver(restaurantRatingObserver);
    }

    public void rateFood(String orderId,int rating){
        Rating r = new Rating(rating,orderId);
        observerSystem.updateRating(r);
    }

    public void orderFood(String orderId,String foodItemId,String restaurantId){
        if(orderMap.containsKey(orderId)){
            System.out.println("Rating already done: "+orderId);
        }
        if(!restaurantMap.containsKey(restaurantId)) {
            restaurantMap.put(restaurantId, new Restaurant(restaurantId));
        }
        orderMap.put(orderId,new Order(orderId,foodItemId,restaurantId));
    }

    public void getTopRestaurants(){
        int s = 0;
        System.out.println("Printing top 20 restaurants");
        for(Restaurant restaurant:restaurantTreeSet){
            if(s>=20)
                break;
            System.out.println("RestaurantId: "+restaurant.getRestaurantId()+" rating: "+restaurant.getRating());
            s++;
        }
    }

    public void getTopRestaurantsByFood(String foodId){
        int s = 0;
        System.out.println("Printing top 20 restaurants");
        for(Restaurant restaurant:foodRestaurantTreeSet.get(foodId)){
            if(s>=20)
                break;
            System.out.println("RestaurantId: "+restaurant.getRestaurantId()+" rating: "+restaurant.getRating());
            s++;
        }
    }

}

public class RestaurantSystem {
    public static void main(String[] args){
        FoodOrderSystem foodOrderSystem = new FoodOrderSystem();
        foodOrderSystem.init(new Helper());
        foodOrderSystem.orderFood("order-0", "food-1", "restaurant-0");
        foodOrderSystem.rateFood("order-0", 3);

        foodOrderSystem.orderFood("order-1", "food-0", "restaurant-2");
        foodOrderSystem.rateFood("order-1", 1);

        foodOrderSystem.orderFood("order-2", "food-0", "restaurant-1");
        foodOrderSystem.rateFood("order-2", 3);

        foodOrderSystem.orderFood("order-3", "food-0", "restaurant-2");
        foodOrderSystem.rateFood("order-3", 5);

        foodOrderSystem.orderFood("order-4", "food-0", "restaurant-0");
        foodOrderSystem.rateFood("order-4", 3);

        foodOrderSystem.orderFood("order-5", "food-0", "restaurant-1");
        foodOrderSystem.rateFood("order-5", 4);

        foodOrderSystem.orderFood("order-6", "food-1", "restaurant-0");
        foodOrderSystem.rateFood("order-6", 2);

        foodOrderSystem.orderFood("order-7", "food-1", "restaurant-0");
        foodOrderSystem.rateFood("order-7", 2);

        foodOrderSystem.orderFood("order-8", "food-0", "restaurant-1");
        foodOrderSystem.rateFood("order-8", 2);

        foodOrderSystem.orderFood("order-9", "food-0", "restaurant-1");
        foodOrderSystem.rateFood("order-9", 4);

        foodOrderSystem.getTopRestaurantsByFood("food-0");
        foodOrderSystem.getTopRestaurantsByFood("food-1");
        foodOrderSystem.getTopRestaurants();

    }
}
